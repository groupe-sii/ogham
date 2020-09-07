package fr.sii.ogham.testing.extension.junit.email;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

import java.util.List;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import com.icegreen.greenmail.junit4.GreenMailRule;
import com.icegreen.greenmail.server.AbstractServer;
import com.icegreen.greenmail.util.ServerSetup;

import fr.sii.ogham.testing.util.RandomPortUtils;

/**
 * Just an extension for {@link GreenMailRule} to handle random port instead of
 * fixed port.
 * 
 * @author Aurélien Baudet
 *
 */
public class RandomPortGreenMailRule extends GreenMailRule {

	/**
	 * Initialize the rule for the SMTP protocol only (random port).
	 */
	public RandomPortGreenMailRule() {
		this(ServerSetup.PROTOCOL_SMTP);
	}

	/**
	 * Initialize the rule for the provided protocols (random port for each
	 * protocol).
	 * 
	 * The random port range is [{@link RandomPortUtils#PORT_RANGE_MIN},
	 * {@link RandomPortUtils#PORT_RANGE_MAX}].
	 * 
	 * @param protocols
	 *            the list of protocols to start
	 */
	public RandomPortGreenMailRule(String... protocols) {
		this(RandomPortUtils.PORT_RANGE_MIN, RandomPortUtils.PORT_RANGE_MAX, protocols);
	}

	/**
	 * Initialize the rule for the provided protocols (random port for each
	 * protocol).
	 * 
	 * The random port range is [{@link RandomPortUtils#PORT_RANGE_MIN},
	 * maxPort].
	 * 
	 * @param maxPort
	 *            the maximum port
	 * @param protocols
	 *            the list of protocols to start
	 */
	public RandomPortGreenMailRule(int maxPort, String... protocols) {
		this(RandomPortUtils.PORT_RANGE_MIN, maxPort, protocols);
	}

	/**
	 * Initialize the rule for the provided protocols (random port for each
	 * protocol).
	 * 
	 * The random port range is [minPort, maxPort].
	 * 
	 * @param minPort
	 *            the minimum port
	 * @param maxPort
	 *            the maximum port
	 * @param protocols
	 *            the list of protocols to start
	 */
	public RandomPortGreenMailRule(int minPort, int maxPort, String... protocols) {
		super(toServerSetup(minPort, maxPort, protocols));
	}

	@Override
	public Statement apply(final Statement base, FrameworkMethod method, Object target) {
		return super.apply(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				try {
					base.evaluate();
				} finally {
					resetPorts();
				}
			}
		}, method, target);
	}

	private void resetPorts() {
		if (getGreenMail() != null) {
			resetPort(getGreenMail().getImap());
			resetPort(getGreenMail().getImaps());
			resetPort(getGreenMail().getPop3());
			resetPort(getGreenMail().getPop3s());
			resetPort(getGreenMail().getSmtp());
			resetPort(getGreenMail().getSmtps());
		}
	}

	private static void resetPort(AbstractServer server) {
		if (server != null) {
			resetPort(server.getServerSetup());
		}
	}

	private static void resetPort(ServerSetup setup) {
		if (setup instanceof RandomPortServerSetup) {
			((RandomPortServerSetup) setup).resetPort();
		}
	}

	private static ServerSetup[] toServerSetup(int minPort, int maxPort, String... protocols) {
		List<ServerSetup> list = stream(protocols).map(p -> new RandomPortServerSetup(minPort, maxPort, p)).collect(toList());
		return list.toArray(new ServerSetup[list.size()]);
	}
}
