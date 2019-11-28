package fr.sii.ogham.sms.splitter;

import java.util.List;

import fr.sii.ogham.sms.exception.message.SplitMessageException;

/**
 * If the message is too long and can't fit in a single SMS, the message has to
 * be split into several segments.
 * 
 * <p>
 * If the whole message can fit in a single SMS, the result is a list of one
 * segment with the whole message.
 * 
 * <p>
 * If the whole message can't fit in a single SMS, the message is split into
 * several segments. Each segment contains a part of the message. In addition to
 * part of the message, the segment may contain some bytes that are used as a
 * header (see <a href="https://en.wikipedia.org/wiki/User_Data_Header">User
 * Data Header</a>).
 * 
 * @author Aurélien Baudet
 *
 */
public interface MessageSplitter {
	/**
	 * If the message is too long and can't fit in a single SMS, the message has
	 * to be split into several segments.
	 * 
	 * <p>
	 * If the whole message can fit in a single SMS, the result is a list of one
	 * segment with the whole message.
	 * 
	 * <p>
	 * If the whole message can't fit in a single SMS, the message is split into
	 * several segments. Each segment contains a part of the message. In
	 * addition to part of the message, the segment may contain some bytes that
	 * are used as a header (see
	 * <a href="https://en.wikipedia.org/wiki/User_Data_Header">User Data
	 * Header</a>).
	 * 
	 * @param message
	 *            the message that may need to be split
	 * @return the result segments
	 * @throws SplitMessageException
	 *             when message couldn't be split
	 */
	List<Segment> split(String message) throws SplitMessageException;
}
