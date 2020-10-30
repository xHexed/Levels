package com.grassminevn.levels.managers;

import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public final class AsyncExecutorManager extends AbstractExecutorService implements ScheduledExecutorService {
    private final ExecutorService taskService;
    private final ScheduledExecutorService timerExecutionService;
    private final Set<ScheduledFuture<?>> tasks = Collections.newSetFromMap(new WeakHashMap<>());

    public AsyncExecutorManager(final Plugin plugin) {
        taskService           = Executors.newCachedThreadPool(r -> {
            final Thread thread = Executors.defaultThreadFactory().newThread(r);
            thread.setName(plugin.getName() + "-scheduler");
            thread.setDaemon(true);
            return thread;
        });
        timerExecutionService = Executors.newSingleThreadScheduledExecutor(r -> {
            final Thread thread = Executors.defaultThreadFactory().newThread(r);
            thread.setName(plugin.getName() + "-scheduler-timer");
            thread.setDaemon(true);
            return thread;
        });
    }

    private ScheduledFuture<?> consumeTask(final ScheduledFuture<?> future) {
        synchronized (tasks) {
            tasks.add(future);
        }
        return future;
    }

    public void cancelRepeatingTasks() {
        synchronized (tasks) {
            for (final ScheduledFuture<?> task : tasks)
                task.cancel(false);
        }
    }

    public void execute(final Runnable runnable) {
        taskService.execute(runnable);
    }

    public ScheduledFuture<?> schedule(final Runnable command, final long delay, final TimeUnit unit) {
        return consumeTask(timerExecutionService.schedule(() -> taskService.execute(command), delay, unit));
    }

    public <V> ScheduledFuture<V> schedule(final Callable<V> callable, final long delay, final TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    public ScheduledFuture<?> scheduleAtFixedRate(final Runnable command, final long initialDelay, final long period, final TimeUnit unit) {
        return consumeTask(timerExecutionService.scheduleAtFixedRate(new FixedRateWorker(command), initialDelay, period, unit));
    }

    public ScheduledFuture<?> scheduleWithFixedDelay(final Runnable command, final long initialDelay, final long delay, final TimeUnit unit) {
        return scheduleAtFixedRate(command, initialDelay, delay, unit);
    }

    public void shutdown() {}

    public List<Runnable> shutdownNow() {
        return Collections.emptyList();
    }

    public boolean isShutdown() {
        return false;
    }

    public boolean isTerminated() {
        return false;
    }

    public boolean awaitTermination(final long timeout, final TimeUnit unit) {
        throw new IllegalStateException("Not shutdown");
    }

    private final class FixedRateWorker implements Runnable {
        private final Runnable delegate;
        private final ReentrantLock lock = new ReentrantLock();
        private final AtomicInteger running = new AtomicInteger(0);

        private FixedRateWorker(final Runnable delegate) {
            this.delegate = delegate;
        }

        public void run() {
            if (running.incrementAndGet() > 2) {
                running.decrementAndGet();
                return;
            }
            taskService.execute(() -> {
                lock.lock();
                try {
                    delegate.run();
                } finally {
                    lock.unlock();
                    running.decrementAndGet();
                }
            });
        }
    }
}
