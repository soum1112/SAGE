package database;
import java.sql.*;

public class LocationDAO {

    public int getOrCreateLocation(String latLon, String city, String region) throws SQLException {
        String lat = "0.0", lon = "0.0";
        if (latLon != null && latLon.contains(",")) {
            lat = latLon.split(",")[0].trim();
            lon = latLon.split(",")[1].trim();
        }

        String address = city + ", " + region;


        String select = "SELECT location_id FROM location WHERE latitude = ? AND longitude = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(select)) {
            ps.setBigDecimal(1, new java.math.BigDecimal(lat));
            ps.setBigDecimal(2, new java.math.BigDecimal(lon));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("location_id"); // reuse existing
        }


        String insert = "INSERT INTO location (latitude, longitude, address) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setBigDecimal(1, new java.math.BigDecimal(lat));
            ps.setBigDecimal(2, new java.math.BigDecimal(lon));
            ps.setString(3, address);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        }

        throw new SQLException("Failed to get or create location.");
    }
}
