package fr.sii.ogham.testing.sms.simulator.jsmpp;

import java.security.SecureRandom;
import java.util.Random;

import org.jsmpp.PDUStringException;
import org.jsmpp.util.MessageIDGenerator;
import org.jsmpp.util.MessageId;
import org.jsmpp.util.RandomMessageIDGenerator;

/**
 * This implementation doesn't use {@link SecureRandom} (unlike
 * {@link RandomMessageIDGenerator}). {@link SecureRandom} can potentially block
 * the simulator process because on some operating systems /dev/random waits for
 * a certain amount of "noise" to be generated on the host machine before
 * returning a result.
 * 
 * <p>
 * jSMPP is used in tests so timeouts have small values that's why we can't wait
 * until enough "noise" is generated.
 * 
 * @author Aurélien Baudet
 * @see RandomMessageIDGenerator
 * @see SecureRandom
 * @see "https://docs.oracle.com/cd/E13209_01/wlcp/wlss30/configwlss/jvmrand.html"
 */
public class UnsecureRandomMessageIDGenerator implements MessageIDGenerator {
	private final Random random;

	public UnsecureRandomMessageIDGenerator() {
		random = new Random();
	}

	@Override
	public MessageId newMessageId() {
		/*
		 * use database sequence convert into hex representation or if not using
		 * database using random
		 */
		try {
			synchronized (random) {
				return new MessageId(Integer.toString(random.nextInt(Integer.MAX_VALUE), 16));
			}
		} catch (PDUStringException e) {
			throw new RuntimeException("Failed creating message id", e);
		}
	}
}