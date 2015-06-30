package fr.sii.ogham.sms.sender.impl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.MessageException;
import fr.sii.ogham.core.exception.MessageNotSentException;
import fr.sii.ogham.core.exception.util.HttpException;
import fr.sii.ogham.core.exception.util.PhoneNumberException;
import fr.sii.ogham.core.sender.AbstractSpecializedSender;
import fr.sii.ogham.core.util.HttpUtils;
import fr.sii.ogham.core.util.PhoneNumberUtils;
import fr.sii.ogham.core.util.HttpUtils.Parameter;
import fr.sii.ogham.core.util.HttpUtils.Response;
import fr.sii.ogham.core.util.StringUtils;
import fr.sii.ogham.sms.message.PhoneNumber;
import fr.sii.ogham.sms.message.Recipient;
import fr.sii.ogham.sms.message.Sms;
import fr.sii.ogham.sms.sender.impl.ovh.OvhAuthParams;
import fr.sii.ogham.sms.sender.impl.ovh.OvhOptions;

public class OvhSmsSender extends AbstractSpecializedSender<Sms> {
	private static final Logger LOG = LoggerFactory.getLogger(OvhSmsSender.class);
	private static final String JSON_CONTENT_TYPE = "application/json";
	private static final String RESPONSE_TYPE = "contentType";
	private static final String MESSAGE = "message";
	private static final String TO = "to";
	private static final String FROM = "from";
	private static final String RECIPIENTS_SEPARATOR = ",";

	private final OvhAuthParams authParams;
	
	private final OvhOptions options;
	
	private final ObjectMapper mapper;
	
	private final URL url;
	
	public OvhSmsSender(URL url, OvhAuthParams authParams, OvhOptions options) {
		super();
		this.url = url;
		this.authParams = authParams;
		this.options = options;
		this.mapper = new ObjectMapper();
	}

	@Override
	public void send(Sms message) throws MessageException {
		try {
			Response response = HttpUtils.get(url.toString(), authParams, options,
									new Parameter(RESPONSE_TYPE, JSON_CONTENT_TYPE),
									// convert phone number to international format
									new Parameter(FROM, toInternational(message.getFrom().getPhoneNumber())),
									new Parameter(TO, StringUtils.join(convert(message.getRecipients()), RECIPIENTS_SEPARATOR)),
									// TODO: manage long messages: how to do ??
									new Parameter(MESSAGE, getContent(message)));
			if(response.getStatus()>=200 && response.getStatus()<300) {
				JsonNode json = mapper.readTree(response.getBody());
				int ovhStatus = json.get("status").getIntValue();
				// 100 <= ovh status < 200     ====> OK -> just log response
				// 200 <= ovh status           ====> KO -> throw an exception
				if(ovhStatus>=200) {
					LOG.error("SMS failed to be sent through OVH");
					LOG.debug("Sent SMS: {}", message);
					LOG.debug("Response status {}", response.getStatus());
					LOG.debug("Response body {}", response.getBody());
					throw new MessageNotSentException("SMS couldn't be sent through OVH: "+json.get("message").getTextValue(), message);
				} else {
					LOG.info("SMS successfully sent through OVH");
					LOG.debug("Sent SMS: {}", message);
					LOG.debug("Response: {}", response.getBody());
				}
			} else {
				LOG.error("Response status {}", response.getStatus());
				LOG.error("Response body {}", response.getBody());
				throw new MessageNotSentException("SMS couldn't be sent. Response status is "+response.getStatus(), message);
			}
		} catch(IOException e) {
			throw new MessageException("Failed to read response when sending SMS through OVH", message, e);
		} catch (HttpException e) {
			throw new MessageException("Failed to send SMS through OVH", message, e);
		} catch (PhoneNumberException e) {
			throw new MessageException("Failed to send SMS through OVH (invalid phone number)", message, e);
		}
	}

	private String getContent(Sms message) {
		// if a string contains \r\n, only \r is kept
		// if there are \n without \r, those \n are converted to \r
		return message.getContent().toString().replaceAll("(\r)?\n", "\r");
	}
	
	private static List<String> convert(List<Recipient> recipients) throws PhoneNumberException {
		List<String> tos = new ArrayList<>(recipients.size());
		// convert phone numbers to international format
		for(Recipient recipient : recipients) {
			tos.add(toInternational(recipient.getPhoneNumber()));
		}
		return tos;
	}
	
	private static String toInternational(PhoneNumber phoneNumber) throws PhoneNumberException {
		// TODO: pad with 0 instead of 2 digits in order to generate a number with 13? digits
		return PhoneNumberUtils.toInternational(phoneNumber.getNumber()).replace("+", "00");
	}

	/**
	 * Les paramètres doivent être séparé par des '&' et sont les suivants :
account = Compte SMS à utiliser (format sms-nic-X)
login = Utilisateur SMS à utiliser sur le compte associé
password = Mot de passe de l'utilisateur
from = Votre numéro d'expéditeur à utiliser, parmi les numéros déclarés sur votre compte SMS (format international 00336X...)
to = Numéro de téléphone du destinataire du message, en cas de destinataires multiples il est possible de rajouter les numéros supplémentaires, séparés par une virgule "," (les numéros étant toujours au format international)
message = Votre message

Viennent ensuite des champs facultatifs, tels que :

noStop = 1 pour ne pas afficher 'STOP au XXXXX' dans le message dans le cas d'un SMS non commercial
deferred = Pour définir la date d'envoi différée (par défaut les messages sont envoyés tout de suite sinon), au format hhmmjjMMAAAA (pour un envoi le 25/08/2011 à 12h30 : 123025082011)
class = Type de classe du SMS, au format N (1 chiffre) parmi les 4 disponibles (classe 1 par défaut) :

- classe 0 : Le message est directement affiché à l’utilisateur sur l’écran du mobile à la réception. Le message n’est enregistré ni dans la mémoire du téléphone ni dans la carte SIM. Il est effacé dès que l’utilisateur a validé la visualisation.
- classe 1 : Le message est enregistré dans la mémoire du téléphone et si cette mémoire est pleine, dans la carte SIM par défaut.
- classe 2 : Le message est enregistré sur la carte SIM.
- classe 3 : Le message est transféré sur un équipement externe connecté au mobile (PDA, PC portable…)

tag = Une chaîne de maximum 20 caractères vous permettant de marquer les messages envoyés.
contentType = Vous pouvez choisir le type de la réponse. Elle peut être de type : text/xml, application/xml, text/json, application/json, text/plain,text/html (par défaut en text/plain).
Faire un saut de ligne le message envoyé = Ajouter %0d pour effectuer un saut de ligne dans le SMS envoyé
Définir l'encodage d'un SMS = Par défaut l'encodage se fait sur 7bit. Si vous souhaitez modifier l'encodage il faut suffit d'ajouter le paramètre smscoding ou smsCoding. 1 pour l'encodage sur 7bit ou 2 pour l'encodage sur 8bit (UTF8). Si vous modifiez l’encodage pour l'UTF8, votre SMS fera 70 caractères maximum contre 160 sur l'encodage 7bit.
	 */
}
