package utils.properties;

import java.util.List;
import java.util.Optional;

import fr.sii.ogham.core.builder.configuration.MayOverride;
import fr.sii.ogham.sms.builder.cloudhopper.EncoderBuilder;
import fr.sii.ogham.sms.builder.cloudhopper.StandardEncodingHelper;
import fr.sii.ogham.sms.sender.impl.cloudhopper.encoder.NamedCharset;

public class TrackEncodingBuilder extends StandardEncodingHelper {
	private final PropertiesAndValue track;
	private final StandardEncodingHelper delegate;
	
	public TrackEncodingBuilder(StandardEncodingHelper delegate, List<PropertiesAndValue> all) {
		super(null, null, null);
		this.delegate = delegate;
		this.track = new PropertiesAndValue(delegate);
		all.add(track);
	}

	@Override
	public StandardEncodingHelper defaultValue(Integer value) {
		delegate.defaultValue(value);
		return this;
	}

	@Override
	public StandardEncodingHelper defaultValue(MayOverride<Integer> possibleNewValue) {
		delegate.defaultValue(possibleNewValue);
		return this;
	}

	@Override
	public void setValue(Integer value) {
		((StandardEncodingHelper) delegate).setValue(value);
	}

	@Override
	public Integer getValue() {
		return ((StandardEncodingHelper) delegate).getValue();
	}

	@Override
	public Integer getValue(Integer defaultValue) {
		return ((StandardEncodingHelper) delegate).getValue(defaultValue);
	}

	@Override
	public boolean hasValueOrProperties() {
		return ((StandardEncodingHelper) delegate).hasValueOrProperties();
	}

	@Override
	public EncoderBuilder and() {
		return delegate.and();
	}

	@Override
	public StandardEncodingHelper value(Optional<Integer> optionalValue) {
		track.addValues(optionalValue);
		super.value(optionalValue);
		return this;
	}

	@Override
	public StandardEncodingHelper properties(String... properties) {
		track.addProperties(properties);
		super.properties(properties);
		return this;
	}

	@Override
	public NamedCharset getCharset() {
		return delegate.getCharset();
	}

}