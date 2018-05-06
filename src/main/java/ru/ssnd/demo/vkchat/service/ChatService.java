package ru.ssnd.demo.vkchat.service;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import ru.ssnd.demo.vkchat.entity.Message;
import ru.ssnd.demo.vkchat.repository.MessagesRepository;

@Service
@PropertySource("classpath:vkAuth.properties")
public class ChatService {

    private final MessagesRepository messages;
    private final VkApiClient vk;
    private Long groupId;
    private String communityAccessToken;

    @Autowired
    public ChatService(MessagesRepository messages, Environment env) {
        this.messages = messages;
        this.vk = new VkApiClient(new HttpTransportClient());
        this.groupId = Long.parseLong(env.getRequiredProperty("groupId"));
        this.communityAccessToken = env.getRequiredProperty("communityAccessToken");
    }

    public Long getCommunityId() {
        return this.groupId;
    }

    // TODO Get, send & store messages

    public Message send(Long interlocutorId, String message) {
        throw new UnsupportedOperationException();

    }

    public Message[] poll(Long id, Long interlocutorId) {
        throw new UnsupportedOperationException();

    }
}
