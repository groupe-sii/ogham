# Release notes


[issues]: https://github.com/groupe-sii/ogham/issues?state=closed
[github-report]: github-report.html



## 1.0.0 / 2015-10-07

### Features

- Add Spring integration with Spring Boot
- Add fluent API
- Service now wrap all exceptions (even runtime exceptions) into checked exception
- Add OVH implementation for sending SMS
- Add basic i18n support


## 0.0.1 / 2015-06-22

### Features

- APIs for multi-implementation support
- APIs for multi-channel support
- Send emails using Java Mail API (first implementation)
- Send SMS using Cloudhopper SMPP (first implementation)
- Integrate Thymeleaf as template engine (first integration)
- Manage both html and text emails
- Automatically fill email/sms with information from properties
- Automatically fill subject for emails provided directly in template
- Handle email attachments
- Apply basic HTML transformations to make the email work on several email clients
- Detect mimetype automatically
- Simple lookup management for finding templates and template resources