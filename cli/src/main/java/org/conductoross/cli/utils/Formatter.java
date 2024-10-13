/*
 * Copyright 2024 Conductor Authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.conductoross.cli.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Formatter {
    public static String printAsYaml(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.convertValue(object, JsonNode.class);
        StringBuilder sb = new StringBuilder();
        printJsonNode(rootNode, "", sb);
        return sb.toString();
    }

    private static void printJsonNode(JsonNode node, String indent, StringBuilder sb) {
        if (node.isObject()) {
            node.fieldNames()
                    .forEachRemaining(
                            fieldName -> {
                                sb.append(indent + fieldName + ": ");
                                printJsonNode(node.get(fieldName), indent + "  ", sb);
                            });
        } else if (node.isArray()) {

            for (JsonNode element : node) {
                printJsonNode(element, indent, sb);
            }
        } else {
            sb.append(node.asText() + System.lineSeparator());
        }
    }
}
