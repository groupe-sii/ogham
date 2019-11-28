package fr.sii.ogham.helper.sms.bean;

/**
 * Simple command that just provide field values
 * 
 * @author Aurélien Baudet
 *
 */
public class SimpleCommand implements Command {
	private final int commandLength;
	private final int commandId;
	private final int commandStatus;
	private final int sequenceNumber;

	/**
	 * @param commandLength
	 *            the command length
	 * @param commandId
	 *            the command identifier
	 * @param commandStatus
	 *            the command status
	 * @param sequenceNumber
	 *            the sequence number
	 */
	public SimpleCommand(int commandLength, int commandId, int commandStatus, int sequenceNumber) {
		super();
		this.commandLength = commandLength;
		this.commandId = commandId;
		this.commandStatus = commandStatus;
		this.sequenceNumber = sequenceNumber;
	}

	@Override
	public int getCommandLength() {
		return commandLength;
	}

	@Override
	public int getCommandId() {
		return commandId;
	}

	@Override
	public int getCommandStatus() {
		return commandStatus;
	}

	@Override
	public int getSequenceNumber() {
		return sequenceNumber;
	}

}
