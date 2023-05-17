package fr.sii.ogham.sample.standard;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.service.MessagingService;

public class DefaultPropertiesLocationSample {
  public static void main(String[] args) throws MessagingException {
    /**
     * By default, Ogham loads files from:
     * - config/ogham.properties and config/application.properties
     *   from classpath if exists
     * - config/ogham.properties and config/application.properties
     *   external files from current directory if exists
     */
    MessagingService service = MessagingBuilder.standard()
        .build();
    service.send(/*your message here*/null);
  }
}
