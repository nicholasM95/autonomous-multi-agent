package be.nicholasmeyers.agent.config;

import be.nicholasmeyers.agent.properties.McpAuthType;
import be.nicholasmeyers.agent.properties.McpClientsProperties;
import be.nicholasmeyers.agent.service.AuthTokenService;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import org.springframework.ai.mcp.client.common.autoconfigure.NamedClientMcpTransport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class McpClientConfig {

    @Bean
    public List<NamedClientMcpTransport> mcpTransports(McpClientsProperties mcpClientsProperties, AuthTokenService tokenService) {
        if (mcpClientsProperties.clients() == null) {
            return List.of();
        }
        return mcpClientsProperties.clients().stream()
                .map(client -> {
                    var builder = HttpClientStreamableHttpTransport.builder(client.url());
                    if (McpAuthType.OAUTH2.equals(client.authType())) {
                        builder.httpRequestCustomizer((requestBuilder, method, uri, body, context) ->
                                requestBuilder.header("Authorization", "Bearer " +
                                        tokenService.getToken(
                                                client.oauth().tokenUrl(),
                                                client.oauth().clientId(),
                                                client.oauth().clientSecret())));
                    }
                    if (McpAuthType.API_KEY.equals(client.authType())) {
                        builder.httpRequestCustomizer((requestBuilder, method, uri, body, context) ->
                                requestBuilder.header("Authorization", "Bearer " + client.apiKey()));
                    }
                    return new NamedClientMcpTransport(client.name(), builder.build());
                })
                .toList();
    }
}
