package oghamcore.ut.sms

import fr.sii.ogham.core.builder.configuration.ConfigurationValueBuilderHelper
import fr.sii.ogham.core.env.PropertyResolver
import fr.sii.ogham.sms.filler.SmsFiller
import fr.sii.ogham.sms.message.Recipient
import fr.sii.ogham.sms.message.Sender
import fr.sii.ogham.sms.message.Sms
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@LogTestInformation
class SmsFillerSpec extends Specification {
	def "Sms from=#original | default from=#defaultValue => #desc from"() {
		given:
			Sms sms = Mock()
			PropertyResolver resolver = Mock()
			Map defaultValues = Mock()
			ConfigurationValueBuilderHelper valueBuilder = Mock()
			sms.getFrom() >> original
			sms.getRecipients() >> []
			defaultValues.get("from") >> valueBuilder
			defaultValues.get(_) >> null
			valueBuilder.getValue(_) >> defaultValue
			def filler = new SmsFiller(resolver, defaultValues)
		
		when:
			filler.fill(sms)
			
		then:
			expected * sms.from(defaultValue)
			
		where:
			desc						| original					| defaultValue			|| expected
			"filler should not fill"	| null						| null					|| 0
			"filler should not fill"	| new Sender("original")	| null					|| 0
			"filler should fill"		| null						| "default"				|| 1
			"filler should not fill"	| new Sender("original")	| "default"				|| 0
	}

	def "Sms to=#original | default to=#defaultValue => #desc to"() {
		given:
			Sms sms = Mock()
			PropertyResolver resolver = Mock()
			Map defaultValues = Mock()
			ConfigurationValueBuilderHelper valueBuilder = Mock()
			sms.getRecipients() >> original
			defaultValues.get("to") >> valueBuilder
			defaultValues.get(_) >> null
			valueBuilder.getValue(_) >> (defaultValue as String[])
			def filler = new SmsFiller(resolver, defaultValues)
		
		when:
			filler.fill(sms)
			
		then:
			expected * sms.to(expectedValues)
			
		where:
			desc						| original						| defaultValue				|| expected | expectedValues
			"filler should not fill"	| null							| null						|| 0		| _
			"filler should not fill"	| []							| null						|| 0		| _
			"filler should not fill"	| [new Recipient("original")]	| null						|| 0		| _
			"filler should fill"		| null							| ["default"]				|| 1		| ["default"]
			"filler should fill"		| []							| ["default"]				|| 1		| ["default"]
			"filler should not fill"	| [new Recipient("original")]	| ["default"]				|| 0		| _
			"filler should fill"		| null							| ["default1", "default2"]	|| 1		| ["default1", "default2"]
			"filler should fill"		| []							| ["default1", "default2"]	|| 1		| ["default1", "default2"]
			"filler should not fill"	| [new Recipient("original")]	| ["default1", "default2"]	|| 0		| _
	}
	
	def "no value builder set should not fill"() {
		given:
			Sms sms = Mock()
			PropertyResolver resolver = Mock()
			Map defaultValues = Mock()
			defaultValues.get(_) >> null
			sms.getFrom() >> from
			sms.getRecipients() >> to
			def filler = new SmsFiller(resolver, defaultValues)
			
		when:
			filler.fill(sms)
			
		then:
			0 * sms.from(_)
			0 * sms.to(_)
			
		where:
			from			| to	
			null			| null	
			null			| []	
	}
}
