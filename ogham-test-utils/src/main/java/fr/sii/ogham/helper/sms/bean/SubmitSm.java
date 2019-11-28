package fr.sii.ogham.helper.sms.bean;

import java.util.List;

/**
 * Abstraction to be able to work with any simulator (JSMPP, Cloudhopper,
 * OpenSmpp, ...)
 * 
 * @author Aurélien Baudet
 *
 */
public interface SubmitSm {

	/**
	 * Get information relative to the command (command id, command length,
	 * command status and sequence number)
	 * 
	 * @return the command wrapper
	 */
	Command getCommand();

	/**
	 * The service_type parameter can be used to indicate the SMS Application
	 * service associated with the message. Specifying the service_type allows
	 * the ESME to:
	 * <ul>
	 * <li>Avail of enhanced messaging services such as message
	 * 'replace_if_present' by service type (generic).</li>
	 * <li>Control the teleservice used on the air interface (e.g.
	 * ANSI-136/TDMA, IS-95/CDMA).</li>
	 * </ul>
	 * 
	 * SMSC's may implicitly associate a "replace if present" function from the
	 * indicated service_type in a message submission operation, i.e., the SMSC
	 * will always replace an existing message pending delivery, that has the
	 * same originating and destination address as the submitted message. For
	 * example, an SMSC can ensure that a Voice Mail System using a service_type
	 * of 'VMA" has at most one outstanding notification per destination MS by
	 * automatically invoking the "replace if present" function.
	 * 
	 * @return the serviceType
	 */
	String getServiceType();

	/**
	 * Specifies the address of SME which originated this message. An ESME which
	 * is implemented as a single SME address, may set this field to NULL to
	 * allow the SMSC to default the source address of the submitted message.
	 * 
	 * @return source address (TON, NPI and address)
	 */
	Address getSourceAddress();

	/**
	 * Specifies the destination SME address. For mobile terminated messages,
	 * this is the directory number of the recipient MS.
	 * 
	 * @return destination address (TON, NPI and address)
	 */
	Address getDestAddress();

	/**
	 * The 'esm_class' parameter is used to indicate special message attributes
	 * associated with the short message.
	 * 
	 * @return the 'esm_class' parameter
	 */
	byte getEsmClass();

	/**
	 * The registered_delivery parameter is used to request an SMSC delivery
	 * receipt and/or SME originated acknowledgements.
	 * 
	 * @return the registered delivery parameter
	 */
	byte getRegisteredDelivery();

	/**
	 * Get the 'data_coding' field
	 * 
	 * @return the 'data_coding' field
	 */
	byte getDataCoding();

	/**
	 * The short_message parameter contains the user data. A maximum of 254
	 * octets can be sent. ESME's should use the optional message_payload
	 * parameter in submit_sm, submit_multi, and deliver_sm to send larger user
	 * data sizes.
	 * 
	 * Returns the short_message field. It contains the message payload. It also
	 * may contain a header (User Data Header).
	 * 
	 * @return the short message (header? + payload)
	 */
	byte[] getShortMessage();

	/**
	 * The sm_length parameter specifies the length of the short_message
	 * parameter in octets. The sm_length should be set to 0 in the submit_sm,
	 * submit_multi, and deliver_sm PDUs if the message_payload parameter is
	 * being used to send user data larger than 254 octets.
	 * 
	 * @return the sm_length parameter
	 */
	int getShortMessageLength();

	/**
	 * Get Optional Parameter that matches the specified tag.
	 * 
	 * Optional Parameters are fields, which may be optionally included in an
	 * SMPP message. Optional Parameters must always appear at the end of a
	 * message, in the "Optional Parameters" section of the SMPP PDU. However,
	 * they may be included in any convenient order within the "Optional
	 * Parameters" section of the SMPP PDU and need not be encoded in the order
	 * presented in this document.
	 * 
	 * For a particular SMPP PDU, the ESME or SMSC may include some, all or none
	 * of the defined optional parameters as required for the particular
	 * application context. For example a paging system may in an SMPP submit_sm
	 * operation, include only the "callback number" related optional
	 * parameters.
	 * 
	 * @param tag
	 *            the tag of the optional parameter
	 * @return the optional parameter if exists or null
	 */
	OptionalParameter getOptionalParameter(Tag tag);

