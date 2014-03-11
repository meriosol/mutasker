package com.meriosol.mutasker.domain;

/**
 * Result of performing task.<br/>
 * TODO: maybe source task itself needs to be added as well.<br/>
 * TODO: trend: this interface getting more and more stats specific methods.<br/>
 *
 * @author meriosol
 * @version 0.1
 * @since 11/03/14
 */
public interface Result<T> {
    /**
     * @return result name (or base info about task this result is about).
     */
    String getName();

    /**
     * @return details of result.
     */
    T getDetails();

    /**
     * @return when task was started. Needed for basic stats.
     */
    long getStartTime();

    /**
     * @return when task was finished. Needed for basic stats.
     */
    long getEndTime();

    /**
     * @return How many units of work were performed during test.
     * For instance how many stock symbols were used to load quotes, Value of (getEndTime() - getStartTime())/getCheckedUnitsOfWork()
     * can reveal how much time approx was used per unit.  Needed for basic stats.
     */
    long getAmountOfUnitsOfWork();

    /**
     * @return How many output items were produced during test.
     * For instance how many stock quotes were returned. Generally amount is the same as provided by <code>getAmountOfUnitsOfWork()</code>.
     * But some methods can return e.g. 2 quotes per symbol or less quotes if some weren't found. Needed for basic stats, but important yet.
     */
    long getAmountOfOutputItems();
}
