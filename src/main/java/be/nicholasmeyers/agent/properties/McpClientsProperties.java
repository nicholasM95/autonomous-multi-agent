package be.nicholasmeyers.agent.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "mcp")
public record McpClientsProperties(List<McpClientProperties> clients) {
}
