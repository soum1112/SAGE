package interfaces;


public interface Alertable {
    void sendAlert(int userId, String location);
    String getAlertMessage();
}
