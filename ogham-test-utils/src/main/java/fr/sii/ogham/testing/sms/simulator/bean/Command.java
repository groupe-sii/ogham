package fr.sii.ogham.testing.sms.simulator.bean;

/**
 * Represents SMPP Command. Contains only the header of SMPP PDU.
 * 
 * @author Aurélien Baudet
 *
 */
public interface Command {

	/**
	 * Get the command_length.
	 * 
	 * @return the command_length.
	 */
	int getCommandLength();

	/**
	 * Get the command_id.
	 * 
	 * @return the command_id.
	 */
	int getCommandId();

	/**
	 * Get the command_status.
	 * 
	 * @return the command_status.
	 */
	int getCommandStatus();

	/**
	 * Get the sequence_number.
	 * 
	 * @return the value of sequence_number.
	 */
	int getSequenceNumber();

}