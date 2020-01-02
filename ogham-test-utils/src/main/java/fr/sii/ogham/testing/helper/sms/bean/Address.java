package fr.sii.ogham.testing.helper.sms.bean;

/**
 * Represents SMPP address field (TON, NPI and address).
 * 
 * @author Aurélien Baudet
 *
 */
public interface Address {
	/**
	 * Define the Type of Number (TON) to be used in the SME address parameters.
	 * 
	 * <ul>
	 * <li>Unknown 00000000</li>
	 * <li>International 00000001</li>
	 * <li>National 00000010</li>
	 * <li>Network Specific 00000011</li>
	 * <li>Subscriber Number 00000100</li>
	 * <li>Alphanumeric 00000101</li>
	 * <li>Abbreviated 00000110</li>
	 * <li>All other values reserved</li>
	 * </ul>
	 * 
	 * @return the Type Of Number
	 */
	byte getTon();

	/**
	 * define the Numeric Plan Indicator (NPI) to be used in the SME address
	 * parameters. The following NPI values are defined:
	 * <ul>
	 * <li>Unknown 00000000</li>
	 * <li>ISDN (E163/E164) 00000001</li>
	 * <li>Data (X.121) 00000011</li>
	 * <li>Telex (F.69) 00000100</li>
	 * <li>Land Mobile (E.212) 00000110</li>
	 * <li>National 00001000</li>
	 * <li>Private 00001001</li>
	 * <li>ERMES 00001010</li>
	 * <li>Internet (IP) 00001110</li>
	 * <li>WAP Client Id (to be defined by WAP Forum) 00010010</li>
	 * <li>All other values reserved</li>
	 * </ul>
	 * 
	 * @return the Numeric Plan Indicator
	 */
	byte getNpi();

	/**
	 * Get the address
	 * 
	 * @return the address
	 */
	String getAddress();
}
