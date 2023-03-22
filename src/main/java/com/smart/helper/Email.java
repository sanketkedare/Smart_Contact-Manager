package com.smart.helper;

public class Email 
{
	private String to;
	private String subject;
	private String massage;

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMassage() {
		return massage;
	}

	public void setMassage(String massage) {
		this.massage = massage;
	}

	public Email(String to, String subject, String massage) {
		super();
		this.to = to;
		this.subject = subject;
		this.massage = massage;
	}

	public Email() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "Email [to=" + to + ", subject=" + subject + ", massage=" + massage + "]";
	}
	

}
