package ru.itmo.wp.model.domain;

import java.io.Serializable;

public class Talk extends Entity implements Serializable {
    private long sourceId;
    private long targetId;
    private String text;

    public long getSourceId() {
        return sourceId;
    }

    public void setSourceId(long sourceId) {
        this.sourceId = sourceId;
    }

    public long getTargetId() {
        return targetId;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
