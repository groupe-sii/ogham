package fr.sii.ogham.snmp.message;

import fr.sii.ogham.core.message.Message;

public class Trap implements Message {
	private List<Target> recipients;
	
	private Content content;
}
