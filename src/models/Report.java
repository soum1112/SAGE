package models;

public class Report {
    private int reportId;
    private int userId;
    private int locationId;
    private String type;
    private String description;
    private String evidence;

    public Report() {}

    public Report(int userId, String description) {
        this.userId = userId;
        this.description = description;
    }

    public int getReportId() { return reportId; }
    public int getUserId() { return userId; }
    public int getLocationId() { return locationId; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public String getEvidence() { return evidence; }

    public void setReportId(int reportId) { this.reportId = reportId; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setLocationId(int locationId) { this.locationId = locationId; }
    public void setType(String type) { this.type = type; }
    public void setDescription(String description) { this.description = description; }
    public void setEvidence(String evidence) { this.evidence = evidence; }
}
