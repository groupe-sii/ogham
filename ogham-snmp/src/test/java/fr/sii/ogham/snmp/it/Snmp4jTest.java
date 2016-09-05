package fr.sii.ogham.snmp.it;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import org.junit.Test;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.PDUv1;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.agent.BaseAgent;
import org.snmp4j.agent.CommandProcessor;
import org.snmp4j.agent.mo.snmp.SnmpCommunityMIB;
import org.snmp4j.agent.mo.snmp.SnmpNotificationMIB;
import org.snmp4j.agent.mo.snmp.SnmpTargetMIB;
import org.snmp4j.agent.mo.snmp.VacmMIB;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.USM;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultSshTransportMapping;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class Snmp4jTest {
	@Test
	public void trap() throws IOException, InterruptedException, ParseException {
//		SnmpAgent agent = new SnmpAgent(new File("/tmp/snmp/boot.agent"), new File("/tmp/snmp/conf.agent"), new CommandProcessor(new OctetString(MPv3.createLocalEngineID())));
//		agent.init();
//		agent.run();
//		agent.
//		SNMPAgent agent = new SNMPAgent("0.0.0.0/2001");
//		agent.start();
		
		SnmpManager manager = new SnmpManager();
		manager.start();
		
		TransportMapping transport = new DefaultUdpTransportMapping();
		transport.listen();
		Snmp snmp = new Snmp(transport);
		
		
		 CommunityTarget cTarget = new CommunityTarget();
		 cTarget.setCommunity(new OctetString("public"));
//		 cTarget.setVersion(SnmpConstants.version1);
		 cTarget.setAddress(new UdpAddress("127.0.0.1/2001"));
//		 cTarget.setTimeout(5000);
//		 cTarget.setRetries(2);
		 PDUv1 pdu = new PDUv1();
//		 pdu.setType(PDU.V1TRAP);
		 pdu.setEnterprise(new OID(".1.3.6.1.4.1.1368"));
		 pdu.setGenericTrap(PDUv1.ENTERPRISE_SPECIFIC);				// devrait être fait automatiquement si on utilise setEntreprise ?
		 pdu.setSpecificTrap(29);
//		 pdu.setTimestamp(System.currentTimeMillis());
//		 pdu.setAgentAddress(new IpAddress("127.0.0.1"));
//		 System.out.println("Sending V1 Trap... Check Wheather NMS is Listening or not? ");
		
		snmp.trap(pdu, cTarget);
		
		 pdu = new PDUv1();
//		 pdu.setType(PDU.V1TRAP);
		 pdu.setEnterprise(new OID(".1.3.6.1.4.1.1368"));
		 pdu.setGenericTrap(PDUv1.ENTERPRISE_SPECIFIC);
		 pdu.setSpecificTrap(29);
//		 pdu.setTimestamp(System.currentTimeMillis());
		 pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.1368.29.0.56"), new OctetString("DNS IP KO")));
		 pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.1368.29.0.2"), new OctetString("Internet")));
		 pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.1368.29.0.21"), new OctetString("foo")));
//		 pdu.setAgentAddress(new IpAddress("127.0.0.1"));
//		 System.out.println("Sending V1 Trap... Check Wheather NMS is Listening or not? ");
		
		snmp.trap(pdu, cTarget);
//		 snmp.send(pdu, cTarget);
		 snmp.close();

		 Thread.sleep(1000L);
		 
		 manager.stop();
	}
	
	public static class SnmpManager {
		private Snmp snmp;

		public void start() throws IOException, InterruptedException {
			TransportMapping transport = new DefaultUdpTransportMapping(new UdpAddress("127.0.0.1/2001"));
			snmp = new Snmp(transport);
			snmp.addCommandResponder(new CommandResponder() {
				
				@Override
				public void processPdu(CommandResponderEvent event) {
					// TODO Auto-generated method stub
					System.out.println(event.getPDU());
				}
			});
			transport.listen();
		}
		
		public void stop() throws IOException {
			snmp.close();
		}
	}
	
	public static class SnmpAgent extends BaseAgent {

		protected SnmpAgent(File bootCounterFile, File configFile, CommandProcessor commandProcessor) {
			super(bootCounterFile, configFile, commandProcessor);
		}

		@Override
		protected void registerManagedObjects() {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void unregisterManagedObjects() {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void addUsmUser(USM usm) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void addNotificationTargets(SnmpTargetMIB targetMIB, SnmpNotificationMIB notificationMIB) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void addViews(VacmMIB vacmMIB) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void addCommunities(SnmpCommunityMIB communityMIB) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
