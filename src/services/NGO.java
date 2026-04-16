package services;

public class NGO extends EmergencyServiceBase {

    private String ngoName;
    private String focusArea;
    private String website;

    public NGO(int serviceId, String name, String contact, String availability,
               String ngoName, String focusArea, String website) {
        super(serviceId, name, contact, availability, "NGO");
        this.ngoName = ngoName;
        this.focusArea = focusArea;
        this.website = website;
    }

    public String getNgoName() { return ngoName; }
    public String getFocusArea() { return focusArea; }
    public String getWebsite() { return website; }

    @Override
    public void displayInfo() {
        System.out.println("[ NGO / SUPPORT ORG ]");
        System.out.printf(" | Org Name : %-38s|%n", ngoName);
        System.out.printf(" | Focus    : %-38s|%n", focusArea);
        System.out.printf(" | Contact  : %-38s|%n", contact);
        System.out.printf(" | Website  : %-38s|%n", website != null ? website : "N/A");
        System.out.printf(" | Status   : %-38s|%n", availability);
    }

    @Override
    public void sendAlert(int userId, String location) {
        System.out.println(" >> NGO SUPPORT ALERT sent to " + ngoName);
        System.out.println("    Focus area: " + focusArea);
        System.out.println("    User #" + userId + " at: " + location + " needs support.");
        System.out.println("    Contact: " + contact + " | Web: " + website);
    }
}
