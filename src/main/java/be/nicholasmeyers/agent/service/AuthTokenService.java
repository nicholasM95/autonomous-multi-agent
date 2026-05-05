package be.nicholasmeyers.agent.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AuthTokenService {

    private static final Logger log = LoggerFactory.getLogger(AuthTokenService.class);
    private final RestClient restClient = RestClient.create();


    public synchronized String getToken(String tokenUrl, String clientId, String clientSecret) {
        log.info("Fetching token from {}", tokenUrl);
        return fetchToken(tokenUrl, clientId, clientSecret);
    }

    private String fetchToken(String tokenUrl, String clientId, String clientSecret) {
        TokenResponse response = restClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body("grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret)
                .retrieve()
                .body(TokenResponse.class);

        assert response != null;
        return response.accessToken();
    }

    private record TokenResponse(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("expires_in") long expiresIn
    ) {}
}
