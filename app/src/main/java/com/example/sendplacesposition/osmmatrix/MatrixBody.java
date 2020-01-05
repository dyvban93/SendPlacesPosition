package com.example.sendplacesposition.osmmatrix;

import java.util.List;

public class MatrixBody {


    private String vehicle;
    private List<String> out_arrays;
    private List<String> to_point_hints;
    private List<String> from_point_hints;
    private List<List<Double>> to_points;
    private List<List<Double>> from_points;

    public MatrixBody(String vehicle, List<String> out_arrays, List<String> to_point_hints, List<String> from_point_hints) {
        this.vehicle = vehicle;
        this.out_arrays = out_arrays;
        this.to_point_hints = to_point_hints;
        this.from_point_hints = from_point_hints;
    }

    public MatrixBody(String vehicle, List<String> out_arrays, List<String> to_point_hints, List<String> from_point_hints, List<List<Double>> to_points, List<List<Double>> from_points) {
        this.vehicle = vehicle;
        this.out_arrays = out_arrays;
        this.to_point_hints = to_point_hints;
        this.from_point_hints = from_point_hints;
        this.to_points = to_points;
        this.from_points = from_points;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public List<String> getOut_arrays() {
        return out_arrays;
    }

    public void setOut_arrays(List<String> out_arrays) {
        this.out_arrays = out_arrays;
    }

    public List<String> getTo_point_hints() {
        return to_point_hints;
    }

    public void setTo_point_hints(List<String> to_point_hints) {
        this.to_point_hints = to_point_hints;
    }

    public List<String> getFrom_point_hints() {
        return from_point_hints;
    }

    public void setFrom_point_hints(List<String> from_point_hints) {
        this.from_point_hints = from_point_hints;
    }

    public List<List<Double>> getTo_points() {
        return to_points;
    }

    public void setTo_points(List<List<Double>> to_points) {
        this.to_points = to_points;
    }

    public List<List<Double>> getFrom_points() {
        return from_points;
    }

    public void setFrom_points(List<List<Double>> from_points) {
        this.from_points = from_points;
    }
}
