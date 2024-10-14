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
package org.conductoross.cli.commands;

import java.io.File;
import java.io.IOException;

import org.conductoross.cli.formatters.Formatter;
import org.conductoross.cli.formatters.FormatterConfig;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

import com.netflix.conductor.client.http.MetadataClient;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;

import com.fasterxml.jackson.databind.ObjectMapper;

@Command(
        command = "metadata",
        group = "metadata",
        description = "Commands used to define, update, delete workflow/task definitions")
public class MetadataCommand {
    private final Formatter formatter;
    private final MetadataClient metadataClient;
    private final ObjectMapper objectMapper;

    public MetadataCommand(Formatter formatter, ObjectMapper objectMapper) {
        this.formatter = formatter;
        this.objectMapper = objectMapper;
        this.metadataClient = new MetadataClient();
    }

    @Command(
            command = "delete-workflow",
            alias = {"remove"},
            description = "Delete a workflow definition")
    public String deleteWorkflow(
            @Option(
                            longNames = {"name", "workflowName"},
                            shortNames = {'n', 'N'},
                            required = true,
                            description = "Name of the workflow",
                            label = "WorkflowName")
                    String workflowName,
            @Option(
                            longNames = {"version"},
                            shortNames = {'v'},
                            label = "Version",
                            required = true)
                    int version,
            @Option(
                            longNames = {"server-uri", "uri"},
                            label = "ServerURI",
                            defaultValue = "http://localhost:8080/")
                    String serverURI) {
        metadataClient.setRootURI(serverURI);
        metadataClient.unregisterWorkflowDef(workflowName, version);
        return "WorkflowDefinition for " + workflowName + "." + version + "removed";
    }

    @Command(
            command = "get-workflow-definition",
            alias = {"see-workflow"},
            description = "See a workflow definition")
    public String getWorkflowDefinition(
            @Option(
                            longNames = {"name", "workflowName"},
                            shortNames = {'n', 'N'},
                            required = true,
                            description = "Name of the workflow",
                            label = "WorkflowName")
                    String workflowName,
            @Option(
                            longNames = {"version"},
                            shortNames = {'v'},
                            label = "Version",
                            required = true)
                    int version,
            @Option(
                            longNames = {"server-uri", "uri"},
                            label = "ServerURI",
                            defaultValue = "http://localhost:8080/")
                    String serverURI,
            @Option(
                            longNames = {"ignore-nulls"},
                            label = "IgnoreNulls",
                            defaultValue = "true")
                    boolean ignoreNulls) {
        metadataClient.setRootURI(serverURI);
        WorkflowDef workflowDef = metadataClient.getWorkflowDef(workflowName, version);
        FormatterConfig config = FormatterConfig.builder().ignoreNulls(ignoreNulls).build();
        return formatter.print(workflowDef, config);
    }

    @Command(
            command = "create-workflow-definition",
            alias = {"define-workflow"},
            description = "Create a workflow definition")
    public String defineWorkflowDefinition(
            @Option(
                            longNames = {"name", "workflowName"},
                            shortNames = {'n', 'N'},
                            required = true,
                            description = "Name of the workflow",
                            label = "WorkflowName")
                    String workflowName,
            @Option(
                            longNames = {"version"},
                            shortNames = {'v'},
                            label = "Version",
                            required = true)
                    int version,
            @Option(
                            longNames = {"file-name"},
                            shortNames = {'f', 'F'},
                            label = "FileName",
                            description =
                                    "The path of the json file containing workflow definition",
                            required = true)
                    String filePath,
            @Option(
                            longNames = {"server-uri", "uri"},
                            label = "ServerURI",
                            defaultValue = "http://localhost:8080/")
                    String serverURI) {
        metadataClient.setRootURI(serverURI);
        WorkflowDef workflowDef = null;
        try {
            workflowDef = objectMapper.readerFor(WorkflowDef.class).readValue(new File(filePath));
        } catch (IOException e) {
            throw new RuntimeException("Error reading workflow definition from " + filePath, e);
        }

        metadataClient.registerWorkflowDef(workflowDef);
        return "Workflow definition created/updated for "
                + workflowDef.getName()
                + " version : "
                + workflowDef.getVersion();
    }
}
