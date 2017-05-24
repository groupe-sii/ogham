package fr.sii.ogham.core.message.content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sii.ogham.core.util.ArrayUtils;
import fr.sii.ogham.core.util.EqualsBuilder;
import fr.sii.ogham.core.util.HashCodeBuilder;

/**
 * Decorator content that provide ability to handle several sub contents. The
 * aim is to be able to handle messages with several distinct contents like
 * email for example that can contain at the same time an HTML message and a
 * text message. The email client implementation is free to display the content
 * it is able to handle.
 * 
 * @author Aurélien Baudet
 *
 */
public class MultiContent implements Content {
	/**
	 * The list of sub contents
	 */
	private final List<Content> contents;

	/**
	 * Initialize the content with one or several sub contents directly as strings.
	 * 
	 * @param content
	 *            the mandatory content
	 * @param contents
	 *            the contents either as array or multiple arguments
	 */
	public MultiContent(String content, String... contents) {
		this(toContent(ArrayUtils.concat(content, contents)));
	}

	/**
	 * Initialize the content with none, one or several sub contents.
	 * 
	 * @param contents
	 *            the contents either as array or multiple arguments
	 */
	public MultiContent(Content... contents) {
		this(new ArrayList<>(Arrays.asList(contents)));
	}

	/**
	 * Initialize the content with a list of sub contents
	 * 
	 * @param contents
	 *            the list of sub contents
	 */
	public MultiContent(List<Content> contents) {
		super();
		this.contents = contents;
	}

	public List<Content> getContents() {
		return contents;
	}

	/**
	 * Add a sub content to the list of sub contents.
	 * 
	 * @param content
	 *            the content to add
	 */
	public void addContent(Content content) {
		contents.add(content);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MultiContent [contents=").append(contents).append("]");
		return builder.toString();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(contents).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder(this, obj).appendFields("contents").isEqual();
	}
	
	private static Content[] toContent(String[] strs) {
		Content[] contents = new Content[strs.length];
		for(int i=0 ; i<strs.length ; i++) {
			contents[i] = new StringContent(strs[i]);
		}
		return contents;
	}
}
