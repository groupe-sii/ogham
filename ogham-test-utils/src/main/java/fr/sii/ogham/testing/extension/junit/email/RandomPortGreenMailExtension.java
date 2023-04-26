package fr.sii.ogham.testing.extension.junit.email;

import fr.sii.ogham.testing.util.RandomPortUtils;
import ogham.testing.com.icegreen.greenmail.junit5.GreenMailExtension;
import ogham.testing.com.icegreen.greenmail.server.AbstractServer;
import ogham.testing.com.icegreen.greenmail.util.GreenMail;
import ogham.testing.com.icegreen.greenmail.util.ServerSetup;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.List;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

/**
 * Just an extension for {@link GreenMailExtension} to handle random port instead of
 * fixed port.
 * 
 * @author Aurélien Baudet
 *
 */
public class RandomPortGreenMailExtension extends GreenMailExtension implements ParameterResolver {
	private boolean perMethod = true;

	/**
	 * Initialize the rule for the SMTP protocol only (random port).
	 */
	public RandomPortGreenMailExtension() {
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
	public RandomPortGreenMailExtension(String... protocols) {
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
	public RandomPortGreenMailExtension(int maxPort, String... protocols) {
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
	public RandomPortGreenMailExtension(int minPort, int maxPort, String... protocols) {
		super(toServerSetup(minPort, maxPort, protocols));
	}

	@Override
	public void afterEach(ExtensionContext context) {
		super.afterEach(context);
		if (perMethod) {
			resetPorts();
		}
	}

	@Override
	public void afterAll(ExtensionContext context) {
		super.afterAll(context);
		resetPorts();
	}

	@Override
	public GreenMailExtension withPerMethodLifecycle(boolean perMethod) {
		this.perMethod = perMethod;
		return super.withPerMethodLifecycle(perMethod);
	}

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		return isGreenMailParam(parameterContext) || isGreenMailExtensionParam(parameterContext);
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		if (isGreenMailParam(parameterContext)) {
			return getGreenMail();
		}
		if (isGreenMailExtensionParam(parameterContext)) {
			return this;
		}
		return null;
	}

	private boolean isGreenMailParam(ParameterContext parameterContext) {
		return GreenMail.class.isAssignableFrom(parameterContext.getParameter().getType());
	}

	private boolean isGreenMailExtensionParam(ParameterContext parameterContext) {
		return GreenMailExtension.class.isAssignableFrom(parameterContext.getParameter().getType());
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
		List<ServerSetup> list = stream(protocols)
				.map(p -> new RandomPortServerSetup(minPort, maxPort, p))
				.collect(toList());
		return list.toArray(new ServerSetup[list.size()]);
	}
}
