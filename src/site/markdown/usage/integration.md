## Integration

Current version: ${project.version}.

### Maven dependency

To use Ogham, add it to your pom.xml:

```xml
  ...
	<dependencies>
	  ...
		<dependency>
			<groupId>fr.sii.ogham</groupId>
			<artifactId>ogham-core</artifactId>
			<version>${ogham-module.version}</version>
		</dependency>
		...
	</dependencies>
	...
```

See [how to use the module](usage/index.html).

### Integrate with Spring Boot

To use Ogham and let Spring Boot auto-configuration mechanism handle integration, just add dependency to your pom.xml:

```xml
  ...
	<dependencies>
	  ...
		<dependency>
			<groupId>fr.sii.ogham</groupId>
			<artifactId>ogham-spring</artifactId>
			<version>${ogham-module.version}</version>
		</dependency>
		...
	</dependencies>
	...
```

It will automatically create and register MessagingService bean. It will also automatically use Spring Thymeleaf integration if it is in the classpath.
It will also automatically use the Spring environment (configuration provided by configuration files for example) and use it for configuring the module.

Then to use it in your code, simply autowire the service:

```java
	public class UserService {
		@Autowired
		MessagingService messagingService;
		
		public User register(UserDTO user) {
			...
			messagingService.send(new Email("account registered", "email content", user.getEmailAddress()));
			...
		}
	}
```

See [how to use the module with Spring](usage/index.html#).

### Manual integration with Spring

To use Ogham without Spring Boot, you have to first add the dependency to your pom.xml:

```xml
  ...
	<dependencies>
	  ...
		<dependency>
			<groupId>fr.sii.ogham</groupId>
			<artifactId>ogham-spring</artifactId>
			<version>${ogham-module.version}</version>
		</dependency>
		...
	</dependencies>
	...
```

Then to use it in your code, simply autowire the service:

```java
	public class UserService {
		@Autowired
		MessagingService messagingService;
		
		public User register(UserDTO user) {
			...
			messagingService.send(new Email("account registered", "email content", user.getEmailAddress()));
			...
		}
	}
```


#### Integration including Thymeleaf in Java

If you want to use Thymeleaf provided by Spring MVC, you have to add the following configuration:

```java
	@Configuration
	public static class DefaultConfiguration {
		@Autowired
		Environment environment;
		
		@Bean
		public MessagingService messagingService(SpringTemplateEngine engine) {
			MessagingBuilder builder = new MessagingBuilder().useAllDefaults(new PropertiesBridge().convert(environment));
			builder.getEmailBuilder().getTemplateBuilder().getThymeleafParser().withTemplateEngine(engine);
			builder.getSmsBuilder().getTemplateBuilder().getThymeleafParser().withTemplateEngine(engine);
			return builder.build();
		}
	}
```


#### Independent integration (standalone Thymeleaf) in Java

If you want to use Thymeleaf provided by Ogham (independent from Spring), you have to add the following configuration:

```java
	@Configuration
	public static class DefaultConfiguration {
		@Autowired
		Environment environment;
		
		@Bean
		public MessagingService messagingService() {
			return new MessagingBuilder().useAllDefaults(new PropertiesBridge().convert(environment)).build();
		}
	}
```

#### Integration including Thymeleaf in XML

#### Independent integration in XML

```xml
	<bean id="messagingService">
```
