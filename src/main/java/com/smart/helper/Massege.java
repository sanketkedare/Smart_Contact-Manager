package com.smart.helper;

public class Massege 
{
	private String content;
	private String type;
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Massege(String content, String type) {
		super();
		this.content = content;
		this.type = type;
	}
	public Massege() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return "Massege [content=" + content + ", type=" + type + "]";
	}
	

}
