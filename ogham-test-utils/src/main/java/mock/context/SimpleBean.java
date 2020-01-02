package mock.context;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
		this(name, value, null);
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{\"name\": \"").append(name).append("\", \"value\": ").append(value).append(", \"date\": \"").append(date).append("\"}");
		return builder.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		SimpleBean rhs = (SimpleBean) obj;
		return new EqualsBuilder().append(name, rhs.name).append(value, rhs.value).append(date, rhs.date).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(name).append(value).append(date).hashCode();
	}
}
