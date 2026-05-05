package be.nicholasmeyers.agent.properties;

import java.util.List;

public record AgentSkillProperties(String id, String name, String description, List<String> tags) {
}
