package oghamcore.it.core.builder


import fr.sii.ogham.core.builder.Builder
import fr.sii.ogham.core.builder.context.DefaultBuildContext
import fr.sii.ogham.core.builder.priority.Priority
import fr.sii.ogham.core.builder.template.TemplateBuilderHelper
import fr.sii.ogham.core.message.Message
import fr.sii.ogham.core.message.content.Content
import fr.sii.ogham.core.resource.path.ResourcePath
import fr.sii.ogham.core.template.context.Context
import fr.sii.ogham.core.template.parser.TemplateParser
import fr.sii.ogham.testing.extension.common.LogTestInformation
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@LogTestInformation
class TemplatePrioritySpec extends Specification {
	static parsedBy
	
	def setup() {
		parsedBy = []
	}
	
	def "using explicit priority should parse using implementation with highest priority"() {
		given:
			Message message = Mock()
			Object parent = Mock();
		
			def helper = new TemplateBuilderHelper(parent, new DefaultBuildContext())
			
			for (def builder : builders) {
				helper.register(builder)
			}
			TemplateParser parser = helper.buildTemplateParser()
			
		when:
			parser.parse(null, null)
		
		then:
			parsedBy.size() == 1
			parsedBy[0] == expected
			
			
		where:
			builders															|| expected
			[LowPriorityBuilder]												|| LowPriorityImpl
			[LowPriorityBuilder, MiddlePriorityBuilder]							|| MiddlePriorityImpl
			[MiddlePriorityBuilder, LowPriorityBuilder]							|| MiddlePriorityImpl
			[LowPriorityBuilder, MiddlePriorityBuilder, HighPriorityBuilder]	|| HighPriorityImpl
			[LowPriorityBuilder, HighPriorityBuilder, MiddlePriorityBuilder]	|| HighPriorityImpl
			[MiddlePriorityBuilder, HighPriorityBuilder, LowPriorityBuilder]	|| HighPriorityImpl
			[MiddlePriorityBuilder, LowPriorityBuilder, HighPriorityBuilder]	|| HighPriorityImpl
			[HighPriorityBuilder, LowPriorityBuilder, MiddlePriorityBuilder]	|| HighPriorityImpl
			[HighPriorityBuilder, MiddlePriorityBuilder, LowPriorityBuilder]	|| HighPriorityImpl
	}
	
	
	def "no explicit priority should parse using implementation with registration order"() {
		given:
			Message message = Mock()
			Object parent = Mock();
		
			def helper = new TemplateBuilderHelper(parent, new DefaultBuildContext())
			
			for (def builder : builders) {
				helper.register(builder)
			}
			TemplateParser parser = helper.buildTemplateParser()
			
		when:
			parser.parse(null, null)

		then:
			parsedBy.size() == 1
			parsedBy[0] == expected
			
			
		where:
			builders																				|| expected
			[NoExplicitPriorityBuilder1]															|| NoExplicitPriorityImpl1
			[NoExplicitPriorityBuilder1, NoExplicitPriorityBuilder2]								|| NoExplicitPriorityImpl1
			[NoExplicitPriorityBuilder2, NoExplicitPriorityBuilder1]								|| NoExplicitPriorityImpl2
			[NoExplicitPriorityBuilder1, NoExplicitPriorityBuilder2, NoExplicitPriorityBuilder3]	|| NoExplicitPriorityImpl1
			[NoExplicitPriorityBuilder1, NoExplicitPriorityBuilder3, NoExplicitPriorityBuilder2]	|| NoExplicitPriorityImpl1
			[NoExplicitPriorityBuilder2, NoExplicitPriorityBuilder3, NoExplicitPriorityBuilder1]	|| NoExplicitPriorityImpl2
			[NoExplicitPriorityBuilder2, NoExplicitPriorityBuilder1, NoExplicitPriorityBuilder3]	|| NoExplicitPriorityImpl2
			[NoExplicitPriorityBuilder3, NoExplicitPriorityBuilder1, NoExplicitPriorityBuilder2]	|| NoExplicitPriorityImpl3
			[NoExplicitPriorityBuilder3, NoExplicitPriorityBuilder2, NoExplicitPriorityBuilder1]	|| NoExplicitPriorityImpl3
	}

	def "mixing explicit priority and not should parse using implementation with highest priority first"() {
		given:
			Message message = Mock()
			Object parent = Mock();
		
			def helper = new TemplateBuilderHelper(parent, new DefaultBuildContext())
			
			for (def builder : builders) {
				helper.register(builder)
			}
			TemplateParser parser = helper.buildTemplateParser()
			
		when:
			parser.parse(null, null)
		
		then:
			parsedBy.size() == 1
			parsedBy[0] == expected
			
			
		where:
			builders															|| expected
			[LowPriorityBuilder, NoExplicitPriorityBuilder1]					|| LowPriorityImpl
			[NoExplicitPriorityBuilder1, LowPriorityBuilder]					|| LowPriorityImpl
	}
	

	
	
	
	
	public static class HighPriorityBuilder implements Builder<HighPriorityImpl> {
		@Override
		public HighPriorityImpl build() {
			return new HighPriorityImpl();
		}
	}
	
	public static class MiddlePriorityBuilder implements Builder<MiddlePriorityImpl> {
		@Override
		public MiddlePriorityImpl build() {
			return new MiddlePriorityImpl();
		}
	}
	
	public static class LowPriorityBuilder implements Builder<LowPriorityImpl> {
		@Override
		public LowPriorityImpl build() {
			return new LowPriorityImpl();
		}
	}
	
	public static class NoExplicitPriorityBuilder1 implements Builder<NoExplicitPriorityImpl1> {
		@Override
		public NoExplicitPriorityImpl1 build() {
			return new NoExplicitPriorityImpl1();
		}
	}
	
	public static class NoExplicitPriorityBuilder2 implements Builder<NoExplicitPriorityImpl2> {
		@Override
		public NoExplicitPriorityImpl2 build() {
			return new NoExplicitPriorityImpl2();
		}
	}

	public static class NoExplicitPriorityBuilder3 implements Builder<NoExplicitPriorityImpl3> {
		@Override
		public NoExplicitPriorityImpl3 build() {
			return new NoExplicitPriorityImpl3();
		}
	}
	
	private static class TrackOrder implements TemplateParser {
		@Override
		public Content parse(ResourcePath templateName, Context ctx) {
			parsedBy.add(getClass());
			return null;
		}
	}
	
	@Priority(defaultValue = 1000)
	private static class HighPriorityImpl extends TrackOrder {}

	@Priority(defaultValue = 900)
	private static class MiddlePriorityImpl extends TrackOrder {}

	@Priority(defaultValue = 800)
	private static class LowPriorityImpl extends TrackOrder {}
	
	private static class NoExplicitPriorityImpl1 extends TrackOrder {}
	
	private static class NoExplicitPriorityImpl2 extends TrackOrder {}
	
	private static class NoExplicitPriorityImpl3 extends TrackOrder {}
}
