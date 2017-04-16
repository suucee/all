package com.maxivetech.backoffice.task;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.maxivetech.backoffice.BackOffice;
import com.maxivetech.backoffice.util.HelperHttp;

@Component("taskGetRate")
public class TaskGetRate {

	public TaskGetRate() {
	}

	@Scheduled(cron = "0 0/5 * * * ?")
	public void getRate() {
		HelperHttp.doPost(BackOffice.getInst().LOCAL_CONTROLLER_ROOT + "/getRate.do");
	}
}
