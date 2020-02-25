package fr.sii.ogham.email.sender.impl.javamail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.util.ByteArrayDataSource;

import fr.sii.ogham.core.exception.mimetype.MimeTypeDetectionException;
import fr.sii.ogham.core.mimetype.MimeTypeProvider;
import fr.sii.ogham.core.resource.NamedResource;
import fr.sii.ogham.core.resource.Resource;
import fr.sii.ogham.core.util.IOUtils;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.exception.javamail.AttachmentResourceHandlerException;

/**
 * Implementation that is able to handle any {@link Resource}.
 * 
 * @author Aurélien Baudet
 *
 */
public class StreamResourceHandler implements JavaMailAttachmentResourceHandler {
	/**
	 * The Mime Type detector
	 */
	private MimeTypeProvider mimetypeProvider;

	public StreamResourceHandler(MimeTypeProvider mimetypeProvider) {
		super();
		this.mimetypeProvider = mimetypeProvider;
	}

	@Override
	@SuppressWarnings("squid:S1192")
	public void setData(BodyPart part, NamedResource resource, Attachment attachment) throws AttachmentResourceHandlerException {
		try (InputStream stream = resource.getInputStream()) {
			InputStream s = stream;
			// stream is read twice, if stream can't handle reset => create a
			// stream that is able to do it
			if (!stream.markSupported()) {
				s = new ByteArrayInputStream(IOUtils.toByteArray(stream));
			}
			// mark to reset at the start of the stream
			s.mark(Integer.MAX_VALUE);
			// detect the mimetype
			String mimetype = getMimetype(attachment, s);
			// reset the stream
			s.reset();
			// set the content
			part.setDataHandler(new DataHandler(new ByteArrayDataSource(s, mimetype)));
		} catch (MimeTypeDetectionException e) {
			throw new AttachmentResourceHandlerException("Failed to attach " + resource.getName() + ". Mime type can't be detected", attachment, e);
		} catch (MessagingException e) {
			throw new AttachmentResourceHandlerException("Failed to attach " + resource.getName(), attachment, e);
		} catch (IOException e) {
			throw new AttachmentResourceHandlerException("Failed to attach " + resource.getName() + ". Stream can't be read", attachment, e);
		}
	}

	private String getMimetype(Attachment attachment, InputStream stream) throws MimeTypeDetectionException {
		if (attachment.getContentType() != null) {
			return attachment.getContentType();
		}
		return mimetypeProvider.detect(stream).toString();
	}

}
