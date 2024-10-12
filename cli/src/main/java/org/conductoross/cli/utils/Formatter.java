package org.conductoross.cli.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Formatter {
    public static String printDefault(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.convertValue(object, JsonNode.class);
        StringBuilder sb = new StringBuilder();
        printJsonNode(rootNode, "", sb);
        return sb.toString();
    }

    private static void printJsonNode(JsonNode node, String indent, StringBuilder sb) {
        if (node.isObject()) {
            node.fieldNames().forEachRemaining(fieldName -> {
                System.out.print(indent + fieldName + ": ");
                printJsonNode(node.get(fieldName), indent + "  ", sb);
            });
        } else if (node.isArray()) {
            for (JsonNode element : node) {
                printJsonNode(element, indent, sb);
            }
        } else {
            System.out.println(node.asText());
        }
    }
}
