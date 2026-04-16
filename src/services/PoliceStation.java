package services;

public class PoliceStation extends EmergencyServiceBase {

    private String stationName;
    private String area;

    public PoliceStation(int serviceId, String name, String contact,
                        String availability, String stationName, String area) {
        super(serviceId, name, contact, availability, "POLICE");
        this.stationName = stationName;
        this.area = area;
    }

    public String getStationName() { return stationName; }
    public String getArea() { return area; }

    @Override
    public void displayInfo() {
        System.out.println("[ POLICE STATION ]");
        System.out.printf(" | Name    : %-40s|%n", name);
        System.out.printf(" | Station : %-40s|%n", stationName);
        System.out.printf(" | Area    : %-40s|%n", area);
        System.out.printf(" | Contact : %-40s|%n", contact);
        System.out.printf(" | Status  : %-40s|%n", availability);

    }

    @Override
    public void sendAlert(int userId, String location) {
        System.out.println(" >> POLICE ALERT sent to " + stationName);
        System.out.println("    User #" + userId + " needs help at: " + location);
        System.out.println("    Dispatch to area: " + area);
        System.out.println("    Call: " + contact);
    }
}
