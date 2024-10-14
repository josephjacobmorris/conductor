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
import org.springframework.shell.command.CommandHandlingResult;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.ExceptionResolver;
import org.springframework.shell.command.annotation.Option;

import com.netflix.conductor.client.exception.ConductorClientException;
import com.netflix.conductor.client.http.WorkflowClient;
import com.netflix.conductor.common.metadata.workflow.RerunWorkflowRequest;
import com.netflix.conductor.common.metadata.workflow.StartWorkflowRequest;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.run.Workflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;

@Command(
        command = "workflow",
        alias = "workflow",
        group = "workflow",
        description =
                "Commands used to get details, pause, unpause, terminate, re-run,start etc a workflow execution")
@Slf4j
public class WorkflowCommand {
    private final Formatter formatter;
    private final WorkflowClient workflowClient;
    private final ObjectMapper objectMapper;

    public WorkflowCommand(Formatter formatter, ObjectMapper objectMapper) {
        this.formatter = formatter;
        this.objectMapper = objectMapper;
        this.workflowClient = new WorkflowClient();
    }

    @Command(
            command = "get-workflow-execution",
            alias = {"get-execution", "details"},
            description = "Used to get details of a particular workflow execution")
    public String getWorkflowExecution(
            @Option(
                            longNames = {"workflow-id", "id"},
                            shortNames = {'K'},
                            label = "workflowId",
                            required = true)
                    @Pattern(
                            regexp =
                                    "^[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[4][0-9A-Fa-f]{3}-[89AB][0-9A-Fa-f]{3}-[0-9A-Fa-f]{12}$",
                            message = "workflowId must be in uuid format")
                    String workflowId,
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
        WorkflowClient client = new WorkflowClient();
        client.setRootURI(serverURI);
        // TODO add option for verbosity and let it decide to include tasks or not
        Workflow workflow = client.getWorkflow(workflowId, false);
        FormatterConfig formatterConfig = FormatterConfig.builder().ignoreNulls(true).build();
        return formatter.print(workflow, formatterConfig);
    }

    @Command(command = "pause", description = "Used to pause a running workflow")
    public String pauseWorkflowExecution(
            // TODO: create a custom annotation for this as it is will be reused a lot in future
            @Option(
                            longNames = {"workflow-id", "id"},
                            shortNames = {'K'},
                            label = "workflowId",
                            required = true)
                    @Pattern(
                            regexp =
                                    "^[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[4][0-9A-Fa-f]{3}-[89AB][0-9A-Fa-f]{3}-[0-9A-Fa-f]{12}$",
                            message = "workflowId must be in uuid format")
                    String workflowId,
            @Option(
                            longNames = {"server-uri", "uri"},
                            label = "ServerURI",
                            defaultValue = "http://localhost:8080/")
                    String serverURI) {
        WorkflowClient client = new WorkflowClient();
        client.setRootURI(serverURI);
        client.pauseWorkflow(workflowId);
        return "Workflow pause triggered for workflowId : " + workflowId;
    }

    @Command(
            command = "unpause",
            alias = "resume",
            description = "Used to resume a paused workflow")
    public String resumeWorkflowExecution(
            @Option(
                            longNames = {"workflow-id", "id"},
                            shortNames = {'K'},
                            label = "workflowId",
                            required = true)
                    @Pattern(
                            regexp =
                                    "^[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[4][0-9A-Fa-f]{3}-[89AB][0-9A-Fa-f]{3}-[0-9A-Fa-f]{12}$",
                            message = "workflowId must be in uuid format")
                    String workflowId,
            @Option(
                            longNames = {"server-uri", "uri"},
                            label = "ServerURI",
                            defaultValue = "http://localhost:8080/")
                    String serverURI) {
        WorkflowClient client = new WorkflowClient();
        client.setRootURI(serverURI);
        client.resumeWorkflow(workflowId);
        return "Workflow resume triggered for workflowId : " + workflowId;
    }

