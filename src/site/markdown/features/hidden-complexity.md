## Hidden complexity

One of the main aim of the library is to hide the implementation complexity. It provides automatic behaviors to simplify the development and focus on useful code.


### Email

#### Mime Type detection

When you send an email, it is really important to indicate what is the type of the message (html, text or maybe anything else). The library automatically detects for you the [Mime Type](https://en.wikipedia.org/wiki/Internet_media_type) of the email content and add this information into the real sent mail for you.

Moreover, every attached file must also provide the Mime Type in order to be correctly handled by the email client. Besides, some email clients can provide a preview of attached file. This can be possible only by providing the right Mime Type. The library will detect the Mime Type for every attached file for you.

#### Working with HTML content

To be sure that most of Email clients will handle an HTML content, there are many rules to follow when writing the content. They might be very complex and time consuming. The library do all this headache work for you.

All these features can be either disabled or use another implementation instead of the default one.

##### Inline CSS and images

For Web developers, it is important to write clean code and separate the concerns. So when writing HTML, developers want to externalize CSS files and images. This is also really important to mutualize the code of CSS files and images for reuse.

However, email clients doesn't handle external CSS files. Styles can be included in a `style` tag but Gmail doesn't support it. So all rules provided in the CSS *MUST* be inlined directly in the HTML. Writing code like this is just awful and error prone. Moreover, images can be references externally but there are many constraints to use it this way. You have to know in advance what is the URL of the final image. And even then, the email client might block those images for safety purpose. Not mentioning offline issues.

The library will automatically inlines CSS rules directly on the HTML tags. The images are either inlined as base64 encoded in `img` `src` attribute or images are attached with the email (with inline content disposition and references in the HTML).

##### Use expanded CSS properties

CSS properties can be written using shorthand version:
```css
padding: 4px 2px;
```
This is equivalent to:
```css
padding: 4px 2px 4px 2px;
```

Some email clients do not understand shorthand properties. So all properties written in shorthand version must be expanded in order to work everywhere.

The library will automatically expand properties from shorthand versions.

##### Add extra attributes for old email clients

Several attributes must be added on some HTML tags in order to be compliant with email clients. For example, tables and images must have attribute `border="0"`in order to prevent an ugly border on some clients.

The library will automatically add these attributes.

##### Use XHTML

It is recommended to write XHTML instead of HTML due to some mail clients. The library do it for you.

##### Background images

Background images are not correctly handled by several mail clients. And again, some workarounds to apply to fix this issue.

The library will apply those workarounds to your HTML.

##### Use tables for layouts

TODO

### SMS

There are some implementation constraints for sending a SMS following SMPP protocol. The library will help you handle these constraints.

#### Phone number TON and NPI policy

SMPP defines two main properties related to the sender and recipient phone number formats: [TON and NPI](https://docs.aerialink.net/api/smpp/ton-npi-settings/).

The library provides a default policy : Just pass the phone numbers and the library will guess the TON and NPI. You can also directly provide the TON and NPI in order to prevent this automatic behavior.

#### Character encoding

Sending SMS with the wrong charset might cause your message to be unreadable. But SMPP Charset are quite different from the common charset definition. The charset resolution is done using a simple mapping between Java charsets and SMPP charsets. By default, Java uses UTF-8 charset and the librairy will map to the SMPP charset [GSM](https://en.wikipedia.org/wiki/GSM_03.38). The library offers you the ability to provide your own charset when sending SMS if need.
