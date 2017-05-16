package fr.sii.ogham.sms.builder.cloudhopper;

import java.util.Arrays;
import java.util.List;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.RetryBuilder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.util.BuilderUtils;

public class SessionBuilder extends AbstractParent<CloudhopperBuilder> implements Builder<CloudhopperSessionOptions> {
	private EnvironmentBuilder<?> environmentBuilder;
	private ValueOrProperties<Long> bind;
	private ValueOrProperties<Long> connect;
	private ValueOrProperties<Long> requestExpiry;
	private ValueOrProperties<Long> windowMonitorInverval;
	private ValueOrProperties<Long> windowWait;
	private ValueOrProperties<Integer> windowSize;
	private ValueOrProperties<Long> write;
	private ValueOrProperties<Long> response;
	private ValueOrProperties<Long> unbind;
	private RetryBuilder<SessionBuilder> connectRetryBuilder;
	
	public SessionBuilder(CloudhopperBuilder parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		this.environmentBuilder = environmentBuilder;
		bind = new ValueOrProperties<>();
		connect = new ValueOrProperties<>();
		requestExpiry = new ValueOrProperties<>();
		windowMonitorInverval = new ValueOrProperties<>();
		windowWait = new ValueOrProperties<>();
		windowSize = new ValueOrProperties<>();
		write = new ValueOrProperties<>();
		response = new ValueOrProperties<>();
		unbind = new ValueOrProperties<>();
	}

	public SessionBuilder bindTimeout(long timeout) {
		bind.setValue(timeout);
		return this;
	}
	
	public SessionBuilder bindTimeout(String... timeout) {
		bind.setProperties(timeout);
		return this;
	}

	public SessionBuilder connectTimeout(long timeout) {
		connect.setValue(timeout);
		return this;
	}
	
	public SessionBuilder connectTimeout(String... timeout) {
		connect.setProperties(timeout);
		return this;
	}
	
	public RetryBuilder<SessionBuilder> connectRetry() {
		if(connectRetryBuilder==null) {
			connectRetryBuilder = new RetryBuilder<>(this, environmentBuilder);
		}
		return connectRetryBuilder;
	}

	public SessionBuilder requestExpiryTimeout(long timeout) {
		requestExpiry.setValue(timeout);
		return this;
	}
	
	public SessionBuilder requestExpiryTimeout(String... timeout) {
		requestExpiry.setProperties(timeout);
		return this;
	}

	public SessionBuilder windowMonitorInterval(long interval) {
		windowMonitorInverval.setValue(interval);
		return this;
	}
	
	public SessionBuilder windowMonitorInterval(String... interval) {
		windowMonitorInverval.setProperties(interval);
		return this;
	}

	public SessionBuilder windowSize(int size) {
		windowSize.setValue(size);
		return this;
	}
	
	public SessionBuilder windowSize(String... size) {
		windowSize.setProperties(size);
		return this;
	}

	public SessionBuilder windowWait(long windowWait) {
		this.windowWait.setValue(windowWait);
		return this;
	}
	
	public SessionBuilder windowWait(String... windowWait) {
		this.windowWait.setProperties(windowWait);
		return this;
	}

	public SessionBuilder writeTimeout(long timeout) {
		write.setValue(timeout);
		return this;
	}
	
	public SessionBuilder writeTimeout(String... timeout) {
		write.setProperties(timeout);
		return this;
	}

	public SessionBuilder responseTimeout(long timeout) {
		response.setValue(timeout);
		return this;
	}
	
	public SessionBuilder responseTimeout(String... timeout) {
		response.setProperties(timeout);
		return this;
	}

	public SessionBuilder unbindTimeout(long timeout) {
		unbind.setValue(timeout);
		return this;
	}
	
	public SessionBuilder unbindTimeout(String... timeout) {
		unbind.setProperties(timeout);
		return this;
	}

	@Override
	public CloudhopperSessionOptions build() throws BuildException {
		CloudhopperSessionOptions sessionOpts = new CloudhopperSessionOptions();
		PropertyResolver propertyResolver = environmentBuilder.build();
		sessionOpts.setBindTimeout(getValue(propertyResolver, bind));
		sessionOpts.setConnectTimeout(getValue(propertyResolver, connect));
		sessionOpts.setRequestExpiryTimeout(getValue(propertyResolver, requestExpiry));
		sessionOpts.setWindowMonitorInterval(getValue(propertyResolver, windowMonitorInverval));
		sessionOpts.setWindowSize(getValue(propertyResolver, windowSize, Integer.class));
		sessionOpts.setWindowWaitTimeout(getValue(propertyResolver, windowWait));
		sessionOpts.setWriteTimeout(getValue(propertyResolver, write));
		sessionOpts.setResponseTimeout(getValue(propertyResolver, response));
		sessionOpts.setUnbindTimeout(getValue(propertyResolver, unbind));
		if(connectRetryBuilder!=null) {
			sessionOpts.setConnectRetry(connectRetryBuilder.build());
		}
		return sessionOpts;
	}
	
	private Long getValue(PropertyResolver propertyResolver, ValueOrProperties<Long> val) {
		return getValue(propertyResolver, val, Long.class);
	}
	
	private <T> T getValue(PropertyResolver propertyResolver, ValueOrProperties<T> val, Class<T> targetType) {
		if(val.getValue()!=null) {
			return val.getValue();
		}
		if(val.getProperties()==null) {
			return null;
		}
		return BuilderUtils.evaluate(val.getProperties(), propertyResolver, targetType);
	}
	
	private static class ValueOrProperties<V> {
		private V value;
		private List<String> properties;
		public V getValue() {
			return value;
		}
		public void setValue(V value) {
			this.value = value;
		}
		public List<String> getProperties() {
			return properties;
		}
		public void setProperties(List<String> properties) {
			this.properties = properties;
		}
		public void setProperties(String... properties) {
			setProperties(Arrays.asList(properties));
		}
	}
}
