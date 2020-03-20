package testutils;

import static testutils.SessionStrategyTestHelper.track;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.cloudhopper.smpp.SmppClient;
import com.cloudhopper.smpp.SmppSession;

import fr.sii.ogham.sms.builder.cloudhopper.SmppClientSupplier;

public class TrackClientAndSessionsDecorator implements SmppClientSupplier {
	private final Supplier<List<SmppClient>> clientsSupplier;
	private final Supplier<List<SmppSession>> allSessionsSupplier;
	private int idx = 0;
	private List<SmppClient> decorated;

	public TrackClientAndSessionsDecorator(Supplier<List<SmppClient>> clientsSupplier, Supplier<List<SmppSession>> allSessionsSupplier) {
		super();
		this.clientsSupplier = clientsSupplier;
		this.allSessionsSupplier = allSessionsSupplier;
	}

	@Override
	public SmppClient get() {
		decorate();
		return decorated.get(idx++);
	}

	private void decorate() {
		if (decorated == null) {
			decorated = new ArrayList<>();
			List<SmppClient> clients = clientsSupplier.get();
			List<SmppSession> allSessions = allSessionsSupplier.get();
			for (int i=0 ; i<clients.size() ; i++) {
				decorated.add(track(clients.get(i), allSessions, i));
			}
		}
	}
}
