package fr.sii.ogham.sms.sender.impl.cloudhopper;

import com.cloudhopper.smpp.pdu.EnquireLink;

public class ReuseSessionOptions {
	/**
	 * Enable or disable the reuse the same session (if possible) for sending
	 * messages.
	 */
	private Boolean enable;
	/**
	 * To check if the session is still alive, an {@link EnquireLink} request is
	 * sent. The request is sent just before sending the message.
	 * 
	 * This is the time (in milliseconds) to wait before considering last
	 * {@link EnquireLink} response as expired (need to send a new
	 * {@link EnquireLink} request to check if session is still alive).
	 * 
	 * <p>
	 * This is needed to prevent sending {@link EnquireLink} request every time
	 * a message has to be sent. Instead it considers that the time elapsed
	 * between now and the last {@link EnquireLink} response (or the last sent
	 * message) is not enough so a new {@link EnquireLink} is not necessary to
	 * check if session is still alive.
	 *
	 * <p>
	 * Set to 0 or null to always check session before sending message.
	 */
	private Long lastInteractionExpirationDelay;
	/**
	 * To check if the session is still alive, an {@link EnquireLink} request is
	 * sent. This request may fail since the session may be killed by the
	 * server. The timeout ensures that the client doesn't wait too long for a
	 * response that may never come.
	 * 
	 * The maximum amount of time (in milliseconds) to wait for receiving a
	 * response from the server to an {@link EnquireLink} request.
	 */
	private Long enquireLinkTimeout;

	public ReuseSessionOptions() {
		super();
	}

	public ReuseSessionOptions(boolean enable) {
		super();
		this.enable = enable;
	}

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}
	
	public boolean isEnable(boolean defaultValue) {
		return enable == null ? defaultValue : enable;
	}

	public Long getLastInteractionExpirationDelay() {
		return lastInteractionExpirationDelay;
	}

	public void setLastInteractionExpirationDelay(Long delay) {
		this.lastInteractionExpirationDelay = delay;
	}

	public Long getEnquireLinkTimeout() {
		return enquireLinkTimeout;
	}

	public void setEnquireLinkTimeout(Long enquireLinkTimeout) {
		this.enquireLinkTimeout = enquireLinkTimeout;
	}

}
