package fr.sii.ogham.testing.helper.sms.bean;

/**
 * Optional Parameter tags
 * 
 * @author Aurélien Baudet
 *
 */
public enum Tag {
	/**
	 * <p>
	 * The payload_type parameter defines the higher layer PDU type contained in
	 * the message payload.
	 * </p>
	 * <table><caption>payload_type</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>payload_type</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of Value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>1</td>
	 * <td>Integer</td>
	 * <td>
	 * <p>
	 * 0 - Default. In the case of a WAP application, the default higher layer
	 * message type is a WDP message. See [WDP] for details.
	 * </p>
	 * <p>
	 * 1 - WCMP message. Wireless Control Message Protocol formatted data. See
	 * [WCMP] for details.
	 * </p>
	 * <p>
	 * values - 2 to 255 are reserved
	 * </p>
	 * </td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	PAYLOAD_TYPE("payload_type", 0x0019),
	/**
	 * <p>
	 * The privacy_indicator indicates the privacy level of the message.
	 * </p>
	 */
	PRIVACY_INDICATOR("privacy_indicator", 0x0201),
	/**
	 * <p>
	 * A reference assigned by the originating SME to the short message.
	 * </p>
	 * <table><caption>user_message_reference</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>user_message_reference</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>All values allowed.</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	USER_MESSAGE_REFERENCE("user_message_reference", 0x0204),
	/**
	 * <p>
	 * A response code set by the user in a User Acknowledgement/Reply message.
	 * The response codes are application specific.
	 * </p>
	 * <table><caption>user_response_code</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>user_response_code</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>1</td>
	 * <td>Integer</td>
	 * <td>0 to 255 (IS-95 CDMA)<br>
	 * 0 to 15 (CMT-136 TDMA)</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	USER_RESPONSE_CODE("user_response_code", 0x0205),
	/**
	 * <p>
	 * The source_port parameter is used to indicate the application port number
	 * associated with the source address of the message.
	 * </p>
	 * <table><caption>source_port</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>source_port</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>All values allowed.</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	SOURCE_PORT("source_port", 0x020A),
	/**
	 * <p>
	 * The destination_port parameter is used to indicate the application port
	 * number associated with the destination address of the message.
	 * </p>
	 * <table><caption>destination_port</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>destination_port</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>All values allowed.</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	DESTINATION_PORT("destination_port", 0x020B),
	/**
	 * <p>
	 * The sar_msg_ref_num parameter is used to indicate the reference number
	 * for a particular concatenated short message.
	 * </p>
	 * <table><caption>sar_msg_ref_num</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>sar_msg_ref_num</td>
	 * 
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>This parameter shall contain a originator generated reference number
	 * so that a segmented short message may be reassembled into a single
	 * original message. This allows the parallel transmission of several
	 * segmented messages. This reference number shall remain constant for every
	 * segment which makes up a particular concatenated short message. When
	 * present, the PDU must also contain the sar_total_segments and
	 * sar_segment_seqnum parameters. Otherwise this parameter shall be
	 * ignored.</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	SAR_MSG_REF_NUM("sar_msg_ref_num", 0x020C),
	/**
	 * <p>
	 * The language_indicator parameter is used to indicate the language of the
	 * short message.
	 * </p>
	 * <table><caption>lang_indicator</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>language_indicator</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>1</td>
	 * <td>Integer</td>
	 * <td>0 = unspecified (default)<br>
	 * 1 = english <br>
	 * 2 = french <br>
	 * 3 = spanish <br>
	 * 4 = german <br>
	 * 5 = Portuguese <br>
	 * refer to [CMT-136] for other values</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	LANGUAGE_INDICATOR("lang_indicator", 0x020D),
	/**
	 * <p>
	 * The sar_total_segments parameter is used to indicate the total number of
	 * short messages within the concatenated short message.
	 * </p>
	 * <table><caption>sar_total_segments</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>sar_total_segments</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>1</td>
	 * <td>Integer</td>
	 * <td>This parameter shall contain a value in the range 1 to 255 indicating
	 * the total number of fragments within the concatenated short message. The
	 * value shall start at 1 and remain constant for every short message which
	 * makes up the concatenated short message. When present, the PDU must also
	 * contain the sar_msg_ref_num and sar_segment_seqnum parameters. Otherwise
	 * this parameter shall be ignored.</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	SAR_TOTAL_SEGMENTS("sar_total_segments", 0x020E),
	/**
	 * <p>
	 * The sar_segment_seqnum parameter is used to indicate the sequence number
	 * of a particular short message within the concatenated short message.
	 * </p>
	 * <table><caption>sar_segment_seqnum</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>ar_segment_seqnum</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>1</td>
	 * <td>Integer</td>
	 * <td>This octet shall contain a value in the range 1 to 255 indicating the
	 * sequence number of a particular message within the concatenated short
	 * message. The value shall start at 1 and increment by one for every
	 * message sent within the concatenated short message. When present, the PDU
	 * must also contain the sar_total_segments and sar_msg_ref_num parameters.
	 * Otherwise this parameter shall be ignored.</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	SAR_SEGMENT_SEQNUM("sar_segment_seqnum", 0x020F),
	/**
	 * <p>
	 * The source_subaddress parameter specifies a subaddress associated with
	 * the originator of the message.
	 * </p>
	 */
	SOURCE_SUBADDRESS("source_subaddress", 0x0202),
	/**
	 * <p>
	 * The dest_subaddress parameter specifies a subaddress associated with the
	 * destination of the message.
	 * </p>
	 * <table><caption>dest_subaddress</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>dest_subaddress</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of Value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>Var 2 - 23</td>
	 * <td>Octet String</td>
	 * <td>See 5.3.2.15 for parameter encoding.</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	DEST_SUBADDRESS("dest_subaddress", 0x0203),
	/**
	 * <p>
	 * The callback_num parameter associates a call back number with the
	 * message. In TDMA networks, it is possible to send and receive multiple
	 * callback numbers to/from TDMA mobile stations.
	 * </p>
	 * <table><caption>callback_num</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>&nbsp;</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of Value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>Var 4 - 19</td>
	 * <td>Octet String</td>
	 * <td>
	 * <p>
	 * <b>Bits 7.............0</b>
	 * </p>
	 * <p>
	 * 0000000D (octet 1)
	 * </p>
	 * <p>
	 * 00000TTT (octet 2)
	 * </p>
	 * <p>
	 * 0000NNNN (octet 3)
	 * </p>
	 * <p>
	 * XXXXXXXX (octet 4)
	 * </p>
	 * <p>
	 * :
	 * </p>
	 * <p>
	 * :
	 * </p>
	 * <p>
	 * XXXXXXXX (octet N)
	 * </p>
	 * <p>
	 * The originating SME can set a Call Back Number for the receiving Mobile
	 * Station. The first octet contains the Digit Mode Indicator. Bit D=0
	 * indicates that the Call Back Number is sent to the mobile as DTMF digits
	 * encoded in TBCD. Bit D=1 indicates that the Call Back Number is sent to
	 * the mobile encoded as ASCII digits. The 2nd octet contains the Type of
	 * Number (TON). Encoded as in section 5.2.5.
	 * </p>
	 * <p>
	 * The third octet contains the Numbering Plan Indicator (NPI). Encoded as
	 * specified in section 5.2.6
	 * </p>
	 * <p>
	 * The remaining octets contain the Call Back Number digits encoded as ASCII
	 * characters
	 * </p>
	 * </td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	CALLBACK_NUM("callback_num", 0x0381),
	/**
	 * <p>
	 * The message_payload parameter contains the user data.
	 * </p>
	 * <table><caption>message_payload</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>message_payload</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Set to length of user data</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>Variable</td>
	 * <td>Octet String</td>
	 * <td>Short message user data. The maximum size is SMSC and network
	 * implementation specific.</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	MESSAGE_PAYLOAD("message_payload", 0x0424),
	/**
	 * <p>
	 * The sc_interface_version parameter is used to indicate the SMPP version
	 * supported by the SMSC. It is returned in the bind response PDUs.
	 * </p>
	 * <table><caption>sc_interface_version</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>sc_interface_version</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>1</td>
	 * <td>Integer</td>
	 * <td>values as per 5.2.4. (interface_version)</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	SC_INTERFACE_VERSION("sc_interface_version", 0x0210),
	/**
	 * <p>
	 * The display_time parameter is used to associate a display time of the
	 * short message on the MS.
	 * </p>
	 * <table><caption>display_time</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>display_time</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>1</td>
	 * <td>Integer</td>
	 * <td>0 = Temporary<br>
	 * 1 = Default (default)<br>
	 * 2 = Invoke<br>
	 * values 3 to 255 are reserved</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	DISPLAY_TIME("display_time", 0x1201),
	/**
	 * <p>
	 * The ms_validity parameter is used to provide an MS with validity
	 * information associated with the received short message.
	 * </p>
	 * <table><caption>ms_validity</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>ms_validity</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>1</td>
	 * <td>Integer</td>
	 * <td>0 = Store Indefinitely (default)<br>
	 * 1 = Power Down <br>
	 * 2 = SID based registration area <br>
	 * 3 = Display Only <br>
	 * values 4 to 255 are reserved</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	MS_VALIDITY("ms_validity", 0x1204),
	/**
	 * <p>
	 * The dpf_result parameter is used in the data_sm_resp PDU to indicate if
	 * delivery pending flag (DPF) was set for a delivery failure of the short
	 * message..
	 * </p>
	 * <p>
	 * If the dpf_result parameter is not included in the data_sm_resp PDU, the
	 * ESME should assume that DPF is not set.
	 * </p>
	 * <p>
	 * Currently this parameter is only applicable for the Transaction message
	 * mode.
	 * </p>
	 * <table><caption>dpf_result</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>dpf_result</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>1</td>
	 * <td>Integer</td>
	 * <td>0 = DPF not set<br>
	 * 1 = DPF set<br>
	 * values 2 to 255 are reserved</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	DPF_RESULT("dpf_result", 0x0420),
	/**
	 * <p>
	 * An ESME may use the set_dpf parameter to request the setting of a
	 * delivery pending flag (DPF) for certain delivery failure scenarios, such
	 * as
	 * </p>
	 * <ul>
	 * <li>MS is unavailable for message delivery (as indicated by the HLR)</li>
	 * </ul>
	 * <p>
	 * The SMSC should respond to such a request with an alert_notification PDU
	 * when it detects that the destination MS has become available.
	 * </p>
	 * <p>
	 * The delivery failure scenarios under which DPF is set is SMSC
	 * implementation and network implementation specific. If a delivery pending
	 * flag is set by the SMSC or network (e.g. HLR), then the SMSC should
	 * indicate this to the ESME in the data_sm_resp message via the dpf_result
	 * parameter.
	 * </p>
	 * <table><caption>set_dpf</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>set_dpf</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>length of value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>1</td>
	 * <td>Integer</td>
	 * <td>0 = Setting of DPF for delivery failure to MS not requested<br>
	 * 1 = Setting of DPF for delivery failure requested (default) <br>
	 * values 2 to 255 are reserved</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	SET_DPF("set_dpf", 0x0421),
	/**
	 * <p>
	 * The ms_availability_status parameter is used in the alert_notification
	 * operation to indicate the availability state of the MS to the ESME.
	 * </p>
	 * <p>
	 * If the SMSC does not include the parameter in the alert_notification
	 * operation, the ESME should assume that the MS is in an "available" state.
	 * </p>
	 * <table><caption>ms_availability_status</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>ms_availability_status</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>1</td>
	 * <td>Integer</td>
	 * <td>0 = Available (Default)<br>
	 * 1 = Denied (e.g. suspended, no SMS capability, etc.)<br>
	 * 2 = Unavailable values 3 to 255 are reserved</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	MS_AVAILABILITY_STATUS("ms_availability_status", 0x0422),
	/**
	 * <p>
	 * The network_error_code parameter is used to indicate the actual network
	 * error code for a delivery failure. The network error code is technology
	 * specific.
	 * </p>
	 * <table><caption>network_error_code</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>network_error_code</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>3</td>
	 * <td>Octet String</td>
	 * <td>
	 * <p>
	 * The first octet indicates the network type. The following values are
	 * defined:
	 * </p>
	 * <ul>
	 * <li>1 = ANSI-136</li>
	 * <li>2 = IS-95</li>
	 * <li>3 = GSM</li>
	 * <li>4 = Reserved</li>
	 * <li>All other values reserved.</li>
	 * </ul>
	 * <p>
	 * The remaining two octets specify the actual network error code
	 * appropriate to the network type.
	 * </p>
	 * </td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	NETWORK_ERROR_CODE("network_error_code", 0x0423),
	/**
	 * <p>
	 * The delivery_failure_reason parameter is used in the data_sm_resp
	 * operation to indicate the outcome of the message delivery attempt (only
	 * applicable for transaction message mode). If a delivery failure due to a
	 * network error is indicated, the ESME may check the network_error_code
	 * parameter (if present) for the actual network error code.
	 * </p>
	 * <p>
	 * The delivery_failure_reason parameter is not included if the delivery
	 * attempt was successful.
	 * </p>
	 * <table><caption>delivery_failure_reason</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>delivery_failure_reason</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>1</td>
	 * <td>Integer</td>
	 * <td>0 = Destination unavailable<br>
	 * 1 = Destination Address Invalid (e.g.<br>
	 * suspended, no SMS capability, etc.)<br>
	 * 2 = Permanent network error<br>
	 * 3 = Temporary network error<br>
	 * values 4 to are 255 reserved</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	DELIVERY_FAILURE_REASON("delivery_failure_reason", 0x0425),
	/**
	 * <p>
	 * The more_messages_to_send parameter is used by the ESME in the submit_sm
	 * and data_sm operations to indicate to the SMSC that there are further
	 * messages for the same destination SME. The SMSC may use this setting for
	 * network resource optimization.
	 * </p>
	 * <table><caption>more_messages_to_send</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>more_messages_to_send</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>1</td>
	 * <td>&nbsp;</td>
	 * <td>0 = No more messages to follow; 1 = More messages to follow (default)
	 * values 2 to 255 are reserved</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	MORE_MESSAGES_TO_SEND("more_messages_to_send", 0x0426),
	/**
	 * <p>
	 * The message_state optional parameter is used by the SMSC in the
	 * deliver_sm and data_sm PDUs to indicate to the ESME the final message
	 * state for an SMSC Delivery Receipt.
	 * </p>
	 * <table><caption>message_state</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>message_state</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>1</td>
	 * <td>&nbsp;</td>
	 * <td>Values as per section 5.2.28</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	MESSAGE_STATE("message_state", 0x0427),
	/**
	 * <table><caption>callback_num_pres_ind</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>callback_num_pres_ind</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of Value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>1</td>
	 * <td>Bit mask</td>
	 * <td>
	 * <p>
	 * <b> Bits 7............0<br>
	 * 0000ppss </b>
	 * </p>
	 * <p>
	 * This parameter controls the presentation indication and screening of the
	 * CallBackNumber at the mobile station.If present, the callback_num
	 * parameter must also be present.
	 * </p>
	 * <p>
	 * The Presentation Indicator is encoded in bits 2 and 3 as follows:
	 * </p>
	 * <p>
	 * 00 = Presentation Allowed
	 * </p>
	 * <p>
	 * 01 = Presentation Restricted
	 * </p>
	 * <p>
	 * 10 = Number Not Available
	 * </p>
	 * <p>
	 * 11 = Reserved
	 * </p>
	 * 
	 * <p>
	 * The Screening Indicator is encoded in bits 0 and 1 as follows:
	 * </p>
	 * <p>
	 * 00 = User provided, not screened
	 * </p>
	 * <p>
	 * 01 = User provided, verified and passed
	 * </p>
	 * <p>
	 * 10 = User provided, verified and failed
	 * </p>
	 * <p>
	 * 11 = Network Provided.
	 * </p>
	 * </td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	CALLBACK_NUM_PRES_IND("callback_num_pres_ind", 0x0302),
	/**
	 * <p>
	 * The callback_num_atag parameter associates an alphanumeric display with
	 * the call back number.
	 * </p>
	 * <table><caption>callback_num_atag</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>callback_num_atag</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of Value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>Var max 65</td>
	 * <td>Octet string</td>
	 * <td>
	 * <p>
	 * Alphanumeric display tag for call back number
	 * </p>
	 * 
	 * <pre class="aclSourceCodeInTable">
	Bits 7...............0
	EEEEEEEE (octet 1)
	XXXXXXXX (octet 2)
	:
	:
	XXXXXXXX (octet N)
	 * </pre>
	 * <p>
	 * The first octet contains the encoding scheme of the Alpha Tag display
	 * characters. This field contains the same values as for Data Coding Scheme
	 * (see section 5.2.19). The following octets contain the display
	 * characters: There is one octet per display character for 7-bit and 8-bit
	 * encoding schemes. There are two octets per display character for 16-bit
	 * encoding schemes.
	 * </p>
	 * </td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	CALLBACK_NUM_ATAG("callback_num_atag", 0x0303),
	/**
	 * <p>
	 * The number_of_messages parameter is used to indicate the number of
	 * messages stored in a mailbox.
	 * </p>
	 * <table><caption>number_of_messages</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>number_of_messages</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of Value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>1</td>
	 * <td>Integer</td>
	 * <td>0 to 99 = allowed values. Values 100 to 255 are reserved</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	NUMBER_OF_MESSAGES("number_of_messages", 0x0304),
	/**
	 * <p>
	 * The sms_signal parameter is used to provide a TDMA MS with alert tone
	 * information associated with the received short message.
	 * </p>
	 * <table><caption>sms_signal</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>sms_signal</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of Value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Encoded as per [CMT-136]</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	SMS_SIGNAL("sms_signal", 0x1203),
	/**
	 * <p>
	 * The alert_on_message_delivery parameter is set to instruct a MS to alert
	 * the user (in a MS implementation specific manner) when the short message
	 * arrives at the MS.
	 * </p>
	 * <table><caption>alert_on_message_delivery</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>alert_on_message_delivery</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of Value part in octets (= 0)</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>0</td>
	 * <td>No Value</td>
	 * <td>&nbsp;</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	ALERT_ON_MESSAGE_DELIVERY("alert_on_message_delivery", 0x130C),
	/**
	 * <p>
	 * The its_reply_type parameter is a required parameter for the CDMA
	 * Interactive Teleservice as defined by the Korean PCS carriers [KORITS].
	 * It indicates and controls the MS user's reply method to an SMS delivery
	 * message received from the ESME.
	 * </p>
	 * <table><caption>its_reply_type</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>its_reply_type</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of Value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>1</td>
	 * <td>Integer</td>
	 * <td>
	 * 
	 * <pre class="aclSourceCodeInTable">
	 * 0 = Digit 
	1 = Number 
	2 = Telephone No. 
	3 = Password 
	4 = Character Line 
	5 = Menu 
	6 = Date 
	7 = Time 
	8 = Continue 
	values 9 to 255 are reserved
	 * </pre>
	 * 
	 * </td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	ITS_REPLY_TYPE("its_reply_type", 0x1380),
	/**
	 * <p>
	 * The its_session_info parameter is a required parameter for the CDMA
	 * Interactive Teleservice as defined by the Korean PCS carriers [KORITS].
	 * It contains control information for the interactive session between an MS
	 * and an ESME.
	 * </p>
	 * <table><caption>its_session_info</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>its_session_info</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of Value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>2</td>
	 * <td>Octet String</td>
	 * <td>
	 * 
	 * <pre class="aclSourceCodeInTable">
	 * Bits 7...............0 
	SSSS SSSS (octet 1) 
	NNNN NNNE (octet 2)
	 * </pre>
	 * <p>
	 * Octet 1 contains the session number (0 - 255) encoded in binary. The
	 * session number remains constant for each session.
	 * </p>
	 * <p>
	 * The sequence number of the dialogue unit (as assigned by the ESME) within
	 * the session is encoded in bits 7..1 of octet 2.
	 * </p>
	 * <p>
	 * The End of Session Indicator indicates the message is the end of the
	 * conversation session and is encoded in bit 0 of octet 2 as follows: 0 =
	 * End of Session Indicator inactive. 1 = End of Session Indicator active.
	 * </p>
	 * </td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	ITS_SESSION_INFO("its_session_info", 0x1383),
	/**
	 * <p>
	 * The ussd_service_op parameter is required to define the USSD service
	 * operation when SMPP is being used as an interface to a (GSM) USSD system.
	 * </p>
	 * <table><caption>ussd_service_op</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>ussd_service_op</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of Value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>1</td>
	 * <td>Octet String</td>
	 * <td>
	 * 
	 * <pre class="aclSourceCodeInTable">
	 * 0 = PSSD indication 
	1 = PSSR indication 
	2 = USSR request 
	3 = USSN request 
	4 to 15 = reserved 
	
	16 = PSSD response 
	17 = PSSR response 
	18 = USSR confirm 
	19 = USSN confirm 
	
	20 to 31 = reserved 
	32 to 255 = reserved for vendor specific 
	USSD operations
	 * </pre>
	 * 
	 * </td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	USSD_SERVICE_OP("ussd_service_op", 0x0501),
	/**
	 * Billing information passed from ESME to MC <br>
	 * <br>
	 * Bits 7......0 <br>
	 * 0XXXXXXX (Reserved) <br>
	 * 1XXXXXXX (Vendor Specific) <br>
	 * The first octet represents the Billing Format tag and indicates the
	 * format of the billing information in the remaining octets. <br>
	 * The remaining octets contain the billing information. <br>
	 * <br>
	 * Wireless Network Technology: Generic
	 *
	 */
	BILLING_IDENTIFICATION("billing_identification", 0x060B),
	/**
	 * <p>
	 * The dest_addr_subunit parameter is used to route messages when received
	 * by a mobile station, for example to a smart card in the mobile station or
	 * to an external device connected to the mobile station.
	 * </p>
	 * <table><caption>dest_addr_subunit</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>dest_addr_subunit</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of Value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>1</td>
	 * <td>Integer</td>
	 * <td>0x00 = Unknown (default)<br>
	 * 0x01 = MS Display <br>
	 * 0x02 = Mobile Equipment<br>
	 * 0x03 = Smart Card 1 (expected to be SIM if<br>
	 * a SIM exists in the MS)<br>
	 * 0x04 = External Unit 1<br>
	 * 5 to 255 = reserved</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	DEST_ADDR_SUBUNIT("dest_addr_subunit", 0x0005),
	/**
	 * The dest_network_type parameter is used to indicate a network type
	 * associated with the destination address of a message. In the case that
	 * the receiving system (e.g. SMSC) does not support the indicated network
	 * type, it may treat this a failure and return a response PDU reporting a
	 * failure.
	 */
	DEST_NETWORK_TYPE("dest_network_type", 0x0006),
	/**
	 * <p>
	 * The dest_bearer_type parameter is used to request the desired bearer for
	 * delivery of the message to the destination address. In the case that the
	 * receiving system (e.g. SMSC) does not support the indicated bearer type,
	 * it may treat this a failure and return a response PDU reporting a
	 * failure.
	 * </p>
	 * <table><caption>dest_bearer_type</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>dest_bearer_type</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of Value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>1</td>
	 * <td>Integer</td>
	 * <td>0x00 = Unknown<br>
	 * 0x01 = SMS<br>
	 * 0x02 = Circuit Switched Data (CSD)<br>
	 * 0x03 = Packet Data<br>
	 * 0x04 = USSD<br>
	 * 0x05 = CDPD<br>
	 * 0x06 = DataTAC<br>
	 * 0x07 = FLEX/ReFLEX<br>
	 * 0x08 = Cell Broadcast (cellcast)<br>
	 * 9 to 255 = reserved</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	DEST_BEARER_TYPE("dest_bearer_type", 0x0007),
	/**
	 * <p>
	 * This parameter defines the telematic interworking to be used by the
	 * delivering system for the destination address. This is only useful when a
	 * specific dest_bearer_type parameter has also been specified as the value
	 * is bearer dependent. In the case that the receiving system (e.g. SMSC)
	 * does not support the indicated telematic interworking, it may treat this
	 * a failure and return a response PDU reporting a failure.
	 * </p>
	 * <table><caption>dest_telematics_id</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>dest_telematics_id</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of Value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>to be defined</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	DEST_TELEMATICS_ID("dest_telematics_id", 0x0008),
	/**
	 * <p>
	 * The source_addr_subunit parameter is used to indicate where a message
	 * originated in the mobile station, for example a smart card in the mobile
	 * station or an external device connected to the mobile station.
	 * </p>
	 * <table><caption>source_addr_subunit</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>source_addr_subunit</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of Value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>1</td>
	 * <td>Integer</td>
	 * <td>see 5.3.2.1</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	SOURCE_ADDR_SUBUNIT("source_addr_subunit", 0x000D),
	/**
	 * <p>
	 * The source_network_type parameter is used to indicate the network type
	 * associated with the device that originated the message.
	 * </p>
	 * <table><caption>source_network_type</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>source_network_type</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of Value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>1</td>
	 * <td>Integer</td>
	 * <td>see 5.3.2.3</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	SOURCE_NETWORK_TYPE("source_network_type", 0x000E),
	/**
	 * <p>
	 * The source_bearer_type parameter indicates the wireless bearer over which
	 * the message originated.
	 * </p>
	 * <table><caption>source_bearer_type</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>source_bearer_type</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of Value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>1</td>
	 * <td>Integer</td>
	 * <td>see 5.3.2.5</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	SOURCE_BEARER_TYPE("source_bearer_type", 0x000F),
	/**
	 * <p>
	 * The source_telematics_id parameter indicates the type of telematics
	 * interface over which the message originated.
	 * </p>
	 * <table><caption>source_telematics_id</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>source_telematics_id</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of Value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>1</td>
	 * <td>Integer</td>
	 * <td>see 5.3.2.7</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	SOURCE_TELEMATICS_ID("source_telematics_id", 0x0010),
	/**
	 * <p>
	 * This parameter defines the number of seconds which the sender requests
	 * the SMSC to keep the message if undelivered before it is deemed expired
	 * and not worth delivering. If the parameter is not present, the SMSC may
	 * apply a default value.
	 * </p>
	 * <table><caption>qos_time_to_live</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>qos_time_to_live</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of Value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>4</td>
	 * <td>Integer</td>
	 * <td>number of seconds for message to be retained by the receiving
	 * system.</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	QOS_TIME_TO_LIVE("qos_time_to_live", 0x0017),
	/**
	 * <p>
	 * The additional_status_info_text parameter gives an ASCII textual
	 * description of the meaning of a response PDU. It is to be used by an
	 * implementation to allow easy diagnosis of problems.
	 * </p>
	 * <table><caption>additional_status_info</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>additional_status_info_text</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of Value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>1 - 256</td>
	 * <td>C Octet String</td>
	 * <td>Free format text to allow implementations to supply the most useful
	 * information for problem diagnosis. Maximum length is 256 octets.</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	ADDITIONAL_STATUS_INFO("additional_status_info", 0x001D),
	/**
	 * <p>
	 * The receipted_message_id parameter indicates the ID of the message being
	 * receipted in an SMSC Delivery Receipt. This is the opaque SMSC message
	 * identifier that was returned in the message_id parameter of the SMPP
	 * response PDU that acknowledged the submission of the original message.
	 * </p>
	 * <table><caption>receipted_message_id</caption>
	 * <thead>
	 * <tr>
	 * <th>Field Name</th>
	 * <th>Size octets</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>Parameter Tag</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>receipted_message_id</td>
	 * </tr>
	 * <tr>
	 * <td>Length</td>
	 * <td>2</td>
	 * <td>Integer</td>
	 * <td>Length of Value part in octets</td>
	 * </tr>
	 * <tr>
	 * <td>Value</td>
	 * <td>1 - 65</td>
	 * <td>C Octet String</td>
	 * <td>SMSC handle of the message being receipted.</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	RECEIPTED_MESSAGE_ID("receipted_message_id", 0x001E),
	/**
	 * <p>
	 * The ms_msg_wait_facilities parameter allows an indication to be provided
	 * to an MS that there are messages waiting for the subscriber on systems on
	 * the PLMN. The indication can be an icon on the MS screen or other MMI
	 * indication.
	 * </p>
	 * <p>
	 * The ms_msg_wait_facilities can also specify the type of message
	 * associated with the message waiting indication.
	 * </p>
	 */
	MS_MSG_WAIT_FACILITIES("ms_msg_wait_facilities", 0x0030);

	private final String tagName;
	private final short code;

	Tag(String tagName, int code) {
		this.code = (short) code;
		this.tagName = tagName;
	}

	/**
	 * @return the tag name
	 */
	public String getTagName() {
		return tagName;
	}

	/**
	 * @return the code of the tag
	 */
	public short getCode() {
		return code;
	}

}
