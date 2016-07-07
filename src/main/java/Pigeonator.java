import com.fasterxml.jackson.databind.JsonNode;
import handler.Email;
import utils.JsonUtils;

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
            if (node != null) {
                System.out.println(node.toString());

                Email emailToSend = new Email(
                        node.get("email").asText(),
                        node.get("to").asText(),
                        node.get("name").asText(),
                        node.get("subject").asText(),
                        node.get("message").asText()
                );
                System.out.println(emailToSend.toString());
            } else {
                response.status(400);
                return "An error has occurred, check input";
            }
            return "Success";
        });
    }
}