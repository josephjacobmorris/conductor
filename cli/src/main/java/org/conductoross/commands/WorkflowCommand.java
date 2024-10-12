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
package org.conductoross.commands;

import com.netflix.conductor.client.http.WorkflowClient;
import com.netflix.conductor.common.run.Workflow;
import org.conductoross.utils.Formatter;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

@Command(
        command = "workflow",
        alias = "workflow",
        group = "workflow",
        description = "Commands used to get details, pause, unpause, terminate, re-run etc")
public class WorkflowCommand {
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
                    String workflowId,
            //TODO: create a custom annotation for this as it is will be reused a lot in future
            @Option( longNames = {"server-uri","uri"},
                    label = "ServerURI",
                    defaultValue = "http://localhost:5000/"
            ) String serverURI
            ) {
        // TODO check if client can be reused instead of creating for each command
        WorkflowClient client = new WorkflowClient();
        client.setRootURI(serverURI);

        // TODO add option for verbosity
        Workflow workflow = client.getWorkflow(workflowId, false);
        return Formatter.printDefault(workflow);
    }
}
