package com.intel.fangpei.network.rpc;

import com.intel.fangpei.terminalmanager.AdminManager;

public class TaskChildHandler {
	private AdminManager.ServerTaskMonitor stm;
	public TaskChildHandler(){
		stm  = AdminManager.getServerTaskMonitorInstance();
	}
	
	public boolean registeChild(int task_id, int child_id, double percent){
		stm.complete(task_id, child_id, percent);
		return true;
	}
}
