package com.intel.fangpei.network.rpc;

import java.util.List;
import java.util.Map;

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
	public boolean isTaskComplete(int task_id){
		
		return stm.isComplete(task_id);
	}
	public List<Integer> getTasks(){
		return stm.getTasks();
	}
	public Map<Integer,Double> getTaskInfo(int task_id){
		return stm.getTaskInfo(task_id);
	}
}
