package com.lotusstay.javafx.model;

public class ShiftAssignment {
    public Long id;
    public Staff staff;
    public WorkShift workShift;
    public String workDate;        // dd/MM/yyyy
    public Attendance attendance;  // có thể null

    public ShiftAssignment() {}

    public ShiftAssignment(Long id, Staff staff, WorkShift workShift, String workDate) {
        this.id = id;
        this.staff = staff;
        this.workShift = workShift;
        this.workDate = workDate;
    }
}
