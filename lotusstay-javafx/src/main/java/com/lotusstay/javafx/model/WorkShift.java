package com.lotusstay.javafx.model;

public class WorkShift {
    public Long id;
    public String shiftName;   // Tên ca: Ca sáng, Ca chiều, Ca đêm...
    public String startTime;   // HH:mm
    public String endTime;     // HH:mm
    public boolean active;

    public WorkShift() {}

    public WorkShift(Long id, String shiftName, String startTime, String endTime, boolean active) {
        this.id = id;
        this.shiftName = shiftName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.active = active;
    }
}
