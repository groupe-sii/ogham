package fr.sii.notification.core.translator.resource;

import fr.sii.notification.core.resource.Resource;
import fr.sii.notification.email.exception.attachment.translator.ResourceTranslatorException;

/**
 * The aim of a resource translator is to transform a resource into a new one. It
 * may be useful for preparing the attachment of the message before sending it.
 * 
 * @author Aurélien Baudet
 *
 */
public interface AttachmentResourceTranslator {
	/**
	 * Transform the resource into a new one.
	 * 
	 * @param resource
	 *            the resource to transform
	 * @return the transformed resource
	 * @throws ResourceTranslatorException
	 *             when the transformation has failed
	 */
	public Resource translate(Resource resource) throws ResourceTranslatorException;
}
