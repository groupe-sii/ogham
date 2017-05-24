package fr.sii.ogham.core.id.generator;

import java.util.concurrent.atomic.AtomicInteger;

public class SequentialIdGenerator implements IdGenerator {
	private AtomicInteger idx;
	
	public SequentialIdGenerator(int initial) {
		super();
		this.idx = new AtomicInteger(initial);
	}

	public SequentialIdGenerator() {
		this(0);
	}
	

	@Override
	public String generate(String name) {
		return name+idx.getAndIncrement();
	}

}
