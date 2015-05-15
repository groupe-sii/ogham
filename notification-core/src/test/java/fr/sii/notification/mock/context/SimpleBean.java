package fr.sii.notification.mock.context;

import java.util.Date;

public class SimpleBean {
	private String name;
	
	private int value;
	
	private Date date;

	public SimpleBean(String name, int value, Date date) {
		super();
		this.name = name;
		this.value = value;
		this.date = date;
	}

	public SimpleBean(String name, int value) {
		this(name, value, new Date());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
