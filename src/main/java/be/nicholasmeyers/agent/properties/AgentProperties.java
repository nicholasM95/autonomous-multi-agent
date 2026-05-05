package be.nicholasmeyers.agent.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "agent")
public record AgentProperties(String name, String description, String url, String version, String prompt, boolean orchestrator,
                              String task, List<AgentSkillProperties> skills, List<String> subAgents) {
}
