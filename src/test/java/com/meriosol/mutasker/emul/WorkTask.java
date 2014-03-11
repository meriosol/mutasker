package com.meriosol.mutasker.emul;

import com.meriosol.mutasker.domain.NamedMeasurableTaskResult;
import com.meriosol.mutasker.domain.Result;
import com.meriosol.mutasker.domain.Task;

/**
 * Basic task for emulated work.
 *
 * @author meriosol
 * @version 0.1
 * @since 11/03/14
 */
public class WorkTask<T> implements Task<T> {
    private int iterationCount = 1;

    public WorkTask() {
    }

    public WorkTask(int iterationCount) {
        this.iterationCount = iterationCount;
    }

    public int getIterationCount() {
        return iterationCount;
    }

    @Override
    public Result<T> execute() {
        Result<T> result = null;
        long startTime = System.currentTimeMillis();
        String name = "WorkEmulator";
        int unitsOfWork = 10;
        int outputItemsCount = 0;
        // Perform nothing:
        StringBuilder stringBuilder = new StringBuilder("TEST:");
        String importantNothing = "Nothing";
        for (int i = 0; i < iterationCount; i++) {
            stringBuilder.append(importantNothing + i + "|");
            outputItemsCount++;
        }
        WorkData workData = new WorkData(stringBuilder.toString());

        //Params: long startTime, String name, T details, long unitsOfWork, long outputItemCount
        // TODO: Amend 'T details' handling.
        // TODO:       [WARNING] WorkTask.java:[46,72] unchecked cast required: T,  found: WorkData
        // TODO: Idea is workData is that 'T'..
        result = new NamedMeasurableTaskResult<T>(startTime, name, (T) workData, unitsOfWork, outputItemsCount);
        return result;
    }
}