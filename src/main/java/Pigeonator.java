import handler.Email;
import utils.JsonUtils;
import utils.RegexUtils;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.*;

import static spark.Spark.get;
import static spark.Spark.post;

/**
 * @author Patrick Jominet
 */
public class Pigeonator {

    private static final Logger logger = LogManager.getLogger(Pigeonator.class);
    private static String errorMessage = "";

    public static void main(String[] args) {
        get("/mail", (request, response) -> "Your carrier pigeon is ready for takeoff.");
        post("/mail", (request, response) -> {
            JsonNode node = JsonUtils.stringToJson(request.body());
            if (node == null) {
                response.status(400);
                errorMessage = "An error has occurred during parsing input";
                logger.error(errorMessage);
                return errorMessage;
            } else if (RegexUtils.validateEmail(node.get("email").asText()) &&
                    RegexUtils.validateEmail(node.get("to").asText()) &&
                    node.get("subject") != null && !node.get("subject").asText().isEmpty() &&
                    node.get("message") != null && !node.get("message").asText().isEmpty()) {
                // Create Mail
                Email emailToSend = new Email(
                        node.get("email").asText(),
                        node.get("to").asText(),
                        node.get("name").asText(),
                        node.get("subject").asText(),
                        node.get("message").asText()
                );

                Properties config = new Properties();
                try {
                    InputStream stream = new FileInputStream(new File("config/config.properties"));
                    config.load(stream);
                } catch (IOException ioex) {
                    logger.error(ioex);
                    ioex.printStackTrace();
                }

                // Assuming you are sending email from localhost
                String host = config.getProperty("smtp");

                Properties properties = System.getProperties();
                // Setup mail server
                properties.setProperty("mail.smtp.host", host);
                Session session = Session.getDefaultInstance(properties);

                try {
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

                } catch (MessagingException mex) {
                    logger.error(mex);
                    mex.printStackTrace();
                }
                return "Success";
            } else {
                errorMessage = "Invalid input";
                logger.error(errorMessage);
                return errorMessage;
            }
        });
    }
}