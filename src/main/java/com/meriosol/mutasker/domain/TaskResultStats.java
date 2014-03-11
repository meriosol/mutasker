package com.meriosol.mutasker.domain;

import com.meriosol.mutasker.util.StatsUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.List;

/**
 * Gathers stats for group of results.<br/>
 * CAUTION: class is not optimized itself. If its performance should be fast, code improvements to be done.
 *
 * @author meriosol
 * @version 0.1
 * @since 11/03/14
 */
public class TaskResultStats<T> {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = ISODateTimeFormat.dateTime();
    private List<Result<T>> results;
    private String workUnitName;

    /**
     * @param results Would be great to provide non-empty..
     */
    public TaskResultStats(List<Result<T>> results, String workUnitName) {
        this.results = results;
        this.workUnitName = workUnitName;
    }

    /**
     * @param value
     * @param base
     * @return Percentage of value related to base. CAUTION: if base is 0, just value will be returned.
     */
    private static double calculatePercentage(double value, double base) {
        if (base == 0) {
            return value;
        } else {
            return 100 * value / base;
        }
    }

    /**
     * @return How many results we have.
     */
    public int getResultsAmount() {
        return results == null ? 0 : results.size();
    }

    /**
     * @return How many units of work we have.
     */
    public int getAmountOfUnitsOfWork() {
        int result = 0;
        if (CollectionUtils.isNotEmpty(this.results)) {
            for (Result<T> resultToCheck : this.results) {
                result += resultToCheck != null ? resultToCheck.getAmountOfUnitsOfWork() : 0;
            }
        }
        return result;
    }

    /**
     * @return How many units of work we have.
     */
    public int getAmountOfOutputItems() {
        int result = 0;
        if (CollectionUtils.isNotEmpty(this.results)) {
            for (Result<T> resultToCheck : this.results) {
                result += resultToCheck != null ? resultToCheck.getAmountOfOutputItems() : 0;
            }
        }
        return result;
    }

    public String prepareBasicStatsReportForDurations() {
        StringBuilder builder = new StringBuilder("   88>> ===============================================V:\n");
        builder.append(String.format("   88>> Results basic stats for durations (work unit name '%s'):\n", this.workUnitName));

        long summaryOfDurations = calculateSummaryOfDurations();
        double meanForDurationPerUnitOfWork = findMeanForDurationPerUnitOfWork();
        double meanForDurationPerOutputItem = findMeanForDurationPerOutputItem();

        double sdForDurationPerUnitOfWork = findStandardDeviationForDurationPerUnitOfWork();
        double sdMeanForDurationPerUnitOfWork = findStandardDeviationMeanForDurationPerUnitOfWork();

        double sdForDurationPerUnitOfWorkPercentage = calculatePercentage(sdForDurationPerUnitOfWork, meanForDurationPerUnitOfWork);
        double sdMeanForDurationPerUnitOfWorkPercentage = calculatePercentage(sdMeanForDurationPerUnitOfWork, meanForDurationPerUnitOfWork);

        String fastestResultInfo = prepareBasicStatsReportForOutstandingResult(findTheFirstFastestResultPerUnitOfWork(), "1st the fastest/unit of work");
        String slowestResultInfo = prepareBasicStatsReportForOutstandingResult(findTheFirstSlowestResultPerUnitOfWork(), "1st the slowest/unit of work");

        builder.append(String.format("      88>> Total amount of: '%d' results; '%d' units_of_work; '%d' output_items; total_duration = '%s' msecs (%s).\n"
                , getResultsAmount(), getAmountOfUnitsOfWork(), getAmountOfOutputItems(), summaryOfDurations,
                DurationFormatUtils.formatDurationWords(summaryOfDurations, true, true))
        );
        builder.append(String.format("      88>>   Duration/OutputItem: MEAN(AVG) = '%.1f' msecs (note: unreliable value cause some items could be skipped).\n", meanForDurationPerOutputItem));
        builder.append(String.format("      88>>   Duration/UnitOfWork: MEAN(AVG) = '%.1f' msecs.\n", meanForDurationPerUnitOfWork));
        builder.append(String.format("      88>>   Duration/UnitOfWork: SD        = '%.1f' msecs ('%.1f' pct from AVG).\n", sdForDurationPerUnitOfWork, sdForDurationPerUnitOfWorkPercentage));
        builder.append(String.format("      88>>   Duration/UnitOfWork: SD MEAN   = '%.1f' msecs ('%.1f' pct from AVG).\n", sdMeanForDurationPerUnitOfWork, sdMeanForDurationPerUnitOfWorkPercentage));
        builder.append(String.format("      88>>   %s\n", fastestResultInfo));
        builder.append(String.format("      88>>   %s\n", slowestResultInfo));
        builder.append("   88>> ===============================================^");

        return builder.toString();
    }

    /**
     * @param outstandingResult
     * @param whatIsOutstanding
     * @return stats info for particular result
     */
    private String prepareBasicStatsReportForOutstandingResult(Result<T> outstandingResult, String whatIsOutstanding) {
        String report = "";
        if (outstandingResult != null) {
            long durationPerUnitOfWork = getDurationPerUnitOfWork(outstandingResult);
            long durationPerOutputItem = getDurationPerOutputItem(outstandingResult);
            DateTime startDate = new DateTime(outstandingResult.getStartTime());
            long duration = calculateDuration(outstandingResult);
            report = String.format("%s result: '%s' work units; '%s' output items; total_duration = '%s' msecs(%s); duration/unit = '%s' msecs; duration/output = '%s' msecs; started at '%s'; name = '%s'.",
                    whatIsOutstanding, outstandingResult.getAmountOfUnitsOfWork(), outstandingResult.getAmountOfOutputItems()
                    , duration, DurationFormatUtils.formatDurationWords(duration, true, true), durationPerUnitOfWork
                    , durationPerOutputItem, DATE_TIME_FORMATTER.print(startDate), outstandingResult.getName());
        }
        return report;
    }

