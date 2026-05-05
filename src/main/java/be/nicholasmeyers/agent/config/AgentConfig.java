package be.nicholasmeyers.agent.config;

import be.nicholasmeyers.agent.properties.AgentProperties;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentConfig {

    @Bean
    ChatClient chatClient(ChatClient.Builder builder, AgentProperties agentProperties, ToolCallbackProvider toolCallbackProvider) {
        return builder
                .defaultSystem(agentProperties.prompt())
                .defaultToolCallbacks(toolCallbackProvider)
                .build();
    }
}
