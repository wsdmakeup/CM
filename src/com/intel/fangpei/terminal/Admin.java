package com.intel.fangpei.terminal;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.hbase.util.Bytes;

import com.intel.fangpei.BasicMessage.BasicMessage;
import com.intel.fangpei.BasicMessage.ServiceMessage;
import com.intel.fangpei.BasicMessage.packet;
import com.intel.fangpei.network.HeartBeatThread;
import com.intel.fangpei.network.NIOAdminHandler;
import com.intel.fangpei.network.rpc.RpcClient;
import com.intel.fangpei.util.CommandPhraser;
import com.intel.fangpei.util.ConfManager;
/**
 * start a Admin Proccess.
 * @author fangpei
 *
 */
public class Admin extends Client {
	public static boolean debug = false;
	/*
	 * buffer to buffer the packet of command packet components: [Client Type]
	 * byte [version] int [arg size] int [command] byte [args...] byte[]
	 */
	ByteBuffer buffer = ByteBuffer.allocate(1024);
	String serverip = "";
	int port = 0;
	
	

	public Admin(String serverip, int port) {
		this.serverip = serverip;
		this.port = port;
		this.connect = new NIOAdminHandler(serverip, port);
		
	}

	@Override
	public void run() {
		try {
			
			new Thread(connect).start();
			
			while (true) {
				byte[] b = new byte[1024];
				System.out.print("--->]:");
				System.in.read(b);
				DoCommand(new String(b).trim());
				b = new byte[1024];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// maybe some command need admin to receive server's request.
	private String DoCommand(String command) throws Exception {
		String[] s = null;
		packet one = null;
		byte COMMAND = CommandPhraser.GetUserInputCommand(command);
		if(COMMAND == BasicMessage.OP_HELP){
			printHelp();
		}
		//add tasks command
		if(COMMAND == BasicMessage.TASKS){
			getTasks();
		}
		if(COMMAND == BasicMessage.TASKINFO){
			int task_id = Integer.parseInt(command.substring(command.indexOf(" "),
					command.length()));
			getTaskInfo(task_id);
		}
		if (COMMAND == BasicMessage.OP_EXEC) {
			try{
			one = new packet(BasicMessage.ADMIN, COMMAND,
					command.substring(command.indexOf(" "),
							command.length()).getBytes());
			}catch(IndexOutOfBoundsException e){
				System.out.println("exec [classname]");
				return "";
			}
		}
		if(COMMAND == BasicMessage.OP_SYSINFO){
		one = new packet(BasicMessage.ADMIN,COMMAND);
		}
		if (COMMAND == BasicMessage.OP_CLOSE) {
		one = new packet(BasicMessage.ADMIN, COMMAND);
		}
		if (COMMAND == BasicMessage.OP_QUIT) {
		one = new packet(BasicMessage.ADMIN, COMMAND);
		}
		if (COMMAND == BasicMessage.OP_SH) {
			one = new packet(BasicMessage.ADMIN, COMMAND,
					command.substring(command.indexOf(" "),
							command.length()).getBytes());
		}
		if (COMMAND == ServiceMessage.THREAD||COMMAND == ServiceMessage.SERVICE) {
			try{
			one = new packet(BasicMessage.ADMIN, COMMAND,
					command.substring(command.indexOf(" "),
							command.length()).getBytes());
			}catch(Exception e){
				System.out.println("thread|service args");
				return "";
			}
		}
		if(one == null)
			return "";
		else
		connect.addSendPacket(one);
		return "";// need to return the server's response!
	}

//	private String formString(String[] s) {
//		StringBuilder sb = new StringBuilder();
//		int len = s.length;
//		for (int i = 0; i < len; i++) {
//			sb.append(" " + s[i]);
//		}
//		return sb.toString();
//	}
	public static void printHelp(){
		System.out.println("the command line Admin Usage:");
		System.out.println("exec          execute a class which extend Extender");
		System.out.println("progress      [not complete command,soon...]");
		System.out.println("close         close the cluster's nodes demon");
		System.out.println("quit          close the admin process");
		System.out.println("sysinfo       get the cluster's system info");
		System.out.println("service       execute a class which extend Extender in a Thread");
	}
	public static void getTasks(){
		RpcClient rpcclient = RpcClient.getInstance();
		Object[] params = new Object[]{};
		List<Integer> tasks = (List<Integer>)rpcclient.execute("TaskHandler.getTasks", params);
		if(tasks.size()!=0){
			System.out.print("Tasks: ");
			for(int i=0;i<tasks.size()-1;i++){
				System.out.print(tasks.get(i)+", ");
			}
			System.out.println(tasks.get(tasks.size()-1)+".");
		}else{
			System.out.println("There is no tasks in cluster!");
		}
	}
	public static void getTaskInfo(int task_id){
		RpcClient rpcclient = RpcClient.getInstance();
		Object[] params = new Object[]{task_id};
		Map<Integer,Double> task_info = (Map<Integer,Double>)rpcclient.execute("TaskHandler.getTaskInfo", params);
		Iterator<Entry<Integer,Double>> itr = task_info.entrySet().iterator();
		System.out.println("Task Info of: "+task_id);
		while(itr.hasNext()){
			Entry<Integer, Double> tmp = itr.next();
			int child_tmp = tmp.getKey();
			Double percent_tmp = tmp.getValue()*100;
			System.out.println("Child: "+child_tmp+", complete "+percent_tmp+"%");
		}
		System.out.println("----------");
	}
	public static void main(String[] args) {
		ConfManager.addResource(null);
		String ip = ConfManager.getConf("selectsocket.server.ip");
		if(ip == null){
			System.out.println("not config ip ...exit...");
			System.exit(0);
		}
		int port = ConfManager.getInt("selectsocket.server.port", 1234);
		for (int i = 0; i < 1; i++) {
			Admin c = new Admin(ip, port);
			new Thread(c).start();
		}
	}
}