    /**
     * @return The first the fastest result in a row.
     * @see #getDurationPerUnitOfWork
     */
    public Result<T> findTheFirstFastestResultPerUnitOfWork() {
        Result<T> result = null;
        if (CollectionUtils.isNotEmpty(this.results)) {
            for (Result<T> resultToCheck : this.results) {
                if (resultToCheck != null && resultToCheck.getAmountOfOutputItems() > 0) {
                    if (result == null) {
                        result = resultToCheck;
                    } else {
                        long durationPerUnitOfWorkToCheck = getDurationPerUnitOfWork(resultToCheck);
                        long durationPerUnitOfWorkPrevious = getDurationPerUnitOfWork(result);
                        if (durationPerUnitOfWorkPrevious > durationPerUnitOfWorkToCheck) {
                            result = resultToCheck;
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * @return The first the slowest result in a row.
     * @see #getDurationPerUnitOfWork
     */
    public Result<T> findTheFirstSlowestResultPerUnitOfWork() {
        Result<T> result = null;
        if (CollectionUtils.isNotEmpty(this.results)) {
            for (Result<T> resultToCheck : this.results) {
                if (resultToCheck != null && resultToCheck.getAmountOfOutputItems() > 0) {
                    if (result == null) {
                        result = resultToCheck;
                    } else {
                        long durationPerUnitOfWorkToCheck = getDurationPerUnitOfWork(resultToCheck);
                        long durationPerUnitOfWorkPrevious = getDurationPerUnitOfWork(result);
                        if (durationPerUnitOfWorkPrevious < durationPerUnitOfWorkToCheck) {
                            result = resultToCheck;
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * @param result
     * @return result execution time duration
     */
    public long calculateDuration(Result<T> result) {
        return result.getEndTime() - result.getStartTime();
    }

    /**
     * @return total time duration
     */
    public long calculateSummaryOfDurations() {
        long result = 0l;
        if (CollectionUtils.isNotEmpty(this.results)) {
            for (Result<T> resultToCheck : this.results) {
                result += getDurationPerUnitOfWork(resultToCheck);
            }
        }
        return result;
    }

    /**
     * @param result
     * @return duration per unit of work, in msecs
     */
    public long getDurationPerUnitOfWork(Result<T> result) {
        return result != null ? getDurationPerOutputItem(result, result.getAmountOfUnitsOfWork()) : 0;
    }

    /**
     * @param result
     * @return duration per output item, in msecs
     */
    public long getDurationPerOutputItem(Result<T> result) {
        return result != null ? getDurationPerOutputItem(result, result.getAmountOfOutputItems()) : 0;
    }

    private long getDurationPerOutputItem(Result<T> result, long unitCount) {
        long durationPerUnit = 0;
        long duration = calculateDuration(result);
        if (unitCount > 0) {
            durationPerUnit = duration / unitCount;
        }
        return durationPerUnit;
    }

    /**
     * @return SD for units of work duration
     * @see #gatherDurationsPerUnitOfWork
     */
    public double findStandardDeviationForDurationPerUnitOfWork() {
        double result = 0d;
        if (CollectionUtils.isNotEmpty(this.results)) {
            result = StatsUtils.calculateStandardDeviation(gatherDurationsPerUnitOfWork());
        }
        return result;
    }

    /**
     * @return SD mean for unit of work durations
     * @see #gatherDurationsPerUnitOfWork
     */
    public double findStandardDeviationMeanForDurationPerUnitOfWork() {
        double result = 0d;
        if (CollectionUtils.isNotEmpty(this.results)) {
            result = StatsUtils.calculateStandardDeviationMean(gatherDurationsPerUnitOfWork());
        }
        return result;
    }

    /**
     * @return Mean for unit of work durations
     */
    public double findMeanForDurationPerUnitOfWork() {
        double result = 0d;
        if (CollectionUtils.isNotEmpty(this.results)) {
            result = StatsUtils.calculateMean(gatherDurationsPerUnitOfWork());
        }
        return result;
    }

    /**
     * @return Mean for OutputItem durations
     */
    public double findMeanForDurationPerOutputItem() {
        double result = 0d;
        if (CollectionUtils.isNotEmpty(this.results)) {
            result = StatsUtils.calculateMean(gatherDurationsPerOutputItem());
        }
        return result;
    }

    /**
     * @return unit of work durations
     */
    public double[] gatherDurationsPerUnitOfWork() {
        double[] durationsPerUnitOfWork = null;
        if (CollectionUtils.isNotEmpty(this.results)) {
            int durationsArraySize = getResultsAmount();
            durationsPerUnitOfWork = new double[durationsArraySize];

            for (int i = 0; i < durationsArraySize; i++) {
                Result<T> resultToCheck = this.results.get(i);
                durationsPerUnitOfWork[i] = getDurationPerUnitOfWork(resultToCheck);
            }
        }
        return durationsPerUnitOfWork;
    }

    /**
     * @return output items durations
     */
    public double[] gatherDurationsPerOutputItem() {
        double[] result = null;
        if (CollectionUtils.isNotEmpty(this.results)) {
            int durationsArraySize = getResultsAmount();
            result = new double[durationsArraySize];

            for (int i = 0; i < durationsArraySize; i++) {
                Result<T> resultToCheck = this.results.get(i);
                result[i] = getDurationPerOutputItem(resultToCheck);
            }
        }
        return result;
    }

}
