package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
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
        } catch (IOException ioex) {
            ioex.printStackTrace();
            return null;
        }
    }

    /**
     * @param jsonFile
     * @return null if string not parsable
     */
    public static JsonNode jsonFromFile(File jsonFile) {
        try {
            return new ObjectMapper().readTree(jsonFile);
        } catch (IOException ioex) {
            ioex.printStackTrace();
            return null;
        }
    }
}