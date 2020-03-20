package fr.sii.ogham.testing.sms.simulator.config;

/**
 * Control delays to simulate a slow server.
 * 
 * @author Aurélien Baudet
 *
 */
public class ServerDelays {
	/**
	 * Simulate slow server by executing {@code sendAlertNotificationWaiting}
	 * function before sending "AlertNotification" data
	 */
	private Awaiter sendAlertNotificationWaiting;
	/**
	 * Simulate slow server by executing {@code sendBindWaiting} function before
	 * sending "Bind" data
	 */
	private Awaiter sendBindWaiting;
	/**
	 * Simulate slow server by executing {@code sendBindRespWaiting} function
	 * before sending "BindResp" data
	 */
	private Awaiter sendBindRespWaiting;
	/**
	 * Simulate slow server by executing {@code sendCancelSmWaiting} function
	 * before sending "CancelSm" data
	 */
	private Awaiter sendCancelSmWaiting;
	/**
	 * Simulate slow server by executing {@code sendCancelSmRespWaiting}
	 * function before sending "CancelSmResp" data
	 */
	private Awaiter sendCancelSmRespWaiting;
	/**
	 * Simulate slow server by executing {@code sendDataSmWaiting} function
	 * before sending "DataSm" data
	 */
	private Awaiter sendDataSmWaiting;
	/**
	 * Simulate slow server by executing {@code sendDataSmRespWaiting} function
	 * before sending "DataSmResp" data
	 */
	private Awaiter sendDataSmRespWaiting;
	/**
	 * Simulate slow server by executing {@code sendDeliverSmWaiting} function
	 * before sending "DeliverSm" data
	 */
	private Awaiter sendDeliverSmWaiting;
	/**
	 * Simulate slow server by executing {@code sendDeliverSmRespWaiting}
	 * function before sending "DeliverSmResp" data
	 */
	private Awaiter sendDeliverSmRespWaiting;
	/**
	 * Simulate slow server by executing {@code sendEnquireLinkWaiting} function
	 * before sending "EnquireLink" data
	 */
	private Awaiter sendEnquireLinkWaiting;
	/**
	 * Simulate slow server by executing {@code sendEnquireLinkRespWaiting}
	 * function before sending "EnquireLinkResp" data
	 */
	private Awaiter sendEnquireLinkRespWaiting;
	/**
	 * Simulate slow server by executing {@code sendGenericNackWaiting} function
	 * before sending "GenericNack" data
	 */
	private Awaiter sendGenericNackWaiting;
	/**
	 * Simulate slow server by executing {@code sendHeaderWaiting} function
	 * before sending "Header" data
	 */
	private Awaiter sendHeaderWaiting;
	/**
	 * Simulate slow server by executing {@code sendOutbindWaiting} function
	 * before sending "Outbind" data
	 */
	private Awaiter sendOutbindWaiting;
	/**
	 * Simulate slow server by executing {@code sendQuerySmWaiting} function
	 * before sending "QuerySm" data
	 */
	private Awaiter sendQuerySmWaiting;
	/**
	 * Simulate slow server by executing {@code sendQuerySmRespWaiting} function
	 * before sending "QuerySmResp" data
	 */
	private Awaiter sendQuerySmRespWaiting;
	/**
	 * Simulate slow server by executing {@code sendReplaceSmWaiting} function
	 * before sending "ReplaceSm" data
	 */
	private Awaiter sendReplaceSmWaiting;
	/**
	 * Simulate slow server by executing {@code sendReplaceSmRespWaiting}
	 * function before sending "ReplaceSmResp" data
	 */
	private Awaiter sendReplaceSmRespWaiting;
	/**
	 * Simulate slow server by executing {@code sendSubmiMultiWaiting} function
	 * before sending "SubmiMulti" data
	 */
	private Awaiter sendSubmiMultiWaiting;
	/**
	 * Simulate slow server by executing {@code sendSubmitMultiRespWaiting}
	 * function before sending "SubmitMultiResp" data
	 */
	private Awaiter sendSubmitMultiRespWaiting;
	/**
	 * Simulate slow server by executing {@code sendSubmitSmWaiting} function
	 * before sending "SubmitSm" data
	 */
	private Awaiter sendSubmitSmWaiting;
	/**
	 * Simulate slow server by executing {@code sendSubmitSmRespWaiting}
	 * function before sending "SubmitSmResp" data
	 */
	private Awaiter sendSubmitSmRespWaiting;
	/**
	 * Simulate slow server by executing {@code sendUnbindWaiting} function
	 * before sending "Unbind" data
	 */
	private Awaiter sendUnbindWaiting;
	/**
	 * Simulate slow server by executing {@code sendUnbindRespWaiting} function
	 * before sending "UnbindResp" data
	 */
	private Awaiter sendUnbindRespWaiting;

	public Awaiter getSendAlertNotificationWaiting() {
		return sendAlertNotificationWaiting;
	}

	public void setSendAlertNotificationWaiting(Awaiter sendAlertNotificationWaiting) {
		this.sendAlertNotificationWaiting = sendAlertNotificationWaiting;
	}

	public Awaiter getSendBindWaiting() {
		return sendBindWaiting;
	}

	public void setSendBindWaiting(Awaiter sendBindWaiting) {
		this.sendBindWaiting = sendBindWaiting;
	}

	public Awaiter getSendBindRespWaiting() {
		return sendBindRespWaiting;
	}

	public void setSendBindRespWaiting(Awaiter sendBindRespWaiting) {
		this.sendBindRespWaiting = sendBindRespWaiting;
	}

	public Awaiter getSendCancelSmWaiting() {
		return sendCancelSmWaiting;
	}

	public void setSendCancelSmWaiting(Awaiter sendCancelSmWaiting) {
		this.sendCancelSmWaiting = sendCancelSmWaiting;
	}

