package fr.sii.ogham.testing.helper.sms.bean;

/**
 * Represents a SMPP SubmitSm response.
 * 
 * @see "https://www.activexperts.com/sms-component/smpp-specifications/smpp-parameter-definition/"
 * 
 * @author Aurélien Baudet
 *
 */
public interface SubmitSmResponse extends SubmitSm {
	/**
	 * The unique message identifier reference assigned by the SMSC to each
	 * submitted short message. It is an opaque value and is set according to
	 * SMSC implementation. It is returned by the SMSC in the submit_sm_resp,
	 * submit_multi_resp, deliver_sm_resp and data_sm_resp PDUs and may be used
	 * by the ESME in subsequent SMPP operations relating to the short message,
	 * e.g. the ESME can use the query_sm operation to query a previously
	 * submitted message using the SMSC message_id as the message handle.
	 * 
	 * @return the mesage_id
	 */
	String getMessageId();
}
