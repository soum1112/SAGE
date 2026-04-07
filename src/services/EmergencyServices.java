package services;

import java.sql.*;

public class EmergencyServices {

    private Connection conn;

    public EmergencyServices(Connection conn) {
        this.conn = conn;
    }

    public void triggerSOS(int userId, String status, String serviceType) {
        try {
            // STEP 1: Insert SOS
            String insertSOS = "INSERT INTO SOS_Alert(user_id, status) VALUES (?, ?)";
            PreparedStatement ps1 = conn.prepareStatement(insertSOS, Statement.RETURN_GENERATED_KEYS);
            ps1.setInt(1, userId);
            ps1.setString(2, status);
            ps1.executeUpdate();

            // STEP 2: Get generated SOS ID
            ResultSet rs = ps1.getGeneratedKeys();
            int sosId = 0;
            if (rs.next()) {
                sosId = rs.getInt(1);
            }

            // STEP 3: Get service_id from Emergency_Service
            String getService = "SELECT service_id FROM Emergency_Service WHERE type = ?";
            PreparedStatement ps2 = conn.prepareStatement(getService);
            ps2.setString(1, serviceType); // POLICE / HOSPITAL
            ResultSet rs2 = ps2.executeQuery();

            int serviceId = 0;
            if (rs2.next()) {
                serviceId = rs2.getInt("service_id");
            } else {
                System.out.println("Service not found!");
                return;
            }

            // STEP 4: Link SOS with Service
            String linkSOS = "INSERT INTO SOS_Service(sos_id, service_id) VALUES (?, ?)";
            PreparedStatement ps3 = conn.prepareStatement(linkSOS);
            ps3.setInt(1, sosId);
            ps3.setInt(2, serviceId);
            ps3.executeUpdate();

            System.out.println("✅ SOS triggered and linked to service!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
