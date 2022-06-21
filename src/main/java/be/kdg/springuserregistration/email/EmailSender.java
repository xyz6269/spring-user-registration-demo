package be.kdg.springuserregistration.email;

public interface EmailSender {
    void sendEmail(String to, String email);
}
