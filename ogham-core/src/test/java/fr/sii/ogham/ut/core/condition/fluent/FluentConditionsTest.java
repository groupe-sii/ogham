package fr.sii.ogham.ut.core.condition.fluent;

import static fr.sii.ogham.core.condition.fluent.MessageConditions.$;
import static fr.sii.ogham.core.condition.fluent.MessageConditions.requiredProperty;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import fr.sii.ogham.core.condition.Condition;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.helper.rule.LoggingTestRule;

public class FluentConditionsTest {
	@Rule
	public final LoggingTestRule loggingRule = new LoggingTestRule();
	
	@Rule
	public final MockitoRule mockito = MockitoJUnit.rule();

	@Mock
	PropertyResolver propertyResolver;
	
	@Test
	public void requiredMailHostOrMailSmtpHostProperties() {
		when(propertyResolver.containsProperty("mail.host")).thenReturn(false, true, false, true);
		when(propertyResolver.containsProperty("mail.smtp.host")).thenReturn(false, /*false, */true/*, true*/);
		Condition<Message> condition = requiredProperty(propertyResolver, "mail.host")
				.or(requiredProperty(propertyResolver, "mail.smtp.host"));
		assertThat(condition.accept(any(Message.class)), is(false));
		assertThat(condition.accept(any(Message.class)), is(true));
		assertThat(condition.accept(any(Message.class)), is(true));
		assertThat(condition.accept(any(Message.class)), is(true));
	}
	
	@Test
	public void requiredMailHostAndMailPortProperties() {
		when(propertyResolver.containsProperty("mail.host")).thenReturn(false, true, false, true);
		when(propertyResolver.containsProperty("mail.port")).thenReturn(/*false, */false/*, true*/, true);
		Condition<Message> condition = requiredProperty(propertyResolver, "mail.host")
				.and(requiredProperty(propertyResolver, "mail.port"));
		assertThat(condition.accept(any(Message.class)), is(false));
		assertThat(condition.accept(any(Message.class)), is(false));
		assertThat(condition.accept(any(Message.class)), is(false));
		assertThat(condition.accept(any(Message.class)), is(true));
	}
	
	@Test
	public void precedence() {
		when(propertyResolver.containsProperty("mail.host")).thenReturn(		false, 		true, 		false, 		true, 		false, 		true, 		false, 		true, 		false, 		true, 		false, 		true, 		false, 		true, 		false, 		true);
		when(propertyResolver.containsProperty("mail.smtp.host")).thenReturn(	false, 		/*false, */	true		/*, true*/, false, 		/*false, */	true		/*, true*/,	false, 		/*false, */	true		/*, true*/, false, 		/*false, */	true		/*, true*/);
		when(propertyResolver.containsProperty("mail.port")).thenReturn(		/*false, */	false, 		false, 		false, 		/*true,*/ 	true, 		true, 		true, 		false, 		false, 		false, 		false, 		true, 		true, 		true, 		true);
		when(propertyResolver.containsProperty("mail.smtp.port")).thenReturn(	/*false, */	false,		false, 		false, 		/*false,*/	/*false,*/	/*false,*/	/*false,*/	true, 		true, 		true, 		true 		/*,true,*/	/*true,*/	/*true,*/	/*true*/);
		Condition<Message> condition = $(requiredProperty(propertyResolver, "mail.host").or(requiredProperty(propertyResolver, "mail.smtp.host")))
					.and(requiredProperty(propertyResolver, "mail.port").or(requiredProperty(propertyResolver, "mail.smtp.port")));
		assertThat(condition.accept(any(Message.class)), is(false));
		assertThat(condition.accept(any(Message.class)), is(false));
		assertThat(condition.accept(any(Message.class)), is(false));
		assertThat(condition.accept(any(Message.class)), is(false));
		
		assertThat(condition.accept(any(Message.class)), is(false));
		assertThat(condition.accept(any(Message.class)), is(true));
		assertThat(condition.accept(any(Message.class)), is(true));
		assertThat(condition.accept(any(Message.class)), is(true));
		
		assertThat(condition.accept(any(Message.class)), is(false));
		assertThat(condition.accept(any(Message.class)), is(true));
		assertThat(condition.accept(any(Message.class)), is(true));
		assertThat(condition.accept(any(Message.class)), is(true));
		
		assertThat(condition.accept(any(Message.class)), is(false));
		assertThat(condition.accept(any(Message.class)), is(true));
		assertThat(condition.accept(any(Message.class)), is(true));
		assertThat(condition.accept(any(Message.class)), is(true));
	}
}
