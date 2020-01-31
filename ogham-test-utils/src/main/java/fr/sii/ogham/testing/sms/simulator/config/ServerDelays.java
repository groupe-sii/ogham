package fr.sii.ogham.testing.sms.simulator.config;

/**
 * Control delays to simulate a slow server.
 * 
 * @author Aurélien Baudet
 *
 */
public class ServerDelays {
	/**
	 * Simulate slow server by waiting {@code sendAlertNotificationDelay}
	 * milliseconds before sending "AlertNotification" data
	 */
	private long sendAlertNotificationDelay;
	/**
	 * Simulate slow server by waiting {@code sendBindDelay} milliseconds before
	 * sending "Bind" data
	 */
	private long sendBindDelay;
	/**
	 * Simulate slow server by waiting {@code sendBindRespDelay} milliseconds
	 * before sending "BindResp" data
	 */
	private long sendBindRespDelay;
	/**
	 * Simulate slow server by waiting {@code sendCancelSmDelay} milliseconds
	 * before sending "CancelSm" data
	 */
	private long sendCancelSmDelay;
	/**
	 * Simulate slow server by waiting {@code sendCancelSmRespDelay}
	 * milliseconds before sending "CancelSmResp" data
	 */
	private long sendCancelSmRespDelay;
	/**
	 * Simulate slow server by waiting {@code sendDataSmDelay} milliseconds
	 * before sending "DataSm" data
	 */
	private long sendDataSmDelay;
	/**
	 * Simulate slow server by waiting {@code sendDataSmRespDelay} milliseconds
	 * before sending "DataSmResp" data
	 */
	private long sendDataSmRespDelay;
	/**
	 * Simulate slow server by waiting {@code sendDeliverSmDelay} milliseconds
	 * before sending "DeliverSm" data
	 */
	private long sendDeliverSmDelay;
	/**
	 * Simulate slow server by waiting {@code sendDeliverSmRespDelay}
	 * milliseconds before sending "DeliverSmResp" data
	 */
	private long sendDeliverSmRespDelay;
	/**
	 * Simulate slow server by waiting {@code sendEnquireLinkDelay} milliseconds
	 * before sending "EnquireLink" data
	 */
	private long sendEnquireLinkDelay;
	/**
	 * Simulate slow server by waiting {@code sendEnquireLinkRespDelay}
	 * milliseconds before sending "EnquireLinkResp" data
	 */
	private long sendEnquireLinkRespDelay;
	/**
	 * Simulate slow server by waiting {@code sendGenericNackDelay} milliseconds
	 * before sending "GenericNack" data
	 */
	private long sendGenericNackDelay;
	/**
	 * Simulate slow server by waiting {@code sendHeaderDelay} milliseconds
	 * before sending "Header" data
	 */
	private long sendHeaderDelay;
	/**
	 * Simulate slow server by waiting {@code sendOutbindDelay} milliseconds
	 * before sending "Outbind" data
	 */
	private long sendOutbindDelay;
	/**
	 * Simulate slow server by waiting {@code sendQuerySmDelay} milliseconds
	 * before sending "QuerySm" data
	 */
	private long sendQuerySmDelay;
	/**
	 * Simulate slow server by waiting {@code sendQuerySmRespDelay} milliseconds
	 * before sending "QuerySmResp" data
	 */
	private long sendQuerySmRespDelay;
	/**
	 * Simulate slow server by waiting {@code sendReplaceSmDelay} milliseconds
	 * before sending "ReplaceSm" data
	 */
	private long sendReplaceSmDelay;
	/**
	 * Simulate slow server by waiting {@code sendReplaceSmRespDelay}
	 * milliseconds before sending "ReplaceSmResp" data
	 */
	private long sendReplaceSmRespDelay;
	/**
	 * Simulate slow server by waiting {@code sendSubmiMultiDelay} milliseconds
	 * before sending "SubmiMulti" data
	 */
	private long sendSubmiMultiDelay;
	/**
	 * Simulate slow server by waiting {@code sendSubmitMultiRespDelay}
	 * milliseconds before sending "SubmitMultiResp" data
	 */
	private long sendSubmitMultiRespDelay;
	/**
	 * Simulate slow server by waiting {@code sendSubmitSmDelay} milliseconds
	 * before sending "SubmitSm" data
	 */
	private long sendSubmitSmDelay;
	/**
	 * Simulate slow server by waiting {@code sendSubmitSmRespDelay}
	 * milliseconds before sending "SubmitSmResp" data
	 */
	private long sendSubmitSmRespDelay;
	/**
	 * Simulate slow server by waiting {@code sendUnbindDelay} milliseconds
	 * before sending "Unbind" data
	 */
	private long sendUnbindDelay;
	/**
	 * Simulate slow server by waiting {@code sendUnbindRespDelay} milliseconds
	 * before sending "UnbindResp" data
	 */
	private long sendUnbindRespDelay;

	public long getSendAlertNotificationDelay() {
		return sendAlertNotificationDelay;
	}

	public void setSendAlertNotificationDelay(long sendAlertNotificationDelay) {
		this.sendAlertNotificationDelay = sendAlertNotificationDelay;
	}

	public long getSendBindDelay() {
		return sendBindDelay;
	}

	public void setSendBindDelay(long sendBindDelay) {
		this.sendBindDelay = sendBindDelay;
	}

	public long getSendBindRespDelay() {
		return sendBindRespDelay;
	}

