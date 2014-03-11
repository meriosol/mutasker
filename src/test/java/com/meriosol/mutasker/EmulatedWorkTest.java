package com.meriosol.mutasker;

import com.meriosol.mutasker.domain.Result;
import com.meriosol.mutasker.domain.Task;
import com.meriosol.mutasker.domain.TaskResultStats;
import com.meriosol.mutasker.emul.WorkData;
import com.meriosol.mutasker.emul.WorkTask;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * This test class shows how library can be used to gather performance statistics.
 *
 * @author meriosol
 * @version 0.1
 * @since 11/03/14
 */
public class EmulatedWorkTest {
    private static Logger LOG = null;
    private static final int MAX_ITER_COUNT = 200000;
    private static final Random RANDOM = new Random();

    @BeforeClass
    public static void init() {
        LOG = LoggerFactory.getLogger(EmulatedWorkTest.class);
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testWork() {
        String testName = "testWork";
        String workUnitName = "emulUnit";

        int workersAmount = 300;
        // CAUTION: be careful with this thread amount (memory/CPU can be overloaded).

        List<Task<WorkData>> tasks = new ArrayList<Task<WorkData>>(workersAmount);
        for (int i = 0; i < workersAmount; i++) {
            int iterationCount = calculateIterationCount();
            Task<WorkData> task = new WorkTask<WorkData>(iterationCount);
            tasks.add(task);
        }

        LOG.debug("About to start emulated work with {} workers..", workersAmount);
        TaskPooledExecutor<WorkData> taskPooledExecutor = new TaskPooledExecutor<WorkData>("EXEC_" + testName);
        List<Result<WorkData>> results = taskPooledExecutor.perform(tasks);
        LOG.debug("Emulated work done.");

        printReportPerformanceStats(testName, workUnitName, results);
    }

    //--------------------------------------------------
    // Utils:

    /**
     * @return iteration count
     */
    private int calculateIterationCount() {
        return Math.abs(RANDOM.nextInt(MAX_ITER_COUNT));
    }

    /**
     * Logs reporting results performance stats.<br>
     * TODO: Write report in some format e.g. XML or JSON.
     *
     * @param testName
     * @param workUnitName
     * @param results
     */
    private void printReportPerformanceStats(String testName, String workUnitName, final List<Result<WorkData>> results) {
        if (results != null && results.size() > 0) {
            LOG.info("o>> ({}) ====================== Results report: V", testName);
            LOG.info("o>> ({}) '{}' results found..", testName, results.size());

            TaskResultStats<WorkData> stats = new TaskResultStats<WorkData>(Collections.unmodifiableList(results), workUnitName);
            String statsBasicInfo = stats.prepareBasicStatsReportForDurations();
            LOG.info("\n{}", statsBasicInfo);

            for (Result<WorkData> result : results) {
                if (result != null) {
                    int rowCount = 0;
                    WorkData report = result.getDetails();

                    long duration = stats.calculateDuration(result);
                    if (duration > 0) {
                        long durationPerUnit = stats.getDurationPerUnitOfWork(result);
                        if (durationPerUnit > 0) {
                            LOG.debug(" oo>> ({}) Result '{}', duration='{}' msecs({}) (or '{}' msecs per result), '{}' items found."
                                    , testName, result.getName(), duration, DurationFormatUtils.formatDurationWords(duration, true, true),
                                    durationPerUnit, rowCount);
                        } else {
                            LOG.debug(" oo>> ({}) Result '{}', WARN: duration/unit='{}' msecs (don't believe), '{}' items found."
                                    , testName, result.getName(), durationPerUnit, rowCount);
                        }
                    } else {
                        LOG.debug(" oo>> ({}) Result '{}', WARN: duration='{}' msecs (don't believe), '{}' items found."
                                , testName, result.getName(), duration, rowCount);
                    }
                } else {
                    LOG.warn(" oo>> ({}) One of search results is null.", testName);
                }
            }
            LOG.info("o>> ({}) ====================== Results report ^", testName);

        } else {
            LOG.warn("o>> ({}) No results provided.", testName);
        }
    }
}
