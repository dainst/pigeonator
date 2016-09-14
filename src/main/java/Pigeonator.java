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
            JsonNode requestBody = JsonUtils.stringToJson(request.body());
            if (requestBody == null) {
                response.status(400);
                errorMessage = "An error has occurred during parsing input";
                logger.error(errorMessage);
                return errorMessage;
            } else if (RegexUtils.validateEmail(requestBody.get("email").asText()) &&
                    requestBody.get("to") != null && !requestBody.get("to").asText().isEmpty() &&
                    requestBody.get("subject") != null && !requestBody.get("subject").asText().isEmpty() &&
                    requestBody.get("message") != null && !requestBody.get("message").asText().isEmpty()) {

                // Create Mail
                Email emailToSend = new Email(
                        requestBody.get("email").asText(),
                        requestBody.get("to").asText(),
                        requestBody.get("name").asText(),
                        requestBody.get("subject").asText(),
                        requestBody.get("message").asText()
                );

                // Get adequate recipient from identifier
                try {
                    JsonNode mailAddresses = JsonUtils.jsonFromFile(new File("config/mailingList.json"));
                    if(mailAddresses != null) {
                        JsonNode toAddress = mailAddresses.get(emailToSend.getRecipient());
                        if (toAddress != null) {
                            emailToSend.setRecipient(toAddress.asText());
                        } else {
                            errorMessage = "Recipient cannot be found";
                            logger.error(errorMessage);
                            response.status(500);
                            return errorMessage;
                        }
                    } else {
                        errorMessage = "No mailing address list found";
                        logger.error(errorMessage);
                        response.status(500);
                        return errorMessage;
                    }
                } catch (NullPointerException npex) {
                    logger.error(npex);
                    npex.printStackTrace();
                }

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
                            new InternetAddress(emailToSend.getSender())
                    );
                    message.addRecipient(
                            Message.RecipientType.TO,
                            new InternetAddress(emailToSend.getRecipient())
                    );
                    message.setSubject(emailToSend.getSubject());
                    message.setText(
                            "From: " + emailToSend.getName() + "\n" +
                                    emailToSend.getMessage()
                    );

                    // Send message
                    Transport.send(message);

                } catch (MessagingException mex) {
                    logger.error(mex);
                    mex.printStackTrace();
                }
                response.status(200);
                return "Success";
            } else {
                errorMessage = "Invalid input";
                logger.error(errorMessage);
                response.status(400);
                return errorMessage;
            }
        });
    }
}