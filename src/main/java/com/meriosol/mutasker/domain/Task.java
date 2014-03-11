package com.meriosol.mutasker.domain;

/**
 * Task (unit of work in dedicated thread).
 *
 * @author meriosol
 * @version 0.1
 * @since 11/03/14
 */
public interface Task<T> {
    Result<T> execute();
}
