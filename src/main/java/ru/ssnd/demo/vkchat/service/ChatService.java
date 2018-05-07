package ru.ssnd.demo.vkchat.service;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.groups.GroupFull;
import com.vk.api.sdk.objects.messages.LongpollParams;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import com.vk.api.sdk.queries.groups.GroupField;
import com.vk.api.sdk.queries.users.UserField;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import ru.ssnd.demo.vkchat.entity.Sender;
import ru.ssnd.demo.vkchat.entity.VKMessage;
import ru.ssnd.demo.vkchat.repository.MessagesRepository;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@PropertySource("classpath:vkAuth.properties")
public class ChatService {

    private final MessagesRepository messages;
    private final VkApiClient vk;
    private final UserActor actor;
    private Sender in;

    @Autowired
    public ChatService(MessagesRepository messages, Environment env) {
        this.messages = messages;
        this.vk = new VkApiClient(new HttpTransportClient());
        this.actor = new UserActor(
                Integer.parseInt(env.getRequiredProperty("groupId")),
                env.getRequiredProperty("communityAccessToken"));
        try {
            GroupFull gf = this.vk.groups().getById(this.actor).fields(GroupField.PHOTO_200).execute().get(0);
            this.in = new Sender(getCommunityId(), gf.getPhoto200(), gf.getName());
        } catch (ClientException | ApiException e) {
            e.printStackTrace();
        }
    }

    public Long getCommunityId() {
        return this.actor.getId().longValue();
    }

    public Boolean send(Long interlocutorId, String message) {
        try {
            this.vk.messages().send(this.actor).userId(interlocutorId.intValue()).message(message).execute();
            return true;
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<VKMessage> poll(Long interlocutorId, Long id) {
        List<VKMessage> vkMessages = new ArrayList<>();
        List<Message> messages;
        Sender out;
        try {
            boolean repeat = false;
            do {
                UserXtrCounters ux = this.vk.users().get(this.actor).userIds(interlocutorId.toString())
                        .fields(UserField.PHOTO_MAX).execute().get(0);
                out = new Sender(interlocutorId, ux.getPhotoMax(), String.format("%s %s", ux.getFirstName(), ux.getLastName()));
                messages = this.vk.messages().getHistory(this.actor)
                        .userId(interlocutorId.intValue()).startMessageId(id.intValue())
                        .offset(-20).execute().getItems();
                if (messages.size() == 0) {
                    LongpollParams lp = this.vk.messages().getLongPollServer(this.actor).lpVersion(3).needPts(true).execute();
                    String query = String.format("https://%s?act=a_check&key=%s&ts=%s&wait=10&mode=34&version=3",
                            lp.getServer(), lp.getKey(), lp.getTs());
                    JSONObject response = new JSONObject(Jsoup.connect(query).ignoreContentType(true).execute().body());
                    repeat = response.getJSONArray("updates").length() != 0;
                }
            } while (repeat);
            for (Message message : messages) {
                vkMessages.add(new VKMessage(message.getId().longValue(), message.getBody(), Instant.ofEpochSecond(message.getDate()),
                        message.isOut() ? this.in : out));
            }
            vkMessages.sort(Comparator.comparingLong(VKMessage::getId));
            this.messages.save(vkMessages);
        } catch (ApiException | ClientException | IOException e) {
            e.printStackTrace();
        }
        return vkMessages;
    }
}
