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

import org.conductoross.cli.utils.Formatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.command.CommandHandlingResult;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.ExceptionResolver;
import org.springframework.shell.command.annotation.Option;

import com.netflix.conductor.client.exception.ConductorClientException;
import com.netflix.conductor.client.http.WorkflowClient;
import com.netflix.conductor.common.run.Workflow;

import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;

@Command(
        command = "workflow",
        alias = "workflow",
        group = "workflow",
        description = "Commands used to get details, pause, unpause, terminate, re-run etc")
@Slf4j
public class WorkflowCommand {
    private static final Logger log = LoggerFactory.getLogger(WorkflowCommand.class);

    @Command(
            command = "get-workflow-execution",
            alias = {"get-execution", "details"},
            description = "Used to get details of a particular workflow execution")
    public String getWorkflowExecution(
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
                    String serverURI,
            @Option(
                            longNames = {"ignore-nulls"},
                            label = "IgnoreNulls",
                            defaultValue = "true")
                    boolean ignoreNulls) {
        // TODO check if client can be reused instead of creating for each command
        WorkflowClient client = new WorkflowClient();
        client.setRootURI(serverURI);
        // TODO add option for verbosity and let it decide to include tasks or not
        Workflow workflow = client.getWorkflow(workflowId, false);
        return Formatter.printAsYaml(workflow, ignoreNulls);
    }

    @ExceptionResolver
    CommandHandlingResult conductorClientException(ConductorClientException ex) {
        return CommandHandlingResult.of(
                """
    Error while executing command: %s
    Suggestions:
    Check if the provided uri is valid and the conductor server is running
    """
                        .formatted(ex.getMessage()),
                4);
    }
}
