package com.inditeperks.lege;

import java.util.Date;

public class Event {

    private Date date;
    private String events;

    public Event(Date date, String events) {
        this.date = date;
        this.events = events;

    }

    public Date getDate() {
        return date;
    }

    public String getEvents() {
        return events;
    }
}
