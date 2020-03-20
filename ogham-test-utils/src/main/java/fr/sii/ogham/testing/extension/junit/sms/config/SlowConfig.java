package fr.sii.ogham.testing.extension.junit.sms.config;

import static org.awaitility.Awaitility.await;

import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.testing.sms.simulator.config.Awaiter;
import fr.sii.ogham.testing.sms.simulator.config.ServerDelays;
import fr.sii.ogham.testing.util.HasParent;

public class SlowConfig extends HasParent<ServerConfig> {
	private static final Logger LOG = LoggerFactory.getLogger(SlowConfig.class);
	
	private final WaitingConfHelper sendAlertNotification;
	private final WaitingConfHelper sendBind;
	private final WaitingConfHelper sendBindResp;
	private final WaitingConfHelper sendCancelSm;
	private final WaitingConfHelper sendCancelSmResp;
	private final WaitingConfHelper sendDataSm;
	private final WaitingConfHelper sendDataSmResp;
	private final WaitingConfHelper sendDeliverSm;
	private final WaitingConfHelper sendDeliverSmResp;
	private final WaitingConfHelper sendEnquireLink;
	private final WaitingConfHelper sendEnquireLinkResp;
	private final WaitingConfHelper sendGenericNack;
	private final WaitingConfHelper sendHeader;
	private final WaitingConfHelper sendOutbind;
	private final WaitingConfHelper sendQuerySm;
	private final WaitingConfHelper sendQuerySmResp;
	private final WaitingConfHelper sendReplaceSm;
	private final WaitingConfHelper sendReplaceSmResp;
	private final WaitingConfHelper sendSubmitMulti;
	private final WaitingConfHelper sendSubmitMultiResp;
	private final WaitingConfHelper sendSubmitSm;
	private final WaitingConfHelper sendSubmitSmResp;
	private final WaitingConfHelper sendUnbind;
	private final WaitingConfHelper sendUnbindResp;

	public SlowConfig(ServerConfig parent) {
		super(parent);
		sendAlertNotification = new WaitingConfHelper(ServerDelays::setSendAlertNotificationWaiting);
		sendBind = new WaitingConfHelper(ServerDelays::setSendBindWaiting);
		sendBindResp = new WaitingConfHelper(ServerDelays::setSendBindRespWaiting);
		sendCancelSm = new WaitingConfHelper(ServerDelays::setSendCancelSmWaiting);
		sendCancelSmResp = new WaitingConfHelper(ServerDelays::setSendCancelSmRespWaiting);
		sendDataSm = new WaitingConfHelper(ServerDelays::setSendDataSmWaiting);
		sendDataSmResp = new WaitingConfHelper(ServerDelays::setSendDataSmRespWaiting);
		sendDeliverSm = new WaitingConfHelper(ServerDelays::setSendDeliverSmWaiting);
		sendDeliverSmResp = new WaitingConfHelper(ServerDelays::setSendDeliverSmRespWaiting);
		sendEnquireLink = new WaitingConfHelper(ServerDelays::setSendEnquireLinkWaiting);
		sendEnquireLinkResp = new WaitingConfHelper(ServerDelays::setSendEnquireLinkRespWaiting);
		sendGenericNack = new WaitingConfHelper(ServerDelays::setSendGenericNackWaiting);
		sendHeader = new WaitingConfHelper(ServerDelays::setSendHeaderWaiting);
		sendOutbind = new WaitingConfHelper(ServerDelays::setSendOutbindWaiting);
		sendQuerySm = new WaitingConfHelper(ServerDelays::setSendQuerySmWaiting);
		sendQuerySmResp = new WaitingConfHelper(ServerDelays::setSendQuerySmRespWaiting);
		sendReplaceSm = new WaitingConfHelper(ServerDelays::setSendReplaceSmRespWaiting);
		sendReplaceSmResp = new WaitingConfHelper(ServerDelays::setSendReplaceSmRespWaiting);
		sendSubmitMulti = new WaitingConfHelper(ServerDelays::setSendSubmiMultiWaiting);
		sendSubmitMultiResp = new WaitingConfHelper(ServerDelays::setSendSubmitMultiRespWaiting);
		sendSubmitSm = new WaitingConfHelper(ServerDelays::setSendSubmitSmWaiting);
		sendSubmitSmResp = new WaitingConfHelper(ServerDelays::setSendSubmitSmRespWaiting);
		sendUnbind = new WaitingConfHelper(ServerDelays::setSendUnbindWaiting);
		sendUnbindResp = new WaitingConfHelper(ServerDelays::setSendUnbindRespWaiting);
	}

