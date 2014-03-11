package com.meriosol.mutasker;

import com.meriosol.mutasker.domain.CallableTask;
import com.meriosol.mutasker.domain.Result;
import com.meriosol.mutasker.domain.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Multi-threaded executor of tasks. Each task will have own thread for execution.
 *
 * @author meriosol
 * @version 0.1
 * @since 11/03/14
 */
public class TaskPooledExecutor<T> {
    private static Logger LOG = LoggerFactory.getLogger(TaskPooledExecutor.class);
    private final static transient int DEFAULT_SHUTDOWN_TIMEOUT = 600; // in seconds
    private final static transient String DEFAULT_EXECUTOR_NAME = "TaskPooledExecutor";
    private int shutdownTimeout = DEFAULT_SHUTDOWN_TIMEOUT;
    private String name = DEFAULT_EXECUTOR_NAME;

    public TaskPooledExecutor() {
        this(DEFAULT_EXECUTOR_NAME, DEFAULT_SHUTDOWN_TIMEOUT);
    }

    public TaskPooledExecutor(String name) {
        this(name, DEFAULT_SHUTDOWN_TIMEOUT);
    }

    public TaskPooledExecutor(String name, int shutdownTimeout) {
        this.name = name;
        this.shutdownTimeout = shutdownTimeout;
    }

    /**
     * @param shutdownTimeout timeout in seconds; after this timeout executor will enforce all task threads termination.
     */
    public TaskPooledExecutor(int shutdownTimeout) {
        this(DEFAULT_EXECUTOR_NAME, shutdownTimeout);
    }

    private void shutdownAndAwaitTermination(String executorName, ExecutorService pool, int timeout) {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(timeout, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(timeout, TimeUnit.SECONDS)) {
                    LOG.error("Pool did not terminate for executor '%s'!", executorName);
                }
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    public List<Result<T>> perform(List<Task<T>> tasks) {
        if (tasks == null || tasks.size() <= 0) {
            throw new IllegalArgumentException("Illegal rate: tasks param should not be empty list!");
        }
        int taskAmount = tasks.size();
        LOG.info("Executor '%s' requested to perform '%s' tasks..", getName(), taskAmount);
        List<Result<T>> results = new ArrayList<>(taskAmount);

        List<Future<Result<T>>> futureWorkResults = new ArrayList<>(taskAmount);
        ExecutorService executor = Executors.newFixedThreadPool(taskAmount);

        for (int i = 0; i < taskAmount; i++) {
            Task<T> task = tasks.get(i);
            CallableTask<T> callableTask = new CallableTask<T>(task);
            Future<Result<T>> future = executor.submit(callableTask);
            futureWorkResults.add(future);
        }
        LOG.info("'%s' tasks were submitted to executor '%s'..", futureWorkResults.size(), getName());

        try {
            LOG.info(String.format("Executor '%s' done its work for '%s' items. Results:", getName(), futureWorkResults.size()));
            for (Future<Result<T>> futureWorkResult : futureWorkResults) {
                results.add(futureWorkResult.get());
            }
        } catch (InterruptedException | ExecutionException ex) {
            // LOG.error("(%s) Future of Result handling was interrupted.", getName(), ex);
            throw new RuntimeException(String.format("(%s) Future of Result handling was interrupted.", getName()), ex);
        }

        shutdownAndAwaitTermination(getName(), executor, this.shutdownTimeout);

        return results;
    }

    public String getName() {
        return name;
    }
}
