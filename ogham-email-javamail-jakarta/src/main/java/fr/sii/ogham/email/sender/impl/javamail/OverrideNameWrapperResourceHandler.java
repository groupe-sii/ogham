package fr.sii.ogham.email.sender.impl.javamail;

import java.io.IOException;

import jakarta.mail.BodyPart;

import fr.sii.ogham.core.resource.ByteResource;
import fr.sii.ogham.core.resource.NamedResource;
import fr.sii.ogham.core.resource.OverrideNameWrapper;
import fr.sii.ogham.core.resource.Resource;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.exception.handler.AttachmentResourceHandlerException;

public class OverrideNameWrapperResourceHandler implements JavaMailAttachmentResourceHandler {
	private final JavaMailAttachmentResourceHandler delegate;
	
	public OverrideNameWrapperResourceHandler(JavaMailAttachmentResourceHandler delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public void setData(BodyPart part, NamedResource resource, Attachment attachment) throws AttachmentResourceHandlerException {
		OverrideNameWrapper wrapper = (OverrideNameWrapper) resource;
		Resource wrapped = wrapper.getDelegate();
		// if possible, do not convert to ByteResource
		if (wrapped instanceof NamedResource) {
			delegate.setData(part, (NamedResource) wrapped, attachment);
			return;
		}
		try {
			delegate.setData(part, new ByteResource(wrapper.getName(), wrapper.getInputStream()), attachment);
		} catch (IOException e) {
			throw new AttachmentResourceHandlerException("Failed to read the content of the attachment named "+wrapper.getName(), attachment, e);
		}
	}

}
