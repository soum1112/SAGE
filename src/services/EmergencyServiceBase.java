package services;

import interfaces.Alertable;
import interfaces.Displayable;

public abstract class EmergencyServiceBase implements Alertable, Displayable {

    protected int serviceId;
    protected String name;
    protected String contact;
    protected String availability;
    protected String type;

    public EmergencyServiceBase(int serviceId, String name, String contact,
                                String availability, String type) {
        this.serviceId = serviceId;
        this.name = name;
        this.contact = contact;
        this.availability = availability;
        this.type = type;
    }

    public int getServiceId() { return serviceId; }
    public String getName() { return name; }
    public String getContact() { return contact; }
    public String getAvailability() { return availability; }
    public String getType() { return type; }

    @Override
    public abstract void displayInfo();

    @Override
    public abstract void sendAlert(int userId, String location);

    @Override
    public String getAlertMessage() {
        return "[" + type + "] " + name + " has been notified. Contact: " + contact;
    }

    @Override
    public String toString() {
        return String.format("[%d] %-25s | %-12s | Phone: %-15s | %s",
                serviceId, name, type, contact, availability);
    }
}
