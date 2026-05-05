package be.nicholasmeyers.agent.job;

import be.nicholasmeyers.agent.properties.AgentProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TriggerJob {

    private static final Logger log = LoggerFactory.getLogger(TriggerJob.class);
    private final AgentProperties agentProperties;
    private final ChatClient chatClient;

    public TriggerJob(AgentProperties agentProperties, ChatClient chatClient) {
        this.agentProperties = agentProperties;
        this.chatClient = chatClient;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void trigger() {
        log.info("Triggering job");
        if (agentProperties.orchestrator()) {
            String task = agentProperties.task();
            String response = chatClient.prompt()
                    .user(task)
                    .call()
                    .content();
            log.info("Response: {}", response);
        }
    }
}
