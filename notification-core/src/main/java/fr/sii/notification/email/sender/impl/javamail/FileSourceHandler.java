package fr.sii.notification.email.sender.impl.javamail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.util.ByteArrayDataSource;

import fr.sii.notification.core.exception.mimetype.MimeTypeDetectionException;
import fr.sii.notification.core.mimetype.MimeTypeProvider;
import fr.sii.notification.email.attachment.Attachment;
import fr.sii.notification.email.attachment.FileSource;
import fr.sii.notification.email.attachment.Source;
import fr.sii.notification.email.exception.javamail.AttachmentSourceHandlerException;

public class FileSourceHandler implements JavaMailAttachmentSourceHandler {
	/**
	 * The Mime Type detector
	 */
	private MimeTypeProvider mimetypeProvider;

	public FileSourceHandler(MimeTypeProvider mimetypeProvider) {
		super();
		this.mimetypeProvider = mimetypeProvider;
	}

	@Override
	public void setData(BodyPart part, Source source, Attachment attachment) throws AttachmentSourceHandlerException {
		try {
			FileSource fileSource = (FileSource) source;
			part.setDataHandler(new DataHandler(new ByteArrayDataSource(new FileInputStream(fileSource.getFile()), mimetypeProvider.getMimeType(fileSource.getFile()).toString())));
		} catch (MimeTypeDetectionException e) {
			throw new AttachmentSourceHandlerException("Failed to attach " + source.getName() + ". Mime type can't be detected", attachment, e);
		} catch (FileNotFoundException e) {
			throw new AttachmentSourceHandlerException("Failed to attach " + source.getName() + ". File doesn't exists", attachment, e);
		} catch (MessagingException e) {
			throw new AttachmentSourceHandlerException("Failed to attach " + source.getName(), attachment, e);
		} catch (IOException e) {
			throw new AttachmentSourceHandlerException("Failed to attach " + source.getName() + ". File can't be read", attachment, e);
		}
	}

}
