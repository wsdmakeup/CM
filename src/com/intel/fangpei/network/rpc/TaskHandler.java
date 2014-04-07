package com.intel.fangpei.network.rpc;

import com.intel.fangpei.terminalmanager.AdminManager;

public class TaskHandler {
	private AdminManager.ServerTaskMonitor stm;
	public TaskHandler(){
		stm  = AdminManager.getServerTaskMonitorInstance();
	}
	
	public boolean registeTask(int task_id){
		stm.newTask(task_id);
		return true;
	}
	public int getTaskCompleteNum(int task_id){
		return stm.taskCompleteNum(task_id);
	}
}
