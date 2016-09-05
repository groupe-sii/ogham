package fr.sii.ogham.snmp.it;

import org.junit.Test;
import org.snmp4j.PDUv1;

import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.email.message.Email;

public class TrapMessageTest {
	
/*	@Test
	public void sendTrap() {
		new SnmpTrap()
			.target(new CommunityTarget())	// réellement nécessaire ? configuration pour établissement connexion ?
			.content(new TemplateContent("", new Toto(new Date(), "127.0.0.1")));
		new SnmpTrap()
			.target(new CommunityTarget())	// réellement nécessaire ? configuration pour établissement connexion ?
			.content(new TemplateContent("", new SnmpBindings().add());
		new SnmpTrap()
			.target(new CommunityTarget())	// réellement nécessaire ? configuration pour établissement connexion ?
			.content(new SnmpContent().add(SnmpConstants.sysUpTime, new OctetString(new Date().toString()))));	// liste de variablebinding ou map
	}
	
	
	Intégrer freemarker ou velocity
	
	template :
		
		${SnmpConstants.sysUpTime}=${date}
		${SnmpConstants.snmpTrapAddress}=${ip}
		${enterprise-specific-oid}.${oid}=${toto}					-> avec enterprise-specific-oid = ogham.snmp.enterprise.oid + ogham.snmp.enterprise.trap-oid 


	properties (conf globale surchargeable par chaque message) :
		ogham.snmp.transport										-> paramètres supplémentaires dépendant du transport
		ogham.snmp.target.community=public							(optionnel)
		ogham.snmp.target.security.name=toto						(optionnel)
		ogham.snmp.target.security.model=SECURITY_MODEL_ANY			(optionnel, voir org.snmp4j.security.SecurityModel)
		ogham.snmp.target.security.level=NOAUTH_NOPRIV				(optionnel, voir org.snmp4j.security.SecurityLevel)
		ogham.snmp.target.user.authoritative-engine-id=fgdgfd		(optionnel)
		ogham.snmp.target.version=v1								(optionnel)
		ogham.snmp.target.address=127.0.0.1/2001					(requis)
		ogham.snmp.target.timeout=5000								(optionnel)
		ogham.snmp.target.retries=2									(optionnel)
		
		ogham.snmp.agent.address=127.0.0.1							(optionnel)
		ogham.snmp.enterprise.oid=1368								(optionnel, seulement si données spécifiques à l'entreprise ce qui sera souvent le cas. Si commence par . => oid complet, sinon ajoute au code normalisé .1.3.6.1.4.1.). Si fourni => appeler setGenericTrap(PDUv1.ENTERPRISE_SPECIFIC) 
		ogham.snmp.enterprise.trap-oid=29							(optionnel, seulement si trap spécifique à l'entreprise. Correspond au préfixe pour les OIDs suivants)
		
*/
}
