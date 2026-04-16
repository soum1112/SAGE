// ReportService.java
package services;
import database.ReportDAO;
import database.LocationDAO;
import models.Report;
import java.util.*;

public class ReportService {
    private ReportDAO reportDAO = new ReportDAO();
    private LocationDAO locationDAO = new LocationDAO();

    public void addReport(int userId, String desc, String type, String evidence, String[] loc) {
    try {
        int locationId = locationDAO.getOrCreateLocation(loc[0], loc[1], loc[2]);
        Report r = new Report();
        r.setUserId(userId);
        r.setDescription(desc);
        r.setType(type);
        r.setEvidence(evidence);
        r.setLocationId(locationId);
        reportDAO.addReport(r);
        System.out.println("  Report filed successfully.");
    } catch (Exception e) {
        System.out.println("  Failed to file report: " + e.getMessage());
    }
}

    public void viewReports(int userId) {
        List<Report> list = reportDAO.getReports(userId);
        for (Report r : list) {
            System.out.println(
                r.getReportId() + " | " +
                r.getType() + " | " +
                r.getDescription()
            );
        }
    }
}
