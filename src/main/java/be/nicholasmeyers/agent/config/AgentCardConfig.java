package be.nicholasmeyers.agent.config;

import be.nicholasmeyers.agent.properties.AgentProperties;
import io.a2a.server.agentexecution.AgentExecutor;
import io.a2a.spec.AgentCapabilities;
import io.a2a.spec.AgentCard;
import io.a2a.spec.AgentSkill;
import org.springaicommunity.a2a.server.executor.DefaultAgentExecutor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AgentCardConfig {

    @Bean
    public AgentExecutor agentExecutor(ChatClient chatClient) {
        return new DefaultAgentExecutor(chatClient, (chat, ctx) -> {
            String userMessage =
                    DefaultAgentExecutor.extractTextFromMessage(ctx.getMessage());
            return chat.prompt().user(userMessage).call().content();
        });
    }

    @Bean
    public AgentCard agentCard(AgentProperties agentProperties) {
        return new AgentCard.Builder()
                .name(agentProperties.name())
                .description(agentProperties.description())
                .url(agentProperties.url())
                .version(agentProperties.version())
                .capabilities(new AgentCapabilities.Builder().streaming(false).build())
                .defaultInputModes(List.of("text"))
                .defaultOutputModes(List.of("text"))
                .skills(createSkills(agentProperties))
                .protocolVersion("0.3.0")
                .build();
    }

    private List<AgentSkill> createSkills(AgentProperties agentProperties) {
        return agentProperties.skills().stream().map(skill -> new AgentSkill.Builder()
                        .id(skill.id())
                        .name(skill.name())
                        .description(skill.description())
                        .tags(skill.tags())
                        .build())
                .toList();
    }
}