	/**
	 * Simulate slow server by waiting {@code sendAlertNotificationDelay}
	 * milliseconds before sending data
	 *
	 * <strong>NOTE:</strong> If {@link #sendAlertNotificationWaiting(Awaiter)}
	 * is also called then this delay has no effect.
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendAlertNotificationDelay(long delayMs) {
		sendAlertNotification.setDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by executing {@code sendAlertNotificationWaiting}
	 * function before sending data
	 * 
	 *
	 * <strong>NOTE:</strong> Calling {@link #sendAlertNotificationDelay(long)}
	 * has no effect if waiting function is set.
	 * 
	 * @param waiting
	 *            the function to execute to simulate delay before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendAlertNotificationWaiting(Awaiter waiting) {
		sendAlertNotification.setWaiting(waiting);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendBindDelay} milliseconds before
	 * sending data.
	 *
	 * <strong>NOTE:</strong> If {@link #sendBindWaiting(Awaiter)} is also
	 * called then this delay has no effect.
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendBindDelay(long delayMs) {
		sendBind.setDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by executing {@code sendBindWaiting} function before
	 * sending data.
	 *
	 * <strong>NOTE:</strong> Calling {@link #sendBindDelay(long)} has no effect
	 * if waiting function is set.
	 * 
	 * @param waiting
	 *            the function to execute to simulate delay before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendBindWaiting(Awaiter waiting) {
		sendBind.setWaiting(waiting);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendBindRespDelay} milliseconds
	 * before sending data.
	 *
	 * <strong>NOTE:</strong> If {@link #sendBindRespWaiting(Awaiter)} is also
	 * called then this delay has no effect.
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendBindRespDelay(long delayMs) {
		sendBindResp.setDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by executing {@code sendBindRespWaiting} function
	 * before sending data.
	 *
	 * <strong>NOTE:</strong> Calling {@link #sendBindRespDelay(long)} has no
	 * effect if waiting function is set.
	 * 
	 * @param waiting
	 *            the function to execute to simulate delay before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendBindRespWaiting(Awaiter waiting) {
		sendBindResp.setWaiting(waiting);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendCancelSmDelay} milliseconds
	 * before sending data.
	 *
	 * <strong>NOTE:</strong> If {@link #sendCancelSmWaiting(Awaiter)} is also
	 * called then this delay has no effect.
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendCancelSmDelay(long delayMs) {
		sendCancelSm.setDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by executing {@code sendCancelSmWaiting} function
	 * before sending data.
	 *
	 * <strong>NOTE:</strong> Calling {@link #sendCancelSmDelay(long)} has no
	 * effect if waiting function is set.
	 * 
	 * @param waiting
	 *            the function to execute to simulate delay before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendCancelSmWaiting(Awaiter waiting) {
		sendCancelSm.setWaiting(waiting);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendCancelSmRespDelay}
	 * milliseconds before sending data.
	 *
	 * <strong>NOTE:</strong> If {@link #sendCancelSmRespWaiting(Awaiter)} is
	 * also called then this delay has no effect.
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendCancelSmRespDelay(long delayMs) {
		sendCancelSmResp.setDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by executing {@code sendCancelSmRespWaiting}
	 * function before sending data.
	 *
	 * <strong>NOTE:</strong> Calling {@link #sendCancelSmRespDelay(long)} has
	 * no effect if waiting function is set.
	 * 
	 * @param waiting
	 *            the function to execute to simulate delay before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendCancelSmRespWaiting(Awaiter waiting) {
		sendCancelSmResp.setWaiting(waiting);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendDataSmDelay} milliseconds
	 * before sending data.
	 *
	 * <strong>NOTE:</strong> If {@link #sendDataSmWaiting(Awaiter)} is also
	 * called then this delay has no effect.
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendDataSmDelay(long delayMs) {
		sendDataSm.setDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by executing {@code sendDataSmWaiting} function
	 * before sending data.
	 *
	 * <strong>NOTE:</strong> Calling {@link #sendDataSmDelay(long)} has no
	 * effect if waiting function is set.
	 * 
	 * @param waiting
	 *            the function to execute to simulate delay before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendDataSmWaiting(Awaiter waiting) {
		sendDataSm.setWaiting(waiting);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendDataSmRespDelay} milliseconds
	 * before sending data.
	 *
	 * <strong>NOTE:</strong> If {@link #sendDataSmRespWaiting(Awaiter)} is also
	 * called then this delay has no effect.
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendDataSmRespDelay(long delayMs) {
		sendDataSmResp.setDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by executing {@code sendDataSmRespWaiting} function
	 * before sending data.
	 *
	 * <strong>NOTE:</strong> Calling {@link #sendDataSmRespDelay(long)} has no
	 * effect if waiting function is set.
	 * 
	 * @param waiting
	 *            the function to execute to simulate delay before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendDataSmRespWaiting(Awaiter waiting) {
		sendDataSmResp.setWaiting(waiting);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendDeliverSmDelay} milliseconds
	 * before sending data.
	 *
	 * <strong>NOTE:</strong> If {@link #sendDeliverSmWaiting(Awaiter)} is also
	 * called then this delay has no effect.
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendDeliverSmDelay(long delayMs) {
		sendDeliverSm.setDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by executing {@code sendDeliverSmWaiting} function
	 * before sending data.
	 *
	 * <strong>NOTE:</strong> Calling {@link #sendDeliverSmDelay(long)} has no
	 * effect if waiting function is set.
	 * 
	 * @param waiting
	 *            the function to execute to simulate delay before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendDeliverSmWaiting(Awaiter waiting) {
		sendDeliverSm.setWaiting(waiting);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendDeliverSmRespDelay}
	 * milliseconds before sending data.
	 *
	 * <strong>NOTE:</strong> If {@link #sendDeliverSmRespWaiting(Awaiter)} is
	 * also called then this delay has no effect.
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendDeliverSmRespDelay(long delayMs) {
		sendDeliverSmResp.setDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by executing {@code sendDeliverSmRespWaiting}
	 * function before sending data.
	 *
	 * <strong>NOTE:</strong> Calling {@link #sendDeliverSmRespDelay(long)} has
	 * no effect if waiting function is set.
	 * 
	 * @param waiting
	 *            the function to execute to simulate delay before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendDeliverSmRespWaiting(Awaiter waiting) {
		sendDeliverSmResp.setWaiting(waiting);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendEnquireLinkDelay} milliseconds
	 * before sending data.
	 *
	 * <strong>NOTE:</strong> If {@link #sendEnquireLinkWaiting(Awaiter)} is
	 * also called then this delay has no effect.
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendEnquireLinkDelay(long delayMs) {
		sendEnquireLink.setDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by executing {@code sendEnquireLinkWaiting} function
	 * before sending data.
	 *
	 * <strong>NOTE:</strong> Calling {@link #sendEnquireLinkDelay(long)} has no
	 * effect if waiting function is set.
	 * 
	 * @param waiting
	 *            the function to execute to simulate delay before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendEnquireLinkWaiting(Awaiter waiting) {
		sendEnquireLink.setWaiting(waiting);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendEnquireLinkRespDelay}
	 * milliseconds before sending data.
	 *
	 * <strong>NOTE:</strong> If {@link #sendEnquireLinkRespWaiting(Awaiter)} is
	 * also called then this delay has no effect.
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendEnquireLinkRespDelay(long delayMs) {
		sendEnquireLinkResp.setDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by executing {@code sendEnquireLinkRespWaiting}
	 * function before sending data.
	 *
	 * <strong>NOTE:</strong> Calling {@link #sendEnquireLinkRespDelay(long)}
	 * has no effect if waiting function is set.
	 * 
	 * @param waiting
	 *            the function to execute to simulate delay before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendEnquireLinkRespWaiting(Awaiter waiting) {
		sendEnquireLinkResp.setWaiting(waiting);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendGenericNackDelay} milliseconds
	 * before sending data.
	 *
	 * <strong>NOTE:</strong> If {@link #sendGenericNackWaiting(Awaiter)} is
	 * also called then this delay has no effect.
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendGenericNackDelay(long delayMs) {
		sendGenericNack.setDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by executing {@code sendGenericNackWaiting} function
	 * before sending data.
	 *
	 * <strong>NOTE:</strong> Calling {@link #sendGenericNackDelay(long)} has no
	 * effect if waiting function is set.
	 * 
	 * @param waiting
	 *            the function to execute to simulate delay before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendGenericNackWaiting(Awaiter waiting) {
		sendGenericNack.setWaiting(waiting);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendHeaderDelay} milliseconds
	 * before sending data.
	 *
	 * <strong>NOTE:</strong> If {@link #sendHeaderWaiting(Awaiter)} is also
	 * called then this delay has no effect.
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendHeaderDelay(long delayMs) {
		sendHeader.setDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by executing {@code sendHeaderWaiting} function
	 * before sending data.
	 *
	 * <strong>NOTE:</strong> Calling {@link #sendHeaderDelay(long)} has no
	 * effect if waiting function is set.
	 * 
	 * @param waiting
	 *            the function to execute to simulate delay before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendHeaderWaiting(Awaiter waiting) {
		sendHeader.setWaiting(waiting);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendOutbindDelay} milliseconds
	 * before sending data.
	 *
	 * <strong>NOTE:</strong> If {@link #sendOutbindWaiting(Awaiter)} is also
	 * called then this delay has no effect.
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendOutbindDelay(long delayMs) {
		sendOutbind.setDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by executing {@code sendOutbindWaiting} function
	 * before sending data.
	 *
	 * <strong>NOTE:</strong> Calling {@link #sendOutbindDelay(long)} has no
	 * effect if waiting function is set.
	 * 
	 * @param waiting
	 *            the function to execute to simulate delay before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendOutbindWaiting(Awaiter waiting) {
		sendOutbind.setWaiting(waiting);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendQuerySmDelay} milliseconds
	 * before sending data.
	 *
	 * <strong>NOTE:</strong> If {@link #sendQuerySmWaiting(Awaiter)} is also
	 * called then this delay has no effect.
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendQuerySmDelay(long delayMs) {
		sendQuerySm.setDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by executing {@code sendQuerySmWaiting} function
	 * before sending data.
	 *
	 * <strong>NOTE:</strong> Calling {@link #sendQuerySmDelay(long)} has no
	 * effect if waiting function is set.
	 * 
	 * @param waiting
	 *            the function to execute to simulate delay before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendQuerySmWaiting(Awaiter waiting) {
		sendQuerySm.setWaiting(waiting);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendQuerySmRespDelay} milliseconds
	 * before sending data.
	 *
	 * <strong>NOTE:</strong> If {@link #sendQuerySmRespWaiting(Awaiter)} is
	 * also called then this delay has no effect.
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendQuerySmRespDelay(long delayMs) {
		sendQuerySmResp.setDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by executing {@code sendQuerySmRespWaiting} function
	 * before sending data.
	 *
	 * <strong>NOTE:</strong> Calling {@link #sendQuerySmRespDelay(long)} has no
	 * effect if waiting function is set.
	 * 
	 * @param waiting
	 *            the function to execute to simulate delay before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendQuerySmRespWaiting(Awaiter waiting) {
		sendQuerySmResp.setWaiting(waiting);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendReplaceSmDelay} milliseconds
	 * before sending data.
	 *
	 * <strong>NOTE:</strong> If {@link #sendReplaceSmWaiting(Awaiter)} is also
	 * called then this delay has no effect.
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendReplaceSmDelay(long delayMs) {
		sendReplaceSm.setDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by executing {@code sendReplaceSmWaiting} function
	 * before sending data.
	 *
	 * <strong>NOTE:</strong> Calling {@link #sendReplaceSmDelay(long)} has no
	 * effect if waiting function is set.
	 * 
	 * @param waiting
	 *            the function to execute to simulate delay before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendReplaceSmWaiting(Awaiter waiting) {
		sendReplaceSm.setWaiting(waiting);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendReplaceSmRespDelay}
	 * milliseconds before sending data.
	 *
	 * <strong>NOTE:</strong> If {@link #sendReplaceSmRespWaiting(Awaiter)} is
	 * also called then this delay has no effect.
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendReplaceSmRespDelay(long delayMs) {
		sendReplaceSmResp.setDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by executing {@code sendReplaceSmRespWaiting}
	 * function before sending data.
	 *
	 * <strong>NOTE:</strong> Calling {@link #sendReplaceSmRespDelay(long)} has
	 * no effect if waiting function is set.
	 * 
	 * @param waiting
	 *            the function to execute to simulate delay before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendReplaceSmRespWaiting(Awaiter waiting) {
		sendReplaceSmResp.setWaiting(waiting);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendSubmitMultiDelay} milliseconds
	 * before sending data.
	 *
	 * <strong>NOTE:</strong> If {@link #sendSubmitMultiWaiting(Awaiter)} is
	 * also called then this delay has no effect.
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendSubmitMultiDelay(long delayMs) {
		sendSubmitMulti.setDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by executing {@code sendSubmitMultiWaiting} function
	 * before sending data.
	 *
	 * <strong>NOTE:</strong> Calling {@link #sendSubmitMultiDelay(long)} has no
	 * effect if waiting function is set.
	 * 
	 * @param waiting
	 *            the function to execute to simulate delay before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendSubmitMultiWaiting(Awaiter waiting) {
		sendSubmitMulti.setWaiting(waiting);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendSubmitMultiRespDelay}
	 * milliseconds before sending data.
	 *
	 * <strong>NOTE:</strong> If {@link #sendSubmitMultiRespWaiting(Awaiter)} is
	 * also called then this delay has no effect.
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendSubmitMultiRespDelay(long delayMs) {
		sendSubmitMultiResp.setDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by executing {@code sendSubmitMultiRespWaiting}
	 * function before sending data.
	 *
	 * <strong>NOTE:</strong> Calling {@link #sendSubmitMultiRespDelay(long)}
	 * has no effect if waiting function is set.
	 * 
	 * @param waiting
	 *            the function to execute to simulate delay before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendSubmitMultiRespWaiting(Awaiter waiting) {
		sendSubmitMultiResp.setWaiting(waiting);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendSubmitSmDelay} milliseconds
	 * before sending data.
	 *
	 * <strong>NOTE:</strong> If {@link #sendSubmitSmWaiting(Awaiter)} is also
	 * called then this delay has no effect.
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendSubmitSmDelay(long delayMs) {
		sendSubmitSm.setDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by executing {@code sendSubmitSmWaiting} function
	 * before sending data.
	 *
	 * <strong>NOTE:</strong> Calling {@link #sendSubmitSmDelay(long)} has no
	 * effect if waiting function is set.
	 * 
	 * @param waiting
	 *            the function to execute to simulate delay before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendSubmitSmWaiting(Awaiter waiting) {
		sendSubmitSm.setWaiting(waiting);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendSubmitSmRespDelay}
	 * milliseconds before sending data.
	 *
	 * <strong>NOTE:</strong> If {@link #sendSubmitSmRespWaiting(Awaiter)} is
	 * also called then this delay has no effect.
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendSubmitSmRespDelay(long delayMs) {
		sendSubmitSmResp.setDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by executing {@code sendSubmitSmRespWaiting}
	 * function before sending data.
	 *
	 * <strong>NOTE:</strong> Calling {@link #sendSubmitSmRespDelay(long)} has
	 * no effect if waiting function is set.
	 * 
	 * @param waiting
	 *            the function to execute to simulate delay before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendSubmitSmRespWaiting(Awaiter waiting) {
		sendSubmitSmResp.setWaiting(waiting);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendUnbindDelay} milliseconds
	 * before sending data.
	 *
	 * <strong>NOTE:</strong> If {@link #sendUnbindWaiting(Awaiter)} is also
	 * called then this delay has no effect.
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendUnbindDelay(long delayMs) {
		sendUnbind.setDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by executing {@code sendUnbindWaiting} function
	 * before sending data.
	 *
	 * <strong>NOTE:</strong> Calling {@link #sendUnbindDelay(long)} has no
	 * effect if waiting function is set.
	 * 
	 * @param waiting
	 *            the function to execute to simulate delay before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendUnbindWaiting(Awaiter waiting) {
		sendUnbind.setWaiting(waiting);
		return this;
	}

	/**
	 * Simulate slow server by waiting {@code sendUnbindRespDelay} milliseconds
	 * before sending data.
	 *
	 * <strong>NOTE:</strong> If {@link #sendUnbindRespWaiting(Awaiter)} is also
	 * called then this delay has no effect.
	 * 
	 * @param delayMs
	 *            the time to wait before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendUnbindRespDelay(long delayMs) {
		sendUnbindResp.setDelay(delayMs);
		return this;
	}

	/**
	 * Simulate slow server by executing {@code sendUnbindRespWaiting} function
	 * before sending data.
	 *
	 * <strong>NOTE:</strong> Calling {@link #sendUnbindRespDelay(long)} has no
	 * effect if waiting function is set.
	 * 
	 * @param waiting
	 *            the function to execute to simulate delay before sending data
	 * @return this instance for fluent chaining
	 */
	public SlowConfig sendUnbindRespWaiting(Awaiter waiting) {
		sendUnbindResp.setWaiting(waiting);
		return this;
	}

