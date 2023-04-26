package fr.sii.ogham.core.mimetype.validation;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import fr.sii.ogham.core.mimetype.MimeType;

/**
 * Default predicate that checks if the mimetype matches the provided string or
 * not.
 * 
 * <p>
 * The match string may contain {@code "*"}. The match string is converted to a
 * regular expression where {@code "*"} is used to match anything.
 * 
 * <p>
 * MimeType parameters (such as {@code ";charset="}) are ignored.
 * 
 * @author Aurélien Baudet
 *
 */
public class MatchMimetypePredicate implements Predicate<MimeType> {
	private static final Pattern PARAMETERS = Pattern.compile(";.*");
	private final Pattern matches;

	public MatchMimetypePredicate(String matches) {
		this(Pattern.compile(toPattern(matches)));
	}

	public MatchMimetypePredicate(Pattern matches) {
		super();
		this.matches = matches;
	}

	@Override
	public boolean test(MimeType t) {
		return matches.matcher(sanitize(t.toString())).matches();
	}

	@Override
	public String toString() {
		return "(" + matches + "?)";
	}
	
	private static String sanitize(String mimetype) {
		return PARAMETERS.matcher(mimetype).replaceAll("");
	}

	private static String toPattern(String matches) {
		StringBuilder regex = new StringBuilder();
		int starIdx = 0;
		int previousStarIdx = 0;
		while ((starIdx = matches.indexOf('*', previousStarIdx)) >= 0) {
			regex.append(Pattern.quote(matches.substring(previousStarIdx, starIdx)));
			regex.append(".*");
			previousStarIdx = starIdx + 1;
		}
		regex.append(Pattern.quote(matches.substring(previousStarIdx)));
		return regex.toString();
	}

}