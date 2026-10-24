package com.lotusstay.javafx.model;

public class Attendance {
    public Long id;
    public ShiftAssignment shiftAssignment;
    public String checkInTime;   // HH:mm hoặc null
    public String checkOutTime;  // HH:mm hoặc null
    public String status;        // DU_CONG, DI_TRE, VE_SOM, VANG_MAT
    public String note;

    public Attendance() {}

    public Attendance(Long id, ShiftAssignment shiftAssignment,
                      String checkInTime, String checkOutTime, String status, String note) {
        this.id = id;
        this.shiftAssignment = shiftAssignment;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.status = status;
        this.note = note;
    }
}
