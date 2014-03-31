package com.intel.fangpei.network;


import com.intel.fangpei.BasicMessage.BasicMessage;
import com.intel.fangpei.BasicMessage.HeartBeatMessage;
import com.intel.fangpei.BasicMessage.packet;

public class HeartBeatThread implements Runnable{
	
	public  static int HEART_BEAT_INTERVAL = 5;
	public static int HEART_BEAT_OUT_TIME = 10;
	private NIOHandler connect;
	public HeartBeatThread(NIOHandler connect){
		
		this.connect=connect;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try {
			while(true){
			packet heart_beat = new packet(BasicMessage.ADMIN, HeartBeatMessage.HEART_BEAT_BEGIN);
			connect.addSendPacket(heart_beat);
			Thread.sleep(HEART_BEAT_INTERVAL*1000);
			packet heart_beat_check = new packet(BasicMessage.ADMIN, HeartBeatMessage.HEART_BEAT_CHECK);
			connect.addSendPacket(heart_beat_check);
			//System.out.println("one heart beat done!");
		}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
