package be.nicholasmeyers.agent.config;

import be.nicholasmeyers.agent.properties.AgentProperties;
import org.springaicommunity.agent.common.task.subagent.SubagentReference;
import org.springaicommunity.agent.common.task.subagent.SubagentType;
import org.springaicommunity.agent.subagent.a2a.A2ASubagentDefinition;
import org.springaicommunity.agent.subagent.a2a.A2ASubagentExecutor;
import org.springaicommunity.agent.subagent.a2a.A2ASubagentResolver;
import org.springaicommunity.agent.tools.task.TaskTool;
import org.springframework.ai.chat.client.ChatClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SubAgentConfig {

    @Bean
    ChatClientCustomizer addTaskTool(AgentProperties agentProperties) {
        return builder -> {
            if (agentProperties.subAgents() == null || agentProperties.subAgents().isEmpty()) {
                return;
            }
            List<SubagentReference> references = agentProperties.subAgents().stream()
                    .map(url -> new SubagentReference(url, A2ASubagentDefinition.KIND))
                    .toList();

            var taskTool = TaskTool.builder()
                    .subagentReferences(references.toArray(new SubagentReference[0]))
                    .subagentTypes(
                            new SubagentType(
                                    new A2ASubagentResolver(), new A2ASubagentExecutor()))
                    .build();
            builder.defaultToolCallbacks(taskTool);
        };
    }
}
