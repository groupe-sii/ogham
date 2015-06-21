## Use properties

Configuration values can be provided by using builders. The builders can either use manually provided properties or
system properties.

### Manual properties

You can set properties manually like this:

```java
	public void init() {
		Properties properties = new Properties();
		properties.setProperty("<property 1 key to set>", "<property value>");
		properties.setProperty("<property 2 key to set>", "<property value>");
		// Instantiate the messaging service using default behavior and
		// provided properties
		MessagingService service = new MessagingBuilder().useAllDefaults(properties).build();
	}
```

This will configure the messaging service using your custom properties.

### Properties in a file

You can put properties in file. For example, you can create a file named `messaging.properties`:

```ini
property1.key.to.set=<property value>
property2.key.to.set=<property value>
```

Then you can load this file in your code:

```java
	public void init() {
		Properties properties = new Properties();
		props.load(getClass().getResourceAsStream("/messaging.properties"));
		// Instantiate the messaging service using default behavior and
		// provided properties
		MessagingService service = new MessagingBuilder().useAllDefaults(properties).build();
	}
```

This will configure the messaging service using your custom properties.

### System properties

You can directly use System properties using the builder `useDefault` variant:

```java
	public void init() {
		// Instantiate the messaging service using default behavior and
		// system properties
		MessagingService service = new MessagingBuilder().useAllDefaults().build();
	}
```

The library will automatically use the System properties if none is specified.

### File and System properties

In order to manage both properties that come from file and properties that come from System properties, you can create a file named for example `messaging.properties`:

```java
property1.key.to.set=<property value>
property2.key.to.set=<property value>
```

Then you can load this file in your code with System properties as backup:

```java
	public void init() {
		Properties properties = new Properties(System.getProperties());
		props.load(getClass().getResourceAsStream("/messaging.properties"));
		// Instantiate the messaging service using default behavior and
		// provided properties
		MessagingService service = new MessagingBuilder().useAllDefaults(properties).build();
	}
```

This will configure the messaging service using your custom properties first and if a property is not found in file, the property will be searched in System properties.
