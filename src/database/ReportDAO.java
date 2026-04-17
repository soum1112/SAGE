package database;

import models.Report;
import java.sql.*;
import java.util.*;

public class ReportDAO {


    public void addReport(Report report) {

        String sql = "INSERT INTO Incident_Report (user_id, location_id, type, description, evidence) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, report.getUserId());
            ps.setInt(2, report.getLocationId());   
            ps.setString(3, report.getType());      
            ps.setString(4, report.getDescription());
            ps.setString(5, report.getEvidence());  

            ps.executeUpdate();
            System.out.println("Report Added!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public List<Report> getReports(int userId) {

        List<Report> list = new ArrayList<>();
        String sql = "SELECT * FROM Incident_Report WHERE user_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Report r = new Report();

                r.setReportId(rs.getInt("report_id"));
                r.setUserId(rs.getInt("user_id"));
                r.setLocationId(rs.getInt("location_id"));   
                r.setType(rs.getString("type"));             
                r.setDescription(rs.getString("description"));
                r.setEvidence(rs.getString("evidence"));     

                list.add(r);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
