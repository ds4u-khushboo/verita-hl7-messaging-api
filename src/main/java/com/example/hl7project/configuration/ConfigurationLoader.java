package com.example.hl7project.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ConfigurationLoader {

    private static final String CONFIG_FILE = "config.properties";
    private static final Map<String, Map<String, Object>> messageConfig = new HashMap<>();

    static {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(new File(CONFIG_FILE));
            Iterator<Map.Entry<String, JsonNode>> fields = root.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String messageType = field.getKey();
                JsonNode config = field.getValue();
                Map<String, Object> segments = new HashMap<>();
                config.get("segments").forEach(segment -> segments.put(segment.asText(), null));
                messageConfig.put(messageType, segments);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public static HL7MessageHandler getHandler(String messageType) throws Exception {
//        Class<? extends HL7MessageHandler> handlerClass = handlers.get(messageType);
//        if (handlerClass != null) {
//            Constructor<? extends HL7MessageHandler> constructor = handlerClass.getConstructor();
//            return constructor.newInstance();
//        }
//        throw new Exception("No handler found for message type: " + messageType);
//    }
//
//    public static Map<String, Object> getMessageConfig(String messageType) {
//        return messageConfig.get(messageType);
//    }
//
//    private static void loadConfiguration() {
//        try (InputStream input = HandlerRegistry.class.getClassLoader().getResourceAsStream("config.properties")) {
//            Properties properties = new Properties();
//            properties.load(input);
//
//            for (String key : properties.stringPropertyNames()) {
//                String className = properties.getProperty(key);
//                try {
//                    Class<? extends HL7MessageHandler> handlerClass = (Class<? extends HL7MessageHandler>) Class.forName(className);
//                    handlerMap.put(key, handlerClass);
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
}
