package com.maxivetech.backoffice.task;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.maxivetech.backoffice.BackOffice;
import com.maxivetech.backoffice.util.HelperHttp;

@Component("taskSyncMT4")
public class TaskSyncMT4 {

	public TaskSyncMT4() {
	}

	@Scheduled(cron = "0 0/5 * * * ?")
	public void syncAll() {
		HelperHttp.doPost(BackOffice.getInst().LOCAL_CONTROLLER_ROOT + "/syncAll.do");
	}
}