	public ServerDelays build() {
		ServerDelays delays = new ServerDelays();
		sendAlertNotification.apply(delays);
		sendBind.apply(delays);
		sendBindResp.apply(delays);
		sendCancelSm.apply(delays);
		sendCancelSmResp.apply(delays);
		sendDataSm.apply(delays);
		sendDataSmResp.apply(delays);
		sendDeliverSm.apply(delays);
		sendDeliverSmResp.apply(delays);
		sendEnquireLink.apply(delays);
		sendEnquireLinkResp.apply(delays);
		sendGenericNack.apply(delays);
		sendHeader.apply(delays);
		sendOutbind.apply(delays);
		sendQuerySm.apply(delays);
		sendQuerySmResp.apply(delays);
		sendReplaceSm.apply(delays);
		sendReplaceSmResp.apply(delays);
		sendSubmitMulti.apply(delays);
		sendSubmitMultiResp.apply(delays);
		sendSubmitSm.apply(delays);
		sendSubmitSmResp.apply(delays);
		sendUnbind.apply(delays);
		sendUnbindResp.apply(delays);
		return delays;
	}

	public static Awaiter waitFor(long delay) {
		return () -> {
			LOG.debug("Waiting for {}ms...", delay);
			final long end = System.currentTimeMillis() + delay;
			await().until(() -> System.currentTimeMillis() >= end);
		};
	}

	public static Awaiter noWait() {
		return () -> {};
	}

	private static class WaitingConfHelper {
		private final BiConsumer<ServerDelays, Awaiter> setter;
		private Long delay;
		private Awaiter waiting;

		public WaitingConfHelper(BiConsumer<ServerDelays, Awaiter> setter) {
			super();
			this.setter = setter;
		}

		public void setDelay(Long delay) {
			this.delay = delay;
		}

		public void setWaiting(Awaiter waiting) {
			this.waiting = waiting;
		}

		public void apply(ServerDelays delays) {
			if (waiting != null) {
				setter.accept(delays, waiting);
			}
			setter.accept(delays, delay == null || delay == 0 ? noWait() : waitFor(delay));
		}
	}
}
