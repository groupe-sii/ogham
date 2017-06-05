package fr.sii.ogham.core.builder.mimetype;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.mimetype.replace.ContainsMimetypeReplacer;
import fr.sii.ogham.core.mimetype.replace.FirstMatchingMimetypeReplacer;
import fr.sii.ogham.core.mimetype.replace.MimetypeReplacer;
import fr.sii.ogham.core.mimetype.replace.PatternMimetypeReplacer;

/**
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent builder (when calling {@link #and()}
 *            method)
 */
public class SimpleReplaceMimetypeBuilder<P> extends AbstractParent<P> implements ReplaceMimetypeBuilder<P> {
	private final List<Builder<MimetypeReplacer>> delegates;

	/**
	 * Initializes the builder with the parent instance (used by the
	 * {@link #and()} method).
	 * 
	 * @param parent
	 *            the parent instance
	 */
	public SimpleReplaceMimetypeBuilder(P parent) {
		super(parent);
		delegates = new ArrayList<>();
	}

	@Override
	public ReplaceMimetypeBuilder<P> contains(String contains, String replacement) {
		delegates.add(new ContainsReplacement(contains, true, replacement));
		return this;
	}

	@Override
	public ReplaceMimetypeBuilder<P> contains(String contains, boolean ignoreCase, String replacement) {
		delegates.add(new ContainsReplacement(contains, ignoreCase, replacement));
		return this;
	}

	@Override
	public ReplaceMimetypeBuilder<P> pattern(String matchingPattern, String replacement) {
		delegates.add(new PatternReplacement(Pattern.compile(matchingPattern), replacement));
		return this;
	}

	@Override
	public ReplaceMimetypeBuilder<P> pattern(Pattern matchingPattern, String replacement) {
		delegates.add(new PatternReplacement(matchingPattern, replacement));
		return this;
	}

	@Override
	public MimetypeReplacer build() {
		List<MimetypeReplacer> replacers = new ArrayList<>();
		for (Builder<MimetypeReplacer> builder : delegates) {
			replacers.add(builder.build());
		}
		return new FirstMatchingMimetypeReplacer(replacers);
	}

	private static class PatternReplacement implements Builder<MimetypeReplacer> {
		private final Pattern pattern;
		private final String replacement;

		public PatternReplacement(Pattern pattern, String replacement) {
			super();
			this.pattern = pattern;
			this.replacement = replacement;
		}

		@Override
		public MimetypeReplacer build() {
			return new PatternMimetypeReplacer(pattern, replacement);
		}

	}

	private static class ContainsReplacement implements Builder<MimetypeReplacer> {
		private final String contains;
		private final boolean ignoreCase;
		private final String replacement;

		public ContainsReplacement(String contains, boolean ignoreCase, String replacement) {
			super();
			this.contains = contains;
			this.ignoreCase = ignoreCase;
			this.replacement = replacement;
		}

		@Override
		public MimetypeReplacer build() {
			return new ContainsMimetypeReplacer(contains, ignoreCase, replacement);
		}

	}
}
