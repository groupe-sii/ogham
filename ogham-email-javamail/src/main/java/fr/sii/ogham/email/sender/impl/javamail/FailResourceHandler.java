package fr.sii.ogham.email.sender.impl.javamail;

import java.util.function.BiFunction;

import javax.mail.BodyPart;

import fr.sii.ogham.core.resource.NamedResource;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.exception.javamail.AttachmentResourceHandlerException;

/**
 * Handler that fails with a particular exception. This implementation is useful
 * to provide a specific exception/message to help the developer to understand
 * why the attachment can't be handled.
 * 
 * @author Aurélien Baudet
 *
 */
public class FailResourceHandler implements JavaMailAttachmentResourceHandler {
	private final BiFunction<NamedResource, Attachment, AttachmentResourceHandlerException> exceptionProvider;

	public FailResourceHandler(BiFunction<NamedResource, Attachment, AttachmentResourceHandlerException> exceptionProvider) {
		super();
		this.exceptionProvider = exceptionProvider;
	}

	@Override
	public void setData(BodyPart part, NamedResource resource, Attachment attachment) throws AttachmentResourceHandlerException {
		throw exceptionProvider.apply(resource, attachment);
	}

}
