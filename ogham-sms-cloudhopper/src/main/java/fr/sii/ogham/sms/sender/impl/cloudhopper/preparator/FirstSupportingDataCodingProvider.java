package fr.sii.ogham.sms.sender.impl.cloudhopper.preparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cloudhopper.commons.gsm.DataCoding;

import fr.sii.ogham.sms.encoder.Encoded;
import fr.sii.ogham.sms.sender.impl.cloudhopper.exception.DataCodingException;

/**
 * Execute registered {@link DataCodingProvider}s until one returns a non-null
 * value and returns this value.
 * 
 * @author Aurélien Baudet
 *
 */
public class FirstSupportingDataCodingProvider implements DataCodingProvider {
	private final List<DataCodingProvider> delegates;

	/**
	 * Initializes with none, one or several {@link DataCodingProvider}s to
	 * execute.
	 * 
	 * @param delegates
	 *            the {@link DataCodingProvider}s to execute
	 */
	public FirstSupportingDataCodingProvider(DataCodingProvider... delegates) {
		this(new ArrayList<>(Arrays.asList(delegates)));
	}

	/**
	 * Initializes with a list of {@link DataCodingProvider}s to execute.
	 * 
	 * @param delegates
	 *            the list of {@link DataCodingProvider}s to execute
	 */
	public FirstSupportingDataCodingProvider(List<DataCodingProvider> delegates) {
		super();
		this.delegates = delegates;
	}

	@Override
	public DataCoding provide(Encoded encoded) throws DataCodingException {
		for (DataCodingProvider delegate : delegates) {
			DataCoding dcs = delegate.provide(encoded);
			if (dcs != null) {
				return dcs;
			}
		}
		throw new DataCodingException("No DataCodingProvider could determine a valid Data Coding Scheme", encoded);
	}

	/**
	 * Register a {@link DataCodingProvider} into the list of possible
	 * providers.
	 * 
	 * <p>
	 * The provider is appended to the list.
	 * </p>
	 * 
	 * @param provider
	 *            the provider to register
	 */
	public void register(DataCodingProvider provider) {
		this.delegates.add(provider);
	}

}
