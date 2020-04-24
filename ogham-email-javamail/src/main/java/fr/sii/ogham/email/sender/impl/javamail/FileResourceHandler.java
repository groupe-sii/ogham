package fr.sii.ogham.email.sender.impl.javamail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.util.ByteArrayDataSource;

import fr.sii.ogham.core.exception.mimetype.MimeTypeDetectionException;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;
import fr.sii.ogham.core.resource.FileResource;
import fr.sii.ogham.core.resource.NamedResource;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.exception.handler.AttachmentResourceHandlerException;

/**
 * Specific implementation for files so that mimetype detection may also use the
 * file name to guess the mimetype.
 * 
 * @author Aurélien Baudet
 *
 */
public class FileResourceHandler implements JavaMailAttachmentResourceHandler {
	private static final String ERROR_MESSAGE_PREFIX = "Failed to attach '";

	/**
	 * The Mime Type detector
	 */
	private MimeTypeProvider mimetypeProvider;

	public FileResourceHandler(MimeTypeProvider mimetypeProvider) {
		super();
		this.mimetypeProvider = mimetypeProvider;
	}

	@Override
	public void setData(BodyPart part, NamedResource resource, Attachment attachment) throws AttachmentResourceHandlerException {
		FileResource fileResource = (FileResource) resource;
		try (FileInputStream fis = new FileInputStream(fileResource.getFile())) {
			part.setDataHandler(new DataHandler(new ByteArrayDataSource(fis, getMimetype(attachment, fileResource))));
		} catch (MimeTypeDetectionException e) {
			throw new AttachmentResourceHandlerException(ERROR_MESSAGE_PREFIX + resource.getName() + "'. Mime type can't be detected", attachment, e);
		} catch (FileNotFoundException e) {
			throw new AttachmentResourceHandlerException(ERROR_MESSAGE_PREFIX + resource.getName() + "'. File doesn't exists", attachment, e);
		} catch (MessagingException e) {
			throw new AttachmentResourceHandlerException(ERROR_MESSAGE_PREFIX + resource.getName() + "'", attachment, e);
		} catch (IOException e) {
			throw new AttachmentResourceHandlerException(ERROR_MESSAGE_PREFIX + resource.getName() + "'. File can't be read", attachment, e);
		}
	}

	private String getMimetype(Attachment attachment, FileResource fileResource) throws MimeTypeDetectionException {
		if (attachment.getContentType() != null) {
			return attachment.getContentType();
		}
		return mimetypeProvider.getMimeType(fileResource.getFile()).toString();
	}

}
