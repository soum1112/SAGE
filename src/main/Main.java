package main;

import java.util.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import services.EmergencyServiceBase;
import services.ContactService;
import services.ReportService;
import database.DBConnection;
import database.EmergencyServiceDAO;

public class Main {
    static Scanner sc = new Scanner(System.in);
    static ContactService contactService = new ContactService();
    static ReportService reportService = new ReportService();
    static EmergencyServiceDAO serviceDAO = new EmergencyServiceDAO();
    static int loggedInUserId = -1;
    static String loggedInUserName = "";

    public static void main(String[] args) {
        printBanner();
        while (true) {
            System.out.println("   SAGE – SAFE ALWAYS");
            System.out.println("  1. Login");
            System.out.println("  2. Sign Up");
            System.out.println("  3. Exit");
            System.out.print("  Choice: ");
            int choice = readInt();
            switch (choice) {
                case 1: login();  break;
                case 2: signUp(); break;
                case 3:
                    System.out.println("  Stay safe. Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("  Invalid choice.");
            }
        }
    }

    static void login() {
        System.out.print("\n  Phone   : ");
        String phone = sc.nextLine().trim();
        System.out.print("  Password: ");
        String pass = sc.nextLine().trim();
        String sql = "SELECT user_id, name FROM `User` WHERE phone = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                loggedInUserId   = rs.getInt("user_id");
                loggedInUserName = rs.getString("name");
                System.out.println("  Welcome, " + loggedInUserName + "!");
                dashboard();
            } else {
                System.out.println("  Invalid credentials.");
            }
        } catch (Exception e) {
            System.out.println("  DB Error: " + e.getMessage());
        }
    }

    static void signUp() {
        System.out.println("\n  -- Create Account --");
        String name = "";
        while (true) {
            System.out.print("  Full Name : ");
            name = sc.nextLine().trim();
            if (isValidName(name)) {
                break;
            }
            System.out.println("  Invalid name. Use only letters and spaces, minimum 2 characters.");
        }
        String phone = "";
        while (true) {
            System.out.print("  Phone     : ");
            phone = sc.nextLine().trim();
            if (isValidPhone(phone)) {
                break;
            }
            System.out.println("  Invalid phone number. Enter digits only, optionally starting with +, and at least 7 characters.");
        }
        String email = "";
        while (true) {
            System.out.print("  Email     : ");
            email = sc.nextLine().trim();
            if (email.isEmpty() || isValidEmail(email)) {
                break;
            }
            System.out.println("  Invalid email. Please enter a valid email address or leave blank.");
        }
        System.out.print("  Password  : "); String pass  = sc.nextLine().trim();
        String sql = "INSERT INTO `User`(name, phone, email, password) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setString(3, email.isEmpty() ? null : email);
            ps.setString(4, pass);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next())
                System.out.println("  Account created! User ID: " + keys.getInt(1));
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("  Phone/Email already registered.");
        } catch (Exception e) {
            System.out.println("  DB Error: " + e.getMessage());
        }
    }

    static boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    static boolean isValidPhone(String phone) {
        return phone.matches("^\\+?[0-9]{7,15}$");
    }

    static boolean isValidName(String name) {
        return name.length() >= 2 && name.matches("^[A-Za-z ]+$");
    }

    static void dashboard() {
        while (true) {
            System.out.println("\nMAIN MENU \n Hello, " + loggedInUserName);
            System.out.println("  1. SOS");
            System.out.println("  2. Tips & Products");
            System.out.println("  3. Safe Zones");
            System.out.println("  4. Emergency Services");
            System.out.println("  5. Emergency Contacts");
            System.out.println("  6. Feedback");
            System.out.println("  7. Incident Reports");
            System.out.println("  8. Notifications");
            System.out.println("  9. Logout");
            System.out.print("  Choice: ");
            int choice = readInt();
            switch (choice) {
                case 1: sosMenu();                        break;
                case 2: tipsAndProducts();                break;
                case 3: safeZones();                      break;
                case 4: emergencyServices();              break;
                case 5: contactMenu();                    break;
                case 6: feedbackMenu();                   break;
                case 7: reportMenu();                     break;
                case 8: notificationsMenu(loggedInUserId); break;
                case 9:
                    System.out.println("  Logged out.");
                    loggedInUserId = -1;
                    return;
                default:
                    System.out.println("  Invalid choice.");
            }
        }
    }

    static void sosMenu() {
        System.out.println("\n  -- SOS ALERT --");
        System.out.println("  1. Send Location + Trigger SOS");
        System.out.println("  2. Back");
        System.out.print("  Choice: ");
        int ch = readInt();
        if (ch == 1) {
            String[] loc = getLocation();
            triggerSOS(loc[0], loc[1], loc[2]);
        }
    }

    public static String[] getLocation() {
        System.out.println("Fetching your location...");
        try {
            HttpURLConnection con = (HttpURLConnection) new URL("http://ipinfo.io/json").openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
            }
            String data   = sb.toString();
            String loc    = extractJson(data, "loc");
            String city   = extractJson(data, "city");
            String region = extractJson(data, "region");
            String lat = "0.0", lon = "0.0";
            if (loc != null && loc.contains(",")) {
                String[] parts = loc.split(",");
                lat = parts[0].trim();
                lon = parts[1].trim();
            }
            System.out.println("Location: " + city + ", " + region + " (" + lat + ", " + lon + ")");
            return new String[]{lat + "," + lon, city != null ? city : "Unknown", region != null ? region : "Unknown"};
        } catch (Exception e) {
            System.out.println("Location fetch failed: " + e.getMessage());
            return new String[]{"0.0,0.0", "Unknown", "Unknown"};
        }
    }

    private static String extractJson(String json, String key) {
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(
            "\"" + key + "\"\\s*:\\s*\"([^\"]+)\""
        );
        java.util.regex.Matcher m = p.matcher(json);
        return m.find() ? m.group(1) : null;
    }

    static void triggerSOS(String location, String city, String region) {
        System.out.println("  SOS TRIGGERED!");
        System.out.println("  Location: " + location + " (" + city + ", " + region + ")");
        alertNearestService("POLICE", location);
        alertNearestService("AMBULANCE", location);
        alertNearestService("NGO", location);
        System.out.println("  Emergency contacts notified.");
    }

    static void alertNearestService(String type, String location) {
        EmergencyServiceBase service = serviceDAO.getAvailableService(type);
        if (service != null) {
            service.sendAlert(loggedInUserId, location);
        } else {
            System.out.println("  No " + type + " service available.");
        }
        
    }

    static void tipsAndProducts() {
        System.out.println("\n  -- SAFETY TIPS & PRODUCTS --");
        System.out.println("  1. View Safety Tips");
        System.out.println("  2. Recommended Products");
        System.out.println("  3. Back");
        System.out.print("  Choice: ");
        int ch = readInt();
        switch (ch) {
            case 1: System.out.println("  Tip: Always share your location with trusted contacts."); break;
            case 2: System.out.println("  Product: Personal Alarm - $19.99"); break;
        }
    }

    static void safeZones() {
        System.out.println("\n  -- SAFE ZONES --");
        System.out.println("  Safe Zone 1: Central Park - Well lit, patrolled.");
        System.out.println("  Safe Zone 2: Mall Area - CCTV coverage.");
    }

    static void emergencyServices() {
        System.out.println("\n  -- EMERGENCY SERVICES --");
        System.out.println("  1. View All Services");
        System.out.println("  2. View Police Stations");
        System.out.println("  3. View Ambulances");
        System.out.println("  4. View NGOs");
        System.out.println("  5. Back");
        System.out.print("  Choice: ");
        int ch = readInt();
        switch (ch) {
            case 1: showServicesPolymorphic(null);        break;
            case 2: showServicesPolymorphic("POLICE");    break;
            case 3: showServicesPolymorphic("AMBULANCE"); break;
            case 4: showServicesPolymorphic("NGO");       break;
        }
    }

    static void showServicesPolymorphic(String type) {
        List<EmergencyServiceBase> services = (type == null)
            ? serviceDAO.getAllServices()
            : serviceDAO.getServicesByType(type);
        System.out.println("\n  ---- Services (" + (type != null ? type : "ALL") + ") ----");
        for (EmergencyServiceBase service : services) {
            service.displayInfo();
            System.out.println();
        }
    }

    static void contactMenu() {
    System.out.println("\n  -- EMERGENCY CONTACTS --");
    System.out.println("  1. View Contacts");
    System.out.println("  2. Add Contact");
    System.out.println("  3. Update Contact");
    System.out.println("  4. Delete Contact");
    System.out.println("  5. Back");
    System.out.print("  Choice: ");

    int ch = readInt();

    switch (ch) {
        case 1:
            contactService.viewContacts(loggedInUserId);
            break;

        case 2:
            System.out.print("  Name: ");
            String name = sc.nextLine().trim();

            System.out.print("  Phone: ");
            String phone = sc.nextLine().trim();

            System.out.print("  Relation: ");
            String relation = sc.nextLine().trim();

            contactService.addContact(loggedInUserId, name, phone, relation);
            break;

        case 3:
            System.out.print("  Enter Contact ID to update: ");
            int updateId = readInt();

            System.out.print("  New Name: ");
            String newName = sc.nextLine().trim();

            System.out.print("  New Phone: ");
            String newPhone = sc.nextLine().trim();

            System.out.print("  New Relation: ");
            String newRelation = sc.nextLine().trim();

            contactService.updateContact(updateId, newName, newPhone, newRelation);
            break;

        case 4:
            System.out.print("  Enter Contact ID to delete: ");
            int deleteId = readInt();

            contactService.deleteContact(deleteId);
            break;

        case 5:
            return;

        default:
            System.out.println("  Invalid choice.");
    }
}

    static void feedbackMenu() {
        System.out.println("\n  -- FEEDBACK --");
        System.out.print("  Your feedback: ");
        String feedback = sc.nextLine().trim();
        System.out.println("  Thank you for your feedback: " + feedback);
    }

    static void reportMenu() {
        System.out.println("\n  -- INCIDENT REPORTS --");
        System.out.println("  1. View Reports");
        System.out.println("  2. File Report");
        System.out.println("  3. Back");
        System.out.print("  Choice: ");
        int ch = readInt();
        switch (ch) {
            case 1:
                reportService.viewReports(loggedInUserId);
                break;
            case 2:
                System.out.print("  Type (e.g. Theft, Assault, General): ");
                String type = sc.nextLine().trim();
                System.out.print("  Description: ");
                String desc = sc.nextLine().trim();
                System.out.print("  Evidence (optional, press Enter to skip): ");
                String evidence = sc.nextLine().trim();

                String[] loc = getLocation(); 
                reportService.addReport( 
                    loggedInUserId,
                    desc,
                    type.isEmpty() ? "General" : type,
                    evidence.isEmpty() ? null : evidence,
                    loc
                );
                break;
        }
    }

    static void notificationsMenu(int userId) {
        System.out.println("\n  -- NOTIFICATIONS --");
        String sql = "SELECT * FROM Notification WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                System.out.println("Type: "    + rs.getString("n_type") + (rs.getBoolean("is_read") ? "" : " [NEW]"));
                System.out.println("Message: " + rs.getString("message"));
                System.out.println("Time: "    + rs.getString("created_at"));
                System.out.println();
            }
            if (!hasData) {
                System.out.println("  No notifications.");
                return;
            }
            String updateSql = "UPDATE Notification SET is_read = 1 WHERE user_id = ?";
            try (PreparedStatement ps2 = conn.prepareStatement(updateSql)) {
                ps2.setInt(1, userId);
                ps2.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println("Error loading notifications: " + e.getMessage());
        }
    }

    static int readInt() {
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (Exception e) {
            return -1;
        }
    }

    static void printBanner() {}
}
