package com.sn.tonux.mail.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

	@Autowired
	private JavaMailSender mailSender;
	
	private String notificationEmail;

	public JobCompletionNotificationListener(String notificationEmail) {
		this.notificationEmail = notificationEmail;
	}
	
	@Override
	public void afterJob(JobExecution jobExecution) {
		
		SimpleMailMessage message = new SimpleMailMessage();
		
		message.setSubject("Job terminé");
		message.setFrom(notificationEmail);
		message.setTo(notificationEmail);
		message.setText("Job terminé avec " + jobExecution.getExitStatus());

		mailSender.send(message);
	}

	@Override
	public void beforeJob(JobExecution jobExecution) {
		
		SimpleMailMessage message = new SimpleMailMessage();
		
		message.setSubject("Job lancé");
		message.setFrom(notificationEmail);
		message.setTo(notificationEmail);
		message.setText("Job lancé");

		mailSender.send(message);

	}

}
