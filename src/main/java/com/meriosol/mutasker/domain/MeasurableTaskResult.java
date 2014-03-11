package com.meriosol.mutasker.domain;

/**
 * TODO: consider moving stats specific methods from Result interface to this class.
 *
 * @author meriosol
 * @version 0.1
 * @since 11/03/14
 */
public abstract class MeasurableTaskResult<T> implements Result<T> {
    private long startTime;
    private long endTime;

    protected MeasurableTaskResult(long startTime, long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    protected MeasurableTaskResult(long startTime) {
        this(startTime, System.currentTimeMillis());
    }

    @Override
    public long getStartTime() {
        return this.startTime;
    }

    @Override
    public long getEndTime() {
        return this.endTime;
    }

}
