package fr.sii.ogham.testing.assertion.internal;

import static org.junit.Assert.assertThat;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.hamcrest.Matcher;

import fr.sii.ogham.sms.sender.impl.CloudhopperSMPPSender;
import fr.sii.ogham.sms.sender.impl.cloudhopper.preparator.MessagePreparator;
import fr.sii.ogham.sms.splitter.MessageSplitter;
import fr.sii.ogham.sms.splitter.NoSplitMessageSplitter;
import fr.sii.ogham.testing.assertion.HasParent;

/**
 * Make assertions on splitter instance to ensure that it is correctly
 * configured.
 * 
 * For example:
 * 
 * <pre>
 * {@code
 *   enabled(is(true))
 * }
 * </pre>
 * 
 * @author Aurélien Baudet
 *
 */
public class CloudhopperSplitterAssertions extends HasParent<CloudhopperAssertions> {

	private final CloudhopperSMPPSender cloudhopperSender;

	public CloudhopperSplitterAssertions(CloudhopperAssertions parent, CloudhopperSMPPSender cloudhopperSender) {
		super(parent);
		this.cloudhopperSender = cloudhopperSender;
	}

	/**
	 * Ensures that splitter is correctly enabled or not. When Splitter is
	 * disabled, the splitter instance is {@link NoSplitMessageSplitter}.
	 * 
	 * @param matcher
	 *            the matcher to ensure that splitter is enabled or disabled
	 */
	public void enabled(Matcher<Boolean> matcher) {
		try {
			boolean noSplit = getSplitter() instanceof NoSplitMessageSplitter;
			assertThat(!noSplit, matcher);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Failed to get 'messagePreparator.messageSplitter' of CloudhopperSMPPSender", e);
		}
	}

	private MessageSplitter getSplitter() throws IllegalAccessException {
		MessagePreparator preparator = (MessagePreparator) FieldUtils.readField(cloudhopperSender, "messagePreparator", true);
		return (MessageSplitter) FieldUtils.readField(preparator, "messageSplitter", true);
	}
}
