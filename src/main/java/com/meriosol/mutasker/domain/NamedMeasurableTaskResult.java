package com.meriosol.mutasker.domain;

/**
 * Named, measurable task result impl.
 *
 * @author meriosol
 * @version 0.1
 * @since 11/03/14
 */
public class NamedMeasurableTaskResult<T> extends MeasurableTaskResult<T> {
    private String name;
    private T details;
    private long unitsOfWork = 1;
    private long outputItemCount = 0;

    /**
     * @param startTime
     * @param name
     * @param details
     * @param unitsOfWork
     * @param outputItemCount
     */
    public NamedMeasurableTaskResult(long startTime, String name, T details, long unitsOfWork, long outputItemCount) {
        super(startTime);
        this.name = name;
        this.details = details;
        this.unitsOfWork = unitsOfWork;
        this.outputItemCount = outputItemCount;
    }

    /**
     * @param startTime
     * @param endTime
     * @param name
     * @param details
     */
    public NamedMeasurableTaskResult(long startTime, long endTime, String name, T details) {
        super(startTime, endTime);
        this.name = name;
        this.details = details;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public T getDetails() {
        return this.details;
    }

    @Override
    public long getAmountOfUnitsOfWork() {
        return this.unitsOfWork;
    }

    @Override
    public long getAmountOfOutputItems() {
        return this.outputItemCount;
    }
}
