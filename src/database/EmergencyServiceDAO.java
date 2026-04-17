package database;

import java.sql.*;
import java.util.*;
import services.EmergencyServiceBase;
import services.PoliceStation;
import services.Ambulance;
import services.NGO;

public class EmergencyServiceDAO {

    public List<EmergencyServiceBase> getAllServices() {

        List<EmergencyServiceBase> list = new ArrayList<>();

        String sql = "SELECT * FROM Emergency_Service";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                EmergencyServiceBase service = createServiceFromResultSet(rs);
                if (service != null) {
                    list.add(service);
                }
            }

        } catch (Exception e) {
            System.out.println("DB Error: " + e.getMessage());
        }

        return list;
    }

    public List<EmergencyServiceBase> getServicesByType(String type) {

        List<EmergencyServiceBase> list = new ArrayList<>();

        String sql = "SELECT * FROM Emergency_Service WHERE type = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, type);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                EmergencyServiceBase service = createServiceFromResultSet(rs);
                if (service != null) {
                    list.add(service);
                }
            }

        } catch (Exception e) {
            System.out.println("DB Error: " + e.getMessage());
        }

        return list;
    }

    public EmergencyServiceBase getAvailableService(String type) {
        List<EmergencyServiceBase> services = getServicesByType(type);
        if (!services.isEmpty()) {
            return services.get(0);
        }
        return null;
    }

    private EmergencyServiceBase createServiceFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("service_id");
        String name = rs.getString("name");
        String type = rs.getString("type");
        String phone = rs.getString("contact");

        // Assuming the DB has columns for the specific fields, but since it's not, I'll use defaults
        switch (type.toLowerCase()) {
            case "police":
                return new PoliceStation(id, name, phone, "Available", name, "City Center");
            case "ambulance":
                return new Ambulance(id, name, phone, "Available", "AMB-" + id, name);
            case "ngo":
                return new NGO(id, name, phone, "Available", name, "Women Safety", "www." + name.toLowerCase() + ".org");
            default:
                return null;
        }
    }
}
