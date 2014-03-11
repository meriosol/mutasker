package com.meriosol.mutasker.util;

/**
 * Stats util methods.
 *
 * @author meriosol
 * @version 0.1
 * @since 11/03/14
 */
public class StatsUtils {

    private StatsUtils() {
    }

    /**
     * Util for Standard Deviation (SD) calculation.<br/>
     * NOTE: ideas grabbed from @link{http://nscraps.com/Java/720-java-calculate-standard-deviation.htm}.
     *
     * @param data
     * @return Stats SD
     */
    public static double calculateStandardDeviation(double[] data) {
        double result = 0d;
        int dataLength = data != null ? data.length : 0;
        if (dataLength > 1) {
            double averageValue = data[0];
            double summary = 0d;
            for (int i = 1; i < dataLength; i++) {
                double dataItem = data[i];
                double deviation = dataItem - averageValue;
                double newAverageValue = averageValue + deviation / (i + 1);
                summary += deviation * deviation;
                averageValue = newAverageValue;
            }
            // Change to ( n - 1 ) to n if you have complete data instead of a sample.
            // result = Math.sqrt(sum / (dataLength - 1));
            result = Math.sqrt(summary / (dataLength));
        }
        return result;

    }

    /**
     * Util for Standard Deviation (SD) mean calculation.<br/>
     * NOTE: ideas grabbed from @link{http://nscraps.com/Java/720-java-calculate-standard-deviation.htm}.
     *
     * @param data
     * @return SD mean
     */
    public static double calculateStandardDeviationMean(double[] data) {
        double result = 0d;
        int dataLength = data != null ? data.length : 0;
        if (dataLength > 1) {
            double meanValue = calculateMean(data);

            double summary = 0d;
            for (int i = 1; i < dataLength; i++) {
                double dataItem = data[i];
                double deviation = dataItem - meanValue;
                summary += deviation * deviation;
            }
            // Change to ( n - 1 ) to n if you have complete data instead of a sample.
            // result = Math.sqrt(sum / (dataLength - 1));
            result = Math.sqrt(summary / (dataLength));
        }
        return result;
    }

    /**
     * Util for mean calculation.<br/>
     * NOTE: ideas grabbed from @link{http://nscraps.com/Java/720-java-calculate-standard-deviation.htm}.
     *
     * @param data
     * @return mean (average) value
     */
    public static double calculateMean(double[] data) {
        double result = 0d;
        int dataLength = data != null ? data.length : 0;
        if (dataLength > 1) {
            for (int i = 1; i < dataLength; i++) {
                result += data[i];
            }
            result /= dataLength;
        }
        return result;
    }

}
