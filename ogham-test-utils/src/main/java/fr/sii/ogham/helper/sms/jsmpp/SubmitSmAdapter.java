package fr.sii.ogham.helper.sms.jsmpp;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Stream;

import fr.sii.ogham.helper.sms.bean.Address;
import fr.sii.ogham.helper.sms.bean.Command;
import fr.sii.ogham.helper.sms.bean.OptionalParameter;
import fr.sii.ogham.helper.sms.bean.SimpleAddress;
import fr.sii.ogham.helper.sms.bean.SimpleCommand;
import fr.sii.ogham.helper.sms.bean.SubmitSm;
import fr.sii.ogham.helper.sms.bean.Tag;

/**
 * Adapts JSMPP {@link org.jsmpp.bean.SubmitSm} into {@link SubmitSm}
 * abstraction.
 * 
 * @author Aurélien Baudet
 *
 */
public class SubmitSmAdapter implements SubmitSm {
	protected final org.jsmpp.bean.SubmitSm original;

	/**
	 * @param original
	 *            the JSMPP {@link org.jsmpp.bean.SubmitSm} to adapt
	 */
	public SubmitSmAdapter(org.jsmpp.bean.SubmitSm original) {
		super();
		this.original = original;
	}

	@Override
	public Command getCommand() {
		return new SimpleCommand(original.getCommandLength(), original.getCommandId(), original.getCommandStatus(), original.getSequenceNumber());
	}

	@Override
	public String getServiceType() {
		return original.getServiceType();
	}

	@Override
	public Address getSourceAddress() {
		return new SimpleAddress(original.getSourceAddr(), original.getSourceAddrTon(), original.getSourceAddrNpi());
	}

	@Override
	public Address getDestAddress() {
		return new SimpleAddress(original.getDestAddress(), original.getDestAddrTon(), original.getDestAddrNpi());
	}

	@Override
	public byte getEsmClass() {
		return original.getEsmClass();
	}

	@Override
	public byte getRegisteredDelivery() {
		return original.getRegisteredDelivery();
	}

	@Override
	public byte getDataCoding() {
		return original.getDataCoding();
	}

	@Override
	public byte[] getShortMessage() {
		return original.getShortMessage();
	}

	@Override
	public int getShortMessageLength() {
		return original.getShortMessage().length;
	}

	@Override
	public OptionalParameter getOptionalParameter(Tag tag) {
		return new OptionalParameterAdapter(original, tag.getCode());
	}

	@Override
	public List<OptionalParameter> getOptionalParameters() {
		return Stream.of(original.getOptionalParameters()).map(p -> new OptionalParameterAdapter(original, p.tag)).collect(toList());
	}

	@Override
	public byte getPriorityFlag() {
		return original.getPriorityFlag();
	}

	@Override
	public byte getProtocolId() {
		return original.getProtocolId();
	}

	@Override
	public byte getReplaceIfPresentFlag() {
		return original.getReplaceIfPresent();
	}

	@Override
	public String getScheduleDeliveryTime() {
		return original.getScheduleDeliveryTime();
	}

	@Override
	public byte getSmDefaultMsgId() {
		return original.getSmDefaultMsgId();
	}

	@Override
	public String getValidityPeriod() {
		return original.getValidityPeriod();
	}

	@Override
	public boolean isUdhi() {
		return original.isUdhi();
	}

}
