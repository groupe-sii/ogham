package testutils;

import static org.mockito.Mockito.spy;
import static testutils.SessionStrategyTestHelper.track;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TrackScheduledTasksDecorator implements ScheduledExecutorService {
	private final ScheduledExecutorService delegate;
	private final List<ScheduledFuture<?>> scheduledTasks;

	public TrackScheduledTasksDecorator(ScheduledExecutorService delegate, List<ScheduledFuture<?>> scheduledTasks) {
		super();
		this.delegate = delegate;
		this.scheduledTasks = scheduledTasks;
	}

	@Override
	public void shutdown() {
		delegate.shutdown();
	}

	@Override
	public List<Runnable> shutdownNow() {
		return delegate.shutdownNow();
	}

	@Override
	public boolean isShutdown() {
		return delegate.isShutdown();
	}

	@Override
	public boolean isTerminated() {
		return delegate.isTerminated();
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return delegate.awaitTermination(timeout, unit);
	}

	@Override
	public <T> Future<T> submit(Callable<T> task) {
		return delegate.submit(task);
	}

	@Override
	public <T> Future<T> submit(Runnable task, T result) {
		return delegate.submit(task, result);
	}

	@Override
	public Future<?> submit(Runnable task) {
		return delegate.submit(task);
	}

	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
		return delegate.invokeAll(tasks);
	}

	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
		return delegate.invokeAll(tasks, timeout, unit);
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
		return delegate.invokeAny(tasks);
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return delegate.invokeAny(tasks, timeout, unit);
	}

	@Override
	public void execute(Runnable command) {
		delegate.execute(command);
	}

	@Override
	public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
		return delegate.schedule(command, delay, unit);
	}

	@Override
	public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
		return delegate.schedule(callable, delay, unit);
	}

	@Override
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
		return delegate.scheduleAtFixedRate(command, initialDelay, period, unit);
	}

	@Override
	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
		ScheduledFuture<?> original = delegate.scheduleWithFixedDelay(command, initialDelay, delay, unit);
		ScheduledFuture<?> enquireLinkTask = spy(track(original, scheduledTasks.size()));
		scheduledTasks.add(enquireLinkTask);
		return enquireLinkTask;
	}
	
	public static class SpayableScheduledFuture<V> implements ScheduledFuture<V> {
		private final ScheduledFuture<V> delegate;

		public SpayableScheduledFuture(ScheduledFuture<V> delegate) {
			super();
			this.delegate = delegate;
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

	}
}
