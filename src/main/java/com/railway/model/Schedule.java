package com.railway.model;

import java.time.LocalTime;
import java.util.Objects;

public class Schedule {
    private String trainNumber;
    private String stationName;
    private String scheduleId;
    private LocalTime arrivalTime;
    private LocalTime departureTime;

    public Schedule() {
    }

    public Schedule(String trainNumber, String stationName, String scheduleId,
                    LocalTime arrivalTime, LocalTime departureTime) {
        this.trainNumber = trainNumber;
        this.stationName = stationName;
        this.scheduleId = scheduleId;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Schedule schedule = (Schedule) o;
        return Objects.equals(trainNumber, schedule.trainNumber) &&
                Objects.equals(stationName, schedule.stationName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trainNumber, stationName);
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "trainNumber='" + trainNumber + '\'' +
                ", stationName='" + stationName + '\'' +
                ", arrivalTime=" + arrivalTime +
                ", departureTime=" + departureTime +
                '}';
    }
}
