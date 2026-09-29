package com.careerprep.dto;

public class PathReadinessResponse {
    private final String path;
    private final String name;
    private final long completed;
    private final long total;
    private final double readiness;

    public PathReadinessResponse(String path, String name, long completed, long total, double readiness) {
        this.path = path;
        this.name = name;
        this.completed = completed;
        this.total = total;
        this.readiness = readiness;
    }

    public String getPath() { return path; }
    public String getName() { return name; }
    public long getCompleted() { return completed; }
    public long getTotal() { return total; }
    public double getReadiness() { return readiness; }
}
