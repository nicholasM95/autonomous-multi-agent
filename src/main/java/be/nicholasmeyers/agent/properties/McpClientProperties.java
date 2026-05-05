package be.nicholasmeyers.agent.properties;

public record McpClientProperties(String name, String url, McpAuthType authType, OAuthProperties oauth, String apiKey) {
}
