package fr.sii.ogham.sms.builder.cloudhopper;

import java.util.Arrays;
import java.util.List;

import com.cloudhopper.smpp.SmppConstants;

import fr.sii.ogham.core.convert.StringToEnumConverter.FactoryMethod;

/**
 * Wraps the version for builder
 * 
 * @author Aurélien Baudet
 *
 */
@FactoryMethod(name = "of")
public enum InterfaceVersion {
	/**
	 * SMPP 3.3 the oldest used version (despite its limitations, it is still
	 * widely used); supports GSM only. Generates an immediate response for each
	 * message sent.
	 */
	VERSION_3_3(SmppConstants.VERSION_3_3, "3.3"),
	/**
	 * SMPP 3.4 adds optional Tag-Length-Value (TLV) parameters, support of
	 * non-GSM SMS technologies and the transceiver support (single connections
	 * that can send and receive messages). The exchange of SMPP request and
	 * response PDUs between an ESME Transmitter and SMSC may occur
	 * synchronously or asynchronously.
	 */
	VERSION_3_4(SmppConstants.VERSION_3_4, "3.4"),
	/**
	 * SMPP 5.0 is the latest version of SMPP; adds support for cell
	 * broadcasting, smart flow control. As of 2019, it is not widely used.
	 */
	VERSION_5_0(SmppConstants.VERSION_5_0, "5.0", "5");

	private final byte value;
	private final List<String> versionNames;

	InterfaceVersion(byte value, String... versionNames) {
		this.value = value;
		this.versionNames = Arrays.asList(versionNames);
	}

	/**
	 * Get the version as byte value.
	 * 
	 * @return the byte value for the version
	 */
	public byte value() {
		return value;
	}

	private boolean matches(String versionName) {
		for (String v : versionNames) {
			if (v.equals(versionName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the {@link InterfaceVersion} from the byte value.
	 * 
	 * If value is {@code null}, {@code null} is returned
	 * 
	 * @param value
	 *            the byte value that represents the version
	 * @return the corresponding interface version
	 * @throws IllegalArgumentException
	 *             if the value doesn't match any known version
	 */
	public static InterfaceVersion fromValue(Byte value) {
		if (value == null) {
			return null;
		}
		for (InterfaceVersion version : values()) {
			if (version.value() == value) {
				return version;
			}
		}
		throw new IllegalArgumentException("Unknown interface version with value " + value);
	}

	/**
	 * Get the {@link InterfaceVersion} from either the enum name (
	 * {@link InterfaceVersion#VERSION_3_3},
	 * {@link InterfaceVersion#VERSION_3_4},
	 * {@link InterfaceVersion#VERSION_5_0}) or from a string that represents
	 * the version ("3.3", "3.4", "5.0").
	 * 
	 * If version name is {@code null}, {@code null} is returned
	 * 
	 * @param version
	 *            the version as string
	 * @return the interface version
	 * @throws IllegalArgumentException
	 *             if the version name doesn't match any known version
	 */
	public static InterfaceVersion of(String version) {
		if (version == null || version.isEmpty()) {
			return null;
		}
		for (InterfaceVersion iv : values()) {
			if (iv.name().equals(version)) {
				return iv;
			}
			if (iv.matches(version)) {
				return iv;
			}
		}
		throw new IllegalArgumentException("Unknown interface version named " + version);
	}
}
