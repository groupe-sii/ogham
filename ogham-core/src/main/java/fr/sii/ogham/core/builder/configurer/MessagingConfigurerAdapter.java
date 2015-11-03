package fr.sii.ogham.core.builder.configurer;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.builder.mimetype.MimetypeDetectionBuilder;
import fr.sii.ogham.core.builder.resolution.ResourceResolutionBuilder;
import fr.sii.ogham.email.builder.EmailBuilder;
import fr.sii.ogham.sms.builder.SmsBuilder;

public abstract class MessagingConfigurerAdapter implements MessagingConfigurer {

	@Override
	public void configure(MessagingBuilder builder) {
		configure(builder.environment());
		configure(builder.resource());
		configure(builder.mimetype());
		configure(builder.email());
		configure(builder.sms());
	}
	
	public void configure(EnvironmentBuilder<?> builder) {
		
	}

	public void configure(ResourceResolutionBuilder<?> builder) {
		
	}

	public void configure(MimetypeDetectionBuilder<?> mimetype) {
		
	}

	public void configure(EmailBuilder builder) {
		
	}
	
	public void configure(SmsBuilder builder) {
		
	}
	
}
