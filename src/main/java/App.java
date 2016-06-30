import com.fasterxml.jackson.databind.JsonNode;
import utils.JsonUtils;

import static spark.Spark.get;
import static spark.Spark.post;

/**
 * @author Patrick Jominet
 */
public class App {

    public static void main(String[] args) {
        get("/", (request, response) -> "Hello Spark");
        post("/mail", (request, response) -> {
            JsonNode node = JsonUtils.stringToJsonNode(request.body());
            if (node != null) {
                System.out.println(node.toString());
            } else {
                response.status(400);
                return "An error has occurred";
            }
            return "Success";
        });
    }

}

