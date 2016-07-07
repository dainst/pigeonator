import com.fasterxml.jackson.databind.JsonNode;
import handler.Email;
import utils.JsonUtils;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;

import static spark.Spark.get;
import static spark.Spark.post;

/**
 * @author Patrick Jominet
 */
public class Pigeonator {

    public static void main(String[] args) {
        get("/mail", (request, response) -> "Your carrier pigeon is ready for takeoff.");
        post("/mail", (request, response) -> {
            JsonNode node = JsonUtils.stringToJson(request.body());
            if (node == null) {
                response.status(400);
                return "An error has occurred during parsing input";
            } else {
                // Create Mail
                Email emailToSend = new Email(
                        node.get("email").asText(),
                        node.get("to").asText(),
                        node.get("name").asText(),
                        node.get("subject").asText(),
                        node.get("message").asText()
                );
                System.out.println(emailToSend.toString());

                // Assuming you are sending email from localhost
                String host = "smtp.uni-koeln.de";

                // Get system properties
                Properties properties = System.getProperties();
                // Setup mail server
                properties.setProperty("mail.smtp.host", host);
                // Get the default Session object.
                Session session = Session.getDefaultInstance(properties);

                try{
                    // Create a default MimeMessage object.
                    MimeMessage message = new MimeMessage(session);

                    // Create message
                    message.setFrom(
                            new InternetAddress(emailToSend.getFrom())
                    );
                    message.addRecipient(
                            Message.RecipientType.TO,
                            new InternetAddress(emailToSend.getTo())
                    );
                    message.setSubject(emailToSend.getSubject());
                    message.setText(
                            "From: " + emailToSend.getName() + "\n" +
                            emailToSend.getMessage()
                    );

                    // Send message
                    Transport.send(message);
                    System.out.println("Sent message successfully....");

                }catch (MessagingException mex) {
                    mex.printStackTrace();
                }
            }
            return "Success";
        });
    }
}