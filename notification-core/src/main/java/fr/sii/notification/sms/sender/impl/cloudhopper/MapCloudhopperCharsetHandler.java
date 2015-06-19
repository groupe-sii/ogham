package fr.sii.notification.sms.sender.impl.cloudhopper;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudhopper.commons.charset.Charset;
import com.cloudhopper.commons.charset.CharsetUtil;

import fr.sii.notification.core.charset.CharsetProvider;
import fr.sii.notification.sms.exception.message.EncodingException;

/**
 * Default implementation of a configurable {@link CloudhopperCharsetHandler}.
 * 
 * @author cdejonghe
 * 
 */
public class MapCloudhopperCharsetHandler implements CloudhopperCharsetHandler {
	private static final Logger LOG = LoggerFactory.getLogger(MapCloudhopperCharsetHandler.class);

	/**
	 * The map of {@link Charset} indexed by the NIO
	 * {@link java.nio.charset.Charset} code.
	 */
	private final Map<String, Charset> mapCloudhopperCharsetByNioCharsetName = new HashMap<>();

	/** The charset provider. */
	private final CharsetProvider charsetProvider;

	public MapCloudhopperCharsetHandler(CharsetProvider charsetProvider) {
		super();
		this.charsetProvider = charsetProvider;
	}

	/**
	 * Initializes with the map of {@link Charset} code handlers indexed indexed
	 * by the NIO {@link java.nio.charset.Charset} code.
	 * 
	 * @param map
	 *            the map of content handlers indexed by the content class
	 * @throws EncodingException
	 *             If the map contains any invalid cloudhopper charset name
	 * 
	 */
	public MapCloudhopperCharsetHandler(CharsetProvider charsetProvider, Map<String, String> mapCloudhopperNameByNioName)
			throws EncodingException {
		this(charsetProvider);

		for (Entry<String, String> nioCharset : mapCloudhopperNameByNioName.entrySet()) {
			addCharset(nioCharset.getKey(), nioCharset.getValue());
		}
	}

	/**
	 * Add a charset mapping.
	 * 
	 * @param nioCharsetName
	 *            Java NIO charset name
	 * @param cloudhopperCharsetName
	 *            Cloudhopper charset name
	 * @see CharsetUtil
	 * @throws EncodingException
	 *             If Cloudhopper charset name is invalid
	 */
	public void addCharset(String nioCharsetName, String cloudhopperCharsetName)
			throws EncodingException {
		Charset charset = CharsetUtil.map(cloudhopperCharsetName);
		if (charset != null) {
			addCharset(nioCharsetName, charset);
		} else {
			throw new EncodingException("Invalid cloudhopper charset name : " + cloudhopperCharsetName);
		}
	}

	/**
	 * Add a charset mapping.
	 * 
	 * @param nioCharsetName
	 *            Java NIO charset name
	 * @param cloudhopperCharset
	 *            Cloudhopper charset
	 */
	public void addCharset(String nioCharsetName, Charset cloudhopperCharset) {
		LOG.debug("Added charset mapping nio {} -> {}", nioCharsetName, cloudhopperCharset);
		mapCloudhopperCharsetByNioCharsetName.put(nioCharsetName, cloudhopperCharset);
	}

	private Charset get(java.nio.charset.Charset nioCharset)
			throws EncodingException {
		Charset cloudhopperCharset = mapCloudhopperCharsetByNioCharsetName.get(nioCharset.name());
		if (cloudhopperCharset == null) {
			throw new EncodingException("No cloudhopper charset registered for nio charset : " + nioCharset.name());
		}

		return mapCloudhopperCharsetByNioCharsetName.get(nioCharset.name());
	}

	@Override
	public byte[] encode(String messageStringContent)
			throws EncodingException {
		java.nio.charset.Charset nioCharset = charsetProvider.getCharset(messageStringContent);
		if (nioCharset == null) {
			throw new EncodingException("No charset provided for message : \n" + messageStringContent);
		}

		Charset cloudhopperCharset = get(nioCharset);
		LOG.debug("Encoding message using mapping nio {} -> {}", nioCharset.name(), cloudhopperCharset);
		return CharsetUtil.encode(messageStringContent, cloudhopperCharset);
	}
}
