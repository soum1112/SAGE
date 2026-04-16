package services;

public class Ambulance extends EmergencyServiceBase {

    private String vehicleNumber;
    private String hospitalName;

    public Ambulance(int serviceId, String name, String contact,
                    String availability, String vehicleNumber, String hospitalName) {
        super(serviceId, name, contact, availability, "AMBULANCE");
        this.vehicleNumber = vehicleNumber;
        this.hospitalName = hospitalName;
    }

    public String getVehicleNumber() { return vehicleNumber; }
    public String getHospitalName() { return hospitalName; }

    @Override
    public void displayInfo() {
        System.out.println("  AMBULANCE ]");
        System.out.printf(" | Service  : %-39s|%n", name);
        System.out.printf(" | Vehicle  : %-39s|%n", vehicleNumber);
        System.out.printf(" | Hospital : %-39s|%n", hospitalName);
        System.out.printf(" | Contact  : %-39s|%n", contact);
        System.out.printf(" | Status   : %-39s|%n", availability);
        System.out.println(" ");
    }

    @Override
    public void sendAlert(int userId, String location) {
        System.out.println(" >> AMBULANCE ALERT dispatched from " + hospitalName);
        System.out.println("    Vehicle: " + vehicleNumber + " heading to: " + location);
        System.out.println("    User " + userId + " requires medical assistance.");
        System.out.println("    Contact: " + contact);
    }
}
