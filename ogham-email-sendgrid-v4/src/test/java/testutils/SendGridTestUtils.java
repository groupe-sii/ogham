package testutils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sendgrid.helpers.mail.Mail;

public class SendGridTestUtils {

	public static String getFromName(Mail val) {
		if(val == null) {
			return null;
		}
		if(val.getFrom() == null) {
			return null;
		}
		return val.getFrom().getName();
	}

	public static String getFromAddress(Mail val) {
		if(val == null) {
			return null;
		}
		if(val.getFrom() == null) {
			return null;
		}
		return val.getFrom().getEmail();
	}

	public static String[] getToNames(Mail val) {
		if(val == null) {
			return null;
		}
		if(val.getPersonalization()==null) {
			return null;
		}
		List<String> tos = val.getPersonalization()
				.stream()
				.flatMap(p -> p.getTos()==null ? Stream.empty() : p.getTos().stream())
				.map(e -> e.getName())
				.collect(Collectors.toList());
		return tos.toArray(new String[tos.size()]);
	}

	public static String[] getTos(Mail val) {
		if(val == null) {
			return null;
		}
		if(val.getPersonalization()==null) {
			return null;
		}
		List<String> tos = val.getPersonalization()
				.stream()
				.flatMap(p -> p.getTos()==null ? Stream.empty() : p.getTos().stream())
				.map(e -> e.getEmail())
				.collect(Collectors.toList());
		return tos.toArray(new String[tos.size()]);
	}

	public static String getHtml(Mail val) {
		if(val == null) {
			return null;
		}
		return val.getContent()
				.stream()
				.filter(c -> c.getType().equals("text/html"))
				.map(c -> c.getValue())
				.findFirst()
				.orElse(null);
	}

	public static String getText(Mail val) {
		if(val == null) {
			return null;
		}
		return val.getContent()
				.stream()
				.filter(c -> c.getType().equals("text/plain"))
				.map(c -> c.getValue())
				.findFirst()
				.orElse(null);
	}
}
