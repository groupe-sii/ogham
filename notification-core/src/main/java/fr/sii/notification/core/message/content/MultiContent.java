package fr.sii.notification.core.message.content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sii.notification.core.util.EqualsBuilder;
import fr.sii.notification.core.util.HashCodeBuilder;

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
	private List<Content> contents;

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
		return new HashCodeBuilder().appendSuper(super.hashCode()).append(contents).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder(this, obj).appendSuper(super.equals(obj)).append("contents").equals();
	}
}