	public Awaiter getSendCancelSmRespWaiting() {
		return sendCancelSmRespWaiting;
	}

	public void setSendCancelSmRespWaiting(Awaiter sendCancelSmRespWaiting) {
		this.sendCancelSmRespWaiting = sendCancelSmRespWaiting;
	}

	public Awaiter getSendDataSmWaiting() {
		return sendDataSmWaiting;
	}

	public void setSendDataSmWaiting(Awaiter sendDataSmWaiting) {
		this.sendDataSmWaiting = sendDataSmWaiting;
	}

	public Awaiter getSendDataSmRespWaiting() {
		return sendDataSmRespWaiting;
	}

	public void setSendDataSmRespWaiting(Awaiter sendDataSmRespWaiting) {
		this.sendDataSmRespWaiting = sendDataSmRespWaiting;
	}

	public Awaiter getSendDeliverSmWaiting() {
		return sendDeliverSmWaiting;
	}

	public void setSendDeliverSmWaiting(Awaiter sendDeliverSmWaiting) {
		this.sendDeliverSmWaiting = sendDeliverSmWaiting;
	}

	public Awaiter getSendDeliverSmRespWaiting() {
		return sendDeliverSmRespWaiting;
	}

	public void setSendDeliverSmRespWaiting(Awaiter sendDeliverSmRespWaiting) {
		this.sendDeliverSmRespWaiting = sendDeliverSmRespWaiting;
	}

	public Awaiter getSendEnquireLinkWaiting() {
		return sendEnquireLinkWaiting;
	}

	public void setSendEnquireLinkWaiting(Awaiter sendEnquireLinkWaiting) {
		this.sendEnquireLinkWaiting = sendEnquireLinkWaiting;
	}

	public Awaiter getSendEnquireLinkRespWaiting() {
		return sendEnquireLinkRespWaiting;
	}

	public void setSendEnquireLinkRespWaiting(Awaiter sendEnquireLinkRespWaiting) {
		this.sendEnquireLinkRespWaiting = sendEnquireLinkRespWaiting;
	}

	public Awaiter getSendGenericNackWaiting() {
		return sendGenericNackWaiting;
	}

	public void setSendGenericNackWaiting(Awaiter sendGenericNackWaiting) {
		this.sendGenericNackWaiting = sendGenericNackWaiting;
	}

	public Awaiter getSendHeaderWaiting() {
		return sendHeaderWaiting;
	}

	public void setSendHeaderWaiting(Awaiter sendHeaderWaiting) {
		this.sendHeaderWaiting = sendHeaderWaiting;
	}

	public Awaiter getSendOutbindWaiting() {
		return sendOutbindWaiting;
	}

	public void setSendOutbindWaiting(Awaiter sendOutbindWaiting) {
		this.sendOutbindWaiting = sendOutbindWaiting;
	}

	public Awaiter getSendQuerySmWaiting() {
		return sendQuerySmWaiting;
	}

	public void setSendQuerySmWaiting(Awaiter sendQuerySmWaiting) {
		this.sendQuerySmWaiting = sendQuerySmWaiting;
	}

	public Awaiter getSendQuerySmRespWaiting() {
		return sendQuerySmRespWaiting;
	}

	public void setSendQuerySmRespWaiting(Awaiter sendQuerySmRespWaiting) {
		this.sendQuerySmRespWaiting = sendQuerySmRespWaiting;
	}

	public Awaiter getSendReplaceSmWaiting() {
		return sendReplaceSmWaiting;
	}

	public void setSendReplaceSmWaiting(Awaiter sendReplaceSmWaiting) {
		this.sendReplaceSmWaiting = sendReplaceSmWaiting;
	}

	public Awaiter getSendReplaceSmRespWaiting() {
		return sendReplaceSmRespWaiting;
	}

	public void setSendReplaceSmRespWaiting(Awaiter sendReplaceSmRespWaiting) {
		this.sendReplaceSmRespWaiting = sendReplaceSmRespWaiting;
	}

	public Awaiter getSendSubmiMultiWaiting() {
		return sendSubmiMultiWaiting;
	}

	public void setSendSubmiMultiWaiting(Awaiter sendSubmiMultiWaiting) {
		this.sendSubmiMultiWaiting = sendSubmiMultiWaiting;
	}

	public Awaiter getSendSubmitMultiRespWaiting() {
		return sendSubmitMultiRespWaiting;
	}

	public void setSendSubmitMultiRespWaiting(Awaiter sendSubmitMultiRespWaiting) {
		this.sendSubmitMultiRespWaiting = sendSubmitMultiRespWaiting;
	}

	public Awaiter getSendSubmitSmWaiting() {
		return sendSubmitSmWaiting;
	}

	public void setSendSubmitSmWaiting(Awaiter sendSubmitSmWaiting) {
		this.sendSubmitSmWaiting = sendSubmitSmWaiting;
	}

	public Awaiter getSendSubmitSmRespWaiting() {
		return sendSubmitSmRespWaiting;
	}

	public void setSendSubmitSmRespWaiting(Awaiter sendSubmitSmRespWaiting) {
		this.sendSubmitSmRespWaiting = sendSubmitSmRespWaiting;
	}

	public Awaiter getSendUnbindWaiting() {
		return sendUnbindWaiting;
	}

	public void setSendUnbindWaiting(Awaiter sendUnbindWaiting) {
		this.sendUnbindWaiting = sendUnbindWaiting;
	}

	public Awaiter getSendUnbindRespWaiting() {
		return sendUnbindRespWaiting;
	}

	public void setSendUnbindRespWaiting(Awaiter sendUnbindRespWaiting) {
		this.sendUnbindRespWaiting = sendUnbindRespWaiting;
	}

}
