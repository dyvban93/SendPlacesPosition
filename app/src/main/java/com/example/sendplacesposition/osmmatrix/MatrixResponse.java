package com.example.sendplacesposition.osmmatrix;

import java.util.List;

public class MatrixResponse {


    private Info info;
    private List<List<Integer>> times;
    private List<List<Integer>> distances;

    public MatrixResponse(Info info, List<List<Integer>> times, List<List<Integer>> distances) {
        this.info = info;
        this.times = times;
        this.distances = distances;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public List<List<Integer>> getTimes() {
        return times;
    }

    public void setTimes(List<List<Integer>> times) {
        this.times = times;
    }

    public List<List<Integer>> getDistances() {
        return distances;
    }

    public void setDistances(List<List<Integer>> distances) {
        this.distances = distances;
    }

    public static class Info {
        private List<String> copyrights;

        public List<String> getCopyrights() {
            return copyrights;
        }

        public void setCopyrights(List<String> copyrights) {
            this.copyrights = copyrights;
        }
    }
}
