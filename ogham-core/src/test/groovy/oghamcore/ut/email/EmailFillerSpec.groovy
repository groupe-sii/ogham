package oghamcore.ut.email

import static fr.sii.ogham.email.message.RecipientType.BCC
import static fr.sii.ogham.email.message.RecipientType.CC
import static fr.sii.ogham.email.message.RecipientType.TO

import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper
import fr.sii.ogham.core.env.PropertyResolver
import fr.sii.ogham.email.filler.EmailFiller
import fr.sii.ogham.email.message.Email
import fr.sii.ogham.email.message.EmailAddress
import fr.sii.ogham.email.message.Recipient
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@LogTestInformation
class EmailFillerSpec extends Specification {
	def "Email subject=#original | default subject=#defaultValue => #desc subject"() {
		given:
			Email email = Mock()
			PropertyResolver resolver = Mock()
			Map defaultValues = Mock()
			ConfigurationValueBuilderHelper valueBuilder = Mock()
			email.getSubject() >> original
			email.getRecipients() >> []
			defaultValues.get("subject") >> valueBuilder
			valueBuilder.getValue(_) >> defaultValue
			def filler = new EmailFiller(resolver, defaultValues)
		
		when:
			filler.fill(email)
			
		then:
			expected * email.subject(defaultValue)
			
		where:
			desc						| original			| defaultValue			|| expected
			"filler should not fill"	| null				| null					|| 0
			"filler should not fill"	| "original"		| null					|| 0
			"filler should fill"		| null				| "default"				|| 1
			"filler should not fill"	| "original"		| "default"				|| 0
	}

	def "Email from=#original | default from=#defaultValue => #desc from"() {
		given:
			Email email = Mock()
			PropertyResolver resolver = Mock()
			Map defaultValues = Mock()
			ConfigurationValueBuilderHelper valueBuilder = Mock()
			email.getFrom() >> original
			email.getRecipients() >> []
			defaultValues.get("from") >> valueBuilder
			valueBuilder.getValue(_) >> defaultValue
			def filler = new EmailFiller(resolver, defaultValues)
		
		when:
			filler.fill(email)
			
		then:
			expected * email.from(defaultValue)
			
		where:
			desc						| original						| defaultValue			|| expected
			"filler should not fill"	| null							| null					|| 0
			"filler should not fill"	| new EmailAddress("original")	| null					|| 0
			"filler should fill"		| null							| "default"				|| 1
			"filler should not fill"	| new EmailAddress("original")	| "default"				|| 0
	}

	def "Email to=#original | default to=#defaultValue => #desc to"() {
		given:
			Email email = Mock()
			PropertyResolver resolver = Mock()
			Map defaultValues = Mock()
			ConfigurationValueBuilderHelper valueBuilder = Mock()
			email.getRecipients() >> original
			defaultValues.get("to") >> valueBuilder
			defaultValues.get(_) >> null
			valueBuilder.getValue(_) >> (defaultValue as String[])
			def filler = new EmailFiller(resolver, defaultValues)
		
		when:
			filler.fill(email)
			
		then:
			expected * email.to(expectedValues)
			
		where:
			desc						| original											| defaultValue				|| expected | expectedValues
			"filler should not fill"	| null												| null						|| 0		| _
			"filler should not fill"	| []												| null						|| 0		| _
			"filler should not fill"	| [new Recipient(new EmailAddress("original"), TO)]	| null						|| 0		| _
			"filler should not fill"	| null												| null						|| 0		| _
			"filler should not fill"	| []												| null						|| 0		| _
			"filler should not fill"	| [new Recipient(new EmailAddress("original"), TO)]	| ["default"]				|| 0		| _
			"filler should fill"		| null												| ["default1", "default2"]	|| 1		| ["default1", "default2"]
			"filler should fill"		| []												| ["default1", "default2"]	|| 1		| ["default1", "default2"]
			"filler should not fill"	| [new Recipient(new EmailAddress("original"), TO)]	| ["default1", "default2"]	|| 0		| _
	}

	
	def "Email cc=#original | default cc=#defaultValue => #desc cc"() {
		given:
			Email email = Mock()
			PropertyResolver resolver = Mock()
			Map defaultValues = Mock()
			ConfigurationValueBuilderHelper valueBuilder = Mock()
			email.getRecipients() >> original
			defaultValues.get("cc") >> valueBuilder
			valueBuilder.getValue(_) >> (defaultValue as String[])
			def filler = new EmailFiller(resolver, defaultValues)
		
		when:
			filler.fill(email)
			
		then:
			expected * email.cc(expectedValues)
			
		where:
			desc						| original											| defaultValue				|| expected | expectedValues
			"filler should not fill"	| null												| null						|| 0		| _
			"filler should not fill"	| []												| null						|| 0		| _
			"filler should not fill"	| [new Recipient(new EmailAddress("original"), CC)]	| null						|| 0		| _
			"filler should not fill"	| null												| null						|| 0		| _
			"filler should not fill"	| []												| null						|| 0		| _
			"filler should not fill"	| [new Recipient(new EmailAddress("original"), CC)]	| ["default"]				|| 0		| _
			"filler should not fill"	| null												| null						|| 0		| _
			"filler should not fill"	| []												| null						|| 0		| _
			"filler should not fill"	| [new Recipient(new EmailAddress("original"), CC)]	| ["default1", "default2"]	|| 0		| _
	}

	def "Email bcc=#original | default bcc=#defaultValue => #desc bcc"() {
		given:
			Email email = Mock()
			PropertyResolver resolver = Mock()
			Map defaultValues = Mock()
			ConfigurationValueBuilderHelper valueBuilder = Mock()
			email.getRecipients() >> original
			defaultValues.get("bcc") >> valueBuilder
			valueBuilder.getValue(_) >> (defaultValue as String[])
			def filler = new EmailFiller(resolver, defaultValues)
		
		when:
			filler.fill(email)
			
		then:
			expected * email.bcc(expectedValues)
			
		where:
			desc						| original												| defaultValue				|| expected | expectedValues
			"filler should not fill"	| null													| null						|| 0		| _
			"filler should not fill"	| []													| null						|| 0		| _
			"filler should not fill"	| [new Recipient(new EmailAddress("original"), BCC)]	| null						|| 0		| _
			"filler should fill"		| null													| ["default"]				|| 1		| ["default"]
			"filler should fill"		| []													| ["default"]				|| 1		| ["default"]
			"filler should not fill"	| [new Recipient(new EmailAddress("original"), BCC)]	| ["default"]				|| 0		| _
			"filler should fill"		| null													| ["default1", "default2"]	|| 1		| ["default1", "default2"]
			"filler should fill"		| []													| ["default1", "default2"]	|| 1		| ["default1", "default2"]
			"filler should not fill"	| [new Recipient(new EmailAddress("original"), BCC)]	| ["default1", "default2"]	|| 0		| _
	}
	
	def "no value builder set should not fill"() {
		given:
			Email email = Mock()
			PropertyResolver resolver = Mock()
			Map defaultValues = Mock()
			defaultValues.get(_) >> null
			email.getFrom() >> from
			email.getRecipients() >> to
			def filler = new EmailFiller(resolver, defaultValues)
			
		when:
			filler.fill(email)
			
		then:
			0 * email.from(_)
			0 * email.to(_)
			0 * email.cc(_)
			0 * email.bcc(_)
			
		where:
			from			| to	
			null			| null	
			null			| []	
	}
}
