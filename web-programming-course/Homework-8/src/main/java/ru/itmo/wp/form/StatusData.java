package ru.itmo.wp.form;

import javax.persistence.Id;
import javax.validation.constraints.Pattern;

public class StatusData {
    @Id
    private long userId;

    private boolean status;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
