package fr.sii.notification.core.id.generator;


public class SequentialIdGenerator implements IdGenerator {
	private int idx;
	
	public SequentialIdGenerator(int initial) {
		super();
		this.idx = initial;
	}

	public SequentialIdGenerator() {
		this(0);
	}
	

	@Override
	public String generate(String name) {
		return "name"+(idx++);
	}

}
