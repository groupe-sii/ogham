package fr.sii.ogham.sms.builder.ovh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.util.BuilderUtils;
import fr.sii.ogham.sms.sender.impl.ovh.OvhOptions;
import fr.sii.ogham.sms.sender.impl.ovh.SmsCoding;

public class OvhOptionsBuilder extends AbstractParent<OvhSmsBuilder> implements Builder<OvhOptions> {
	private EnvironmentBuilder<?> environmentBuilder;
	private List<String> noStops;
	private Boolean noStop;
	private List<String> tags;
	private List<String> smsCodings;
	private SmsCoding smsCoding;

	public OvhOptionsBuilder(OvhSmsBuilder parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		this.environmentBuilder = environmentBuilder;
		noStops = new ArrayList<>();
		tags = new ArrayList<>();
		smsCodings = new ArrayList<>();
	}

	public OvhOptionsBuilder noStop(String... noStop) {
		noStops.addAll(Arrays.asList(noStop));
		return this;
	}

	public OvhOptionsBuilder noStop(boolean noStop) {
		this.noStop = noStop;
		return this;
	}

	public OvhOptionsBuilder tag(String... tag) {
		tags.addAll(Arrays.asList(tag));
		return this;
	}

	public OvhOptionsBuilder smsCoding(String... smsCoding) {
		smsCodings.addAll(Arrays.asList(smsCoding));
		return this;
	}

	public OvhOptionsBuilder smsCoding(SmsCoding smsCoding) {
		this.smsCoding = smsCoding;
		return this;
	}

	@Override
	public OvhOptions build() throws BuildException {
		PropertyResolver propertyResolver = environmentBuilder.build();
		boolean noStop = buildNoStop(propertyResolver);
		String tag = buildTag(propertyResolver);
		SmsCoding smsCoding = buildSmsCoding(propertyResolver);
		return new OvhOptions(noStop, tag, smsCoding);
	}

	private boolean buildNoStop(PropertyResolver propertyResolver) {
		if(noStop!=null) {
			return noStop;
		}
		return BuilderUtils.evaluate(noStops, propertyResolver, Boolean.class);
	}

	private String buildTag(PropertyResolver propertyResolver) {
		return BuilderUtils.evaluate(tags, propertyResolver, String.class);
	}

	private SmsCoding buildSmsCoding(PropertyResolver propertyResolver) {
		if(smsCoding!=null) {
			return smsCoding;
		}
		String name = BuilderUtils.evaluate(smsCodings, propertyResolver, String.class);
		return name==null ? null : SmsCoding.valueOf(name);
	}
}