    @Command(
            command = "terminate",
            alias = "stop",
            description = "Used to terminate a running workflow")
    public String terminateWorkflowExecution(
            @Option(
                            longNames = {"workflow-id", "id"},
                            shortNames = {'K'},
                            label = "workflowId",
                            required = true)
                    @Pattern(
                            regexp =
                                    "^[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[4][0-9A-Fa-f]{3}-[89AB][0-9A-Fa-f]{3}-[0-9A-Fa-f]{12}$",
                            message = "workflowId must be in uuid format")
                    String workflowId,
            @Option(
                            longNames = {"server-uri", "uri"},
                            label = "ServerURI",
                            defaultValue = "http://localhost:8080/")
                    String serverURI,
            @Option(
                            longNames = {"server-uri", "uri"},
                            label = "Reason",
                            description =
                                    "Describes the reason for terminating for future auditing",
                            defaultValue = "")
                    String reason) {
        WorkflowClient client = new WorkflowClient();
        client.setRootURI(serverURI);
        client.terminateWorkflow(workflowId, reason);
        return "Workflow terminate triggered for workflowId : " + workflowId;
    }

    @Command(command = "rerun", description = "Used to rerun a terminated/finished workflow")
    public String rerunWorkflowExecution(
            @Option(
                            longNames = {"workflow-id", "id"},
                            shortNames = {'K'},
                            label = "workflowId",
                            required = true)
                    @Pattern(
                            regexp =
                                    "^[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[4][0-9A-Fa-f]{3}-[89AB][0-9A-Fa-f]{3}-[0-9A-Fa-f]{12}$",
                            message = "workflowId must be in uuid format")
                    String workflowId,
            @Option(
                            longNames = {"server-uri", "uri"},
                            label = "ServerURI",
                            defaultValue = "http://localhost:8080/")
                    String serverURI) {
        WorkflowClient client = new WorkflowClient();
        RerunWorkflowRequest rerunWorkflowRequest = new RerunWorkflowRequest();
        rerunWorkflowRequest.setReRunFromWorkflowId(workflowId);
        client.setRootURI(serverURI);
        client.rerunWorkflow(workflowId, rerunWorkflowRequest);
        return "Workflow rerun triggered for workflowId : " + workflowId;
    }

    @Command(command = "retry", description = "Used to retry from last failed task in  workflow")
    public String retryWorkflowExecution(
            // TODO: create a custom annotation for this as it is will be reused a lot in future
            @Option(
                            longNames = {"workflow-id", "id"},
                            shortNames = {'K'},
                            label = "workflowId",
                            required = true)
                    @Pattern(
                            regexp =
                                    "^[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[4][0-9A-Fa-f]{3}-[89AB][0-9A-Fa-f]{3}-[0-9A-Fa-f]{12}$",
                            message = "workflowId must be in uuid format")
                    String workflowId,
            // TODO: create a custom annotation for this as it is will be reused a lot in future
            @Option(
                            longNames = {"server-uri", "uri"},
                            label = "ServerURI",
                            defaultValue = "http://localhost:8080/")
                    String serverURI) {
        WorkflowClient client = new WorkflowClient();
        client.setRootURI(serverURI);
        client.retryLastFailedTask(workflowId);
        return "Workflow retry triggered for workflowId : " + workflowId;
    }

    @Command(
            command = "start-workflow",
            alias = {"start"},
            description = "Start a workflow execution")
    public String startWorkflowExecution(
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
                            longNames = {"correlation-id", "cid"},
                            label = "CorrelationId")
                    String correlationId,
            @Option(
                            longNames = {"file-name"},
                            shortNames = {'f', 'F'},
                            label = "FileName",
                            description =
                                    "The path of the json file containing workflow definition")
                    String filePath,
            @Option(
                            longNames = {"server-uri", "uri"},
                            label = "ServerURI",
                            defaultValue = "http://localhost:8080/")
                    String serverURI) {
        workflowClient.setRootURI(serverURI);
        WorkflowDef workflowDef = null;
        try {
            if (filePath != null) {
                workflowDef =
                        objectMapper.readerFor(WorkflowDef.class).readValue(new File(filePath));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading workflow definition from " + filePath, e);
        }
        StartWorkflowRequest startWorkflowRequest = new StartWorkflowRequest();
        startWorkflowRequest.setName(workflowName);
        startWorkflowRequest.setVersion(version);
        startWorkflowRequest.setCorrelationId(correlationId);
        startWorkflowRequest.setWorkflowDef(workflowDef);

        workflowClient.startWorkflow(startWorkflowRequest);
        return "Workflow started for " + workflowName + " version : " + version;
    }

    @ExceptionResolver
    CommandHandlingResult conductorClientException(ConductorClientException ex) {
        return CommandHandlingResult.of(
                """
    Error while executing command: %s
    Suggestions:
    1. Check if the provided uri is valid and the conductor server is running
    """
                        .formatted(ex.getMessage()),
                4);
    }
}
