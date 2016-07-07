package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * @author Patrick Jominet
 */
public class JsonUtils {

    /**
     * @param stringyfiedJson
     * @return null if string not parsable
     */
    public static JsonNode stringToJson(String stringyfiedJson) {
        try {
            return new ObjectMapper().readTree(stringyfiedJson);
        } catch (IOException ex) {
            System.out.println(ex);
            return null;
        }
    }
}