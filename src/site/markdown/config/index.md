## Configuration

### Configure using properties

Configuration values can be provided by using builders. The builders can either use manually provided properties or
system properties.

[Read more about using properties &raquo;](properties.html)


---


### Email configuration

Some configuration is possible to customize email management.

[Read more about email configuration &raquo;](email.html)


### SMS configuration

Some configuration is possible to customize SMS management.

[Read more about SMS configuration &raquo;](sms.html)


---

### Template engines configuration

There exists several options for configuring template engines. Some configuration values are common to all template engines. There are also specific configuration values for each template engine.

[Read more about template engine configuration &raquo;](templates.html)


### Select implementation

The library is able to handle many ways to send the same message. For example, sending an email can be done using the following ways:
 
 - Send email through SMTP using Java Mail API
 - Send email through SMTP using Apache Commons Email
 - Send email through SMTP using Spring Email
 - Send email through a SendGrid WebService
 - Send email through any other WebService


The library is using conditions to select the implementation to use at runtime.

[Read more about implementation selection &raquo;](select-implementation.html)
