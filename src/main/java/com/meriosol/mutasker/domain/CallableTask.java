package com.meriosol.mutasker.domain;

import java.util.concurrent.Callable;

/**
 * Wrapper around task.
 *
 * @author meriosol
 * @version 0.1
 * @since 11/03/14
 */
public class CallableTask<T> implements Callable<Result<T>> {
    private Task<T> task;

    public CallableTask(Task<T> task) {
        this.task = task;
    }

    @Override
    public Result<T> call() throws Exception {
        return this.task.execute();
    }
}