	/**
	 * Get the list of all Optional Parameters.
	 * 
	 * Optional Parameters are fields, which may be optionally included in an
	 * SMPP message. Optional Parameters must always appear at the end of a
	 * message, in the "Optional Parameters" section of the SMPP PDU. However,
	 * they may be included in any convenient order within the "Optional
	 * Parameters" section of the SMPP PDU and need not be encoded in the order
	 * presented in this document.
	 * 
	 * For a particular SMPP PDU, the ESME or SMSC may include some, all or none
	 * of the defined optional parameters as required for the particular
	 * application context. For example a paging system may in an SMPP submit_sm
	 * operation, include only the "callback number" related optional
	 * parameters.
	 * 
	 * @return the list of parameters
	 */
	List<OptionalParameter> getOptionalParameters();

	/**
	 * The priority_flag parameter allows the originating SME to assign a
	 * priority level to the short message.
	 * 
	 * Four Priority Levels are supported:
	 * 
	 * <ul>
	 * <li>0 = Level 0 (lowest) priority</li>
	 * <li>1 = Level 1</li>
	 * <li>priority 2 = Level 2</li>
	 * <li>priority 3 = Level 3 (highest) priority</li>
	 * <li>&gt;3 = Reserved</li>
	 * </ul>
	 * 
	 * @return the priority
	 */
	byte getPriorityFlag();

	/**
	 * <h3>GSM</h3>
	 * 
	 * Set according to GSM 03.40 [GSM 03.40]
	 * 
	 * <h3>ANSI-136 (TDMA)</h3>
	 * 
	 * For mobile terminated messages, this field is not used and is therefore
	 * ignored by the SMSC. For ANSI-136 mobile originated messages, the SMSC
	 * should set this value to NULL.
	 * 
	 * <h3>IS-95 (CDMA)</h3>
	 * 
	 * For mobile terminated messages, this field is not used and is therefore
	 * ignored by the SMSC. For IS-95 mobile originated messages, the SMSC
	 * should set this value to NULL.
	 * 
	 * @return the protocolId
	 */
	byte getProtocolId();

	/**
	 * The replace_if_present_flag parameter is used to request the SMSC to
	 * replace a previously submitted message, that is still pending delivery.
	 * The SMSC will replace an existing message provided that the source
	 * address, destination address and service_type match the same fields in
	 * the new message.
	 * 
	 * @return the 'replace_if_present_flag' field
	 */
	byte getReplaceIfPresentFlag();

	/**
	 * This parameter specifies the scheduled time at which the message delivery
	 * should be first attempted.
	 * 
	 * It defines either the absolute date and time or relative time from the
	 * current SMSC time at which delivery of this message will be attempted by
	 * the SMSC.
	 * 
	 * It can be specified in either absolute time format or relative time
	 * format. The encoding of a time format is specified in Section 7.1.1.
	 * 
	 * @return the scheduleDeliveryTime
	 */
	String getScheduleDeliveryTime();

	/**
	 * The sm_default_msg_id parameter specifies the SMSC index of a pre-defined
	 * ('canned') message.
	 * 
	 * @return the sm_default_msg_id
	 */
	byte getSmDefaultMsgId();

	/**
	 * The validity_period parameter indicates the SMSC expiration time, after
	 * which the message should be discarded if not delivered to the
	 * destination. It can be defined in absolute time format or relative time
	 * format. The encoding of absolute and relative time format is specified in
	 * Section 7.1.1.
	 * 
	 * @return the validityPeriod
	 */
	String getValidityPeriod();

	/**
	 * Specific Features.
	 *
	 * @return if the esmClass contains the User Data Header Indicator
	 */
	boolean isUdhi();

}