	public void setSendBindRespDelay(long sendBindRespDelay) {
		this.sendBindRespDelay = sendBindRespDelay;
	}

	public long getSendCancelSmDelay() {
		return sendCancelSmDelay;
	}

	public void setSendCancelSmDelay(long sendCancelSmDelay) {
		this.sendCancelSmDelay = sendCancelSmDelay;
	}

	public long getSendCancelSmRespDelay() {
		return sendCancelSmRespDelay;
	}

	public void setSendCancelSmRespDelay(long sendCancelSmRespDelay) {
		this.sendCancelSmRespDelay = sendCancelSmRespDelay;
	}

	public long getSendDataSmDelay() {
		return sendDataSmDelay;
	}

	public void setSendDataSmDelay(long sendDataSmDelay) {
		this.sendDataSmDelay = sendDataSmDelay;
	}

	public long getSendDataSmRespDelay() {
		return sendDataSmRespDelay;
	}

	public void setSendDataSmRespDelay(long sendDataSmRespDelay) {
		this.sendDataSmRespDelay = sendDataSmRespDelay;
	}

	public long getSendDeliverSmDelay() {
		return sendDeliverSmDelay;
	}

	public void setSendDeliverSmDelay(long sendDeliverSmDelay) {
		this.sendDeliverSmDelay = sendDeliverSmDelay;
	}

	public long getSendDeliverSmRespDelay() {
		return sendDeliverSmRespDelay;
	}

	public void setSendDeliverSmRespDelay(long sendDeliverSmRespDelay) {
		this.sendDeliverSmRespDelay = sendDeliverSmRespDelay;
	}

	public long getSendEnquireLinkDelay() {
		return sendEnquireLinkDelay;
	}

	public void setSendEnquireLinkDelay(long sendEnquireLinkDelay) {
		this.sendEnquireLinkDelay = sendEnquireLinkDelay;
	}

	public long getSendEnquireLinkRespDelay() {
		return sendEnquireLinkRespDelay;
	}

	public void setSendEnquireLinkRespDelay(long sendEnquireLinkRespDelay) {
		this.sendEnquireLinkRespDelay = sendEnquireLinkRespDelay;
	}

	public long getSendGenericNackDelay() {
		return sendGenericNackDelay;
	}

	public void setSendGenericNackDelay(long sendGenericNackDelay) {
		this.sendGenericNackDelay = sendGenericNackDelay;
	}

	public long getSendHeaderDelay() {
		return sendHeaderDelay;
	}

	public void setSendHeaderDelay(long sendHeaderDelay) {
		this.sendHeaderDelay = sendHeaderDelay;
	}

	public long getSendOutbindDelay() {
		return sendOutbindDelay;
	}

	public void setSendOutbindDelay(long sendOutbindDelay) {
		this.sendOutbindDelay = sendOutbindDelay;
	}

	public long getSendQuerySmDelay() {
		return sendQuerySmDelay;
	}

	public void setSendQuerySmDelay(long sendQuerySmDelay) {
		this.sendQuerySmDelay = sendQuerySmDelay;
	}

	public long getSendQuerySmRespDelay() {
		return sendQuerySmRespDelay;
	}

	public void setSendQuerySmRespDelay(long sendQuerySmRespDelay) {
		this.sendQuerySmRespDelay = sendQuerySmRespDelay;
	}

	public long getSendReplaceSmDelay() {
		return sendReplaceSmDelay;
	}

	public void setSendReplaceSmDelay(long sendReplaceSmDelay) {
		this.sendReplaceSmDelay = sendReplaceSmDelay;
	}

	public long getSendReplaceSmRespDelay() {
		return sendReplaceSmRespDelay;
	}

	public void setSendReplaceSmRespDelay(long sendReplaceSmRespDelay) {
		this.sendReplaceSmRespDelay = sendReplaceSmRespDelay;
	}

	public long getSendSubmiMultiDelay() {
		return sendSubmiMultiDelay;
	}

	public void setSendSubmiMultiDelay(long sendSubmiMultiDelay) {
		this.sendSubmiMultiDelay = sendSubmiMultiDelay;
	}

	public long getSendSubmitMultiRespDelay() {
		return sendSubmitMultiRespDelay;
	}

	public void setSendSubmitMultiRespDelay(long sendSubmitMultiRespDelay) {
		this.sendSubmitMultiRespDelay = sendSubmitMultiRespDelay;
	}

	public long getSendSubmitSmDelay() {
		return sendSubmitSmDelay;
	}

	public void setSendSubmitSmDelay(long sendSubmitSmDelay) {
		this.sendSubmitSmDelay = sendSubmitSmDelay;
	}

	public long getSendSubmitSmRespDelay() {
		return sendSubmitSmRespDelay;
	}

	public void setSendSubmitSmRespDelay(long sendSubmitSmRespDelay) {
		this.sendSubmitSmRespDelay = sendSubmitSmRespDelay;
	}

	public long getSendUnbindDelay() {
		return sendUnbindDelay;
	}

	public void setSendUnbindDelay(long sendUnbindDelay) {
		this.sendUnbindDelay = sendUnbindDelay;
	}

	public long getSendUnbindRespDelay() {
		return sendUnbindRespDelay;
	}

	public void setSendUnbindRespDelay(long sendUnbindRespDelay) {
		this.sendUnbindRespDelay = sendUnbindRespDelay;
	}
}
