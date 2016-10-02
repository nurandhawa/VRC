package ca.sfu.teambeta.logic;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import ca.sfu.teambeta.core.User;

/**
 * Notifier class to send an email to players regarding their scheduled play time and group
 */
public class EmailNotifier implements Notifier {
    public static final String EMAIL_SMTP_HOSTNAME = "localhost";
    public static final int EMAIL_SMTP_PORT = 587;
    public static final String EMAIL_SMTP_USERNAME = "test";
    public static final String EMAIL_SMTP_PASSWORD = "password";

    private static final String KEY_TRANSPORT_PROTOCOL = "mail.transport.protocol";
    private static final String KEY_SMTP_HOST = "mail.smtp.host";
    private static final String KEY_SMTP_PORT = "mail.smtp.port";
    private static final String KEY_SMTP_AUTH = "mail.smtp.auth";

    private Properties mailServerProperties;

    public EmailNotifier() {
        mailServerProperties = new Properties();
        mailServerProperties.put(KEY_TRANSPORT_PROTOCOL, "smtp");
        mailServerProperties.put(KEY_SMTP_HOST, EMAIL_SMTP_HOSTNAME);
        mailServerProperties.put(KEY_SMTP_PORT, EMAIL_SMTP_PORT);
        mailServerProperties.put(KEY_SMTP_AUTH, "true");
    }

    @Override
    public void notify(User user) {
        Session session = createSession();

        try {
            Address[] recipients = {new InternetAddress(user.getEmail())};
            Message message = createMessage(recipients, session);

            Transport transport = session.getTransport();
            transport.connect();
            transport.sendMessage(message, recipients);
            transport.close();

        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    private Message createMessage(Address[] recipients, Session session) throws MessagingException {
        Message message = new MimeMessage(session);

        message.addRecipients(Message.RecipientType.TO, recipients);

        message.setSubject("Test message");

        BodyPart bodyPart = new MimeBodyPart();
        bodyPart.setText("This is a test email message.");
        message.setContent(new MimeMultipart(bodyPart));

        return message;
    }

    private Session createSession() {
        return Session.getDefaultInstance(mailServerProperties, new SMTPAuthenticator());
    }

    private class SMTPAuthenticator extends Authenticator {
        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(EMAIL_SMTP_USERNAME, EMAIL_SMTP_PASSWORD);
        }
    }

}
