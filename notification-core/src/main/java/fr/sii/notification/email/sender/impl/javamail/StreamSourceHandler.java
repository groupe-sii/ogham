package fr.sii.notification.email.sender.impl.javamail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.transform.stream.StreamSource;

import fr.sii.notification.core.exception.mimetype.MimeTypeDetectionException;
import fr.sii.notification.core.mimetype.MimeTypeProvider;
import fr.sii.notification.core.util.IOUtils;
import fr.sii.notification.email.attachment.Attachment;
import fr.sii.notification.email.attachment.ByteSource;
import fr.sii.notification.email.attachment.Source;
import fr.sii.notification.email.exception.javamail.AttachmentSourceHandlerException;

/**
 * Implementation that is able to handle {@link StreamSource}.
 * 
 * @author Aurélien Baudet
 *
 */
public class StreamSourceHandler implements JavaMailAttachmentSourceHandler {
	/**
	 * The Mime Type detector
	 */
	private MimeTypeProvider mimetypeProvider;

	public StreamSourceHandler(MimeTypeProvider mimetypeProvider) {
		super();
		this.mimetypeProvider = mimetypeProvider;
	}

	@Override
	public void setData(BodyPart part, Source source, Attachment attachment) throws AttachmentSourceHandlerException {
		ByteSource streamSource = (ByteSource) source;
		try (InputStream stream = streamSource.getStream()) {
			InputStream s = stream;
			// stream is read twice, if stream can't handle reset => create a
			// stream that is able to do it
			if (!stream.markSupported()) {
				s = new ByteArrayInputStream(IOUtils.toByteArray(stream));
			}
			// mark to reset at the start of the stream
			s.mark(Integer.MAX_VALUE);
			// detect the mimetype
			String mimetype = mimetypeProvider.detect(s).toString();
			// reset the stream
			s.reset();
			// set the content
			part.setDataHandler(new DataHandler(new ByteArrayDataSource(s, mimetype)));
		} catch (MimeTypeDetectionException e) {
			throw new AttachmentSourceHandlerException("Failed to attach " + source.getName() + ". Mime type can't be detected", attachment, e);
		} catch (MessagingException e) {
			throw new AttachmentSourceHandlerException("Failed to attach " + source.getName(), attachment, e);
		} catch (IOException e) {
			throw new AttachmentSourceHandlerException("Failed to attach " + source.getName() + ". Stream can't be read", attachment, e);
		}
	}

}
