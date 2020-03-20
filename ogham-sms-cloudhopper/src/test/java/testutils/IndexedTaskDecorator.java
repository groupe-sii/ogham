package testutils;

import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class IndexedTaskDecorator<V> implements ScheduledFuture<V> {
	private final ScheduledFuture<V> delegate;
	private final int taskIdx;

	public IndexedTaskDecorator(ScheduledFuture<V> delegate, int taskIdx) {
		super();
		this.delegate = delegate;
		this.taskIdx = taskIdx;
	}

	@Override
	public long getDelay(TimeUnit unit) {
		return delegate.getDelay(unit);
	}

	@Override
	public int compareTo(Delayed o) {
		return delegate.compareTo(o);
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return delegate.cancel(mayInterruptIfRunning);
	}

	@Override
	public boolean isCancelled() {
		return delegate.isCancelled();
	}

	@Override
	public boolean isDone() {
		return delegate.isDone();
	}

	@Override
	public V get() throws InterruptedException, ExecutionException {
		return delegate.get();
	}

	@Override
	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return delegate.get(timeout, unit);
	}

	public int getTaskIdx() {
		return taskIdx;
	}

	@Override
	public String toString() {
		return "task[" + taskIdx + "]";
	}

}
