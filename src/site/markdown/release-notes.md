# Release notes

The history of Ogham releases is documented below. For details of changes refer to the [project's GitHub issues][issues] or the [GitHub report][github-report].

[issues]: https://github.com/groupe-sii/ogham/issues?state=closed
[github-report]: github-report.html



## 2.0.0 

### Features

////
TODO: ajouter des liens pour chaque fonctionnalité
////

- New builder API
- Only fluent API (remove all Email/SMS constructors)
- Follow Spring Boot starter conventions
- Better Spring Boot integration with existing module (spring-boot-start-mail, spring-boot-starter-thymeleaf, spring-boot-starter-freemarker, spring-boot-starter-sendgrid)
- Add possibility to create its own configurer

### Internal

- Split projects to separate core from implementations
- Creating a new implementation is easier
- Add annotations to indicate that an implementation needs some preconditions in order to work


## 1.1.0 / 2017-03-14

### Features

- Support FreeMarker templates
- It is possible to mix Thymeleaf and FreeMarker templates
- Tested with Spring Boot 1.3.8+ latest releases (1.3.8, 1.4.5 and 1.5.2)


## 1.0.0 / 2015-10-08

### Features

- [Add Spring integration with Spring Boot](usage/integration.html#integrate-with-spring-boot)
- [Add Spring integration using Java config or XML config](usage/integration.html#manual-integration-with-spring)
- Add fluent API
- Service now wrap all exceptions (even runtime exceptions) into checked exception
- Add OVH implementation for sending SMS
- Add basic i18n support
- Allow to configure different template prefixes/suffixes for each kind of message 


## 0.0.1 / 2015-06-22

### Features

- [APIs for multi-implementation support](config/select-implementation.html)
- [APIs for multi-channel support](usage/index.html)
- [Send emails using Java Mail API (first implementation)](config/select-implementation.html#email)
- [Send SMS using Cloudhopper SMPP (first implementation)](config/select-implementation.html#sms)
- Integrate Thymeleaf as template engine (first integration)
- [Manage both html and text emails](usage/how-to-send-email.html#both-html-and-text)
- [Automatically fill email/sms with information from properties](config/properties.html)
- [Automatically fill subject for emails provided directly in template](usage/how-to-send-email.html#sending-email-with-subject-from-template)
- [Handle email attachments](usage/how-to-send-email.html#attachments)
- [Apply basic HTML transformations to make the email work on several email clients](features/hidden-complexity.html)
- Detect mimetype automatically
- Simple lookup management for finding templates and template resources