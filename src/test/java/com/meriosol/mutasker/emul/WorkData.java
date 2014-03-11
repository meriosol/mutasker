package com.meriosol.mutasker.emul;

import org.apache.commons.lang3.StringUtils;

/**
 * Basic POJO for keeping data related to emulated work.
 *
 * @author meriosol
 * @version 0.1
 * @since 11/03/14
 */
public class WorkData {
    private String text;

    public WorkData() {
    }

    public WorkData(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "WorkData{" +
                "text='" + StringUtils.abbreviate(text, 100) + '\'' +
                '}';
    }
}
