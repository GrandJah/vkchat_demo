package ru.ssnd.demo.vkchat.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.async.DeferredResult;
import ru.ssnd.demo.vkchat.http.Response;
import ru.ssnd.demo.vkchat.service.ChatService;

@Controller
@RequestMapping(value = "/api/chat")
public class ChatController {

    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("{interlocutorId}/poll")
    public DeferredResult<Response> poll(@PathVariable Long interlocutorId, @RequestParam(required = false) Long lastId) {
        DeferredResult<Response> result = new DeferredResult<>();
        new Thread(() -> result.setResult(new Response.Builder()
                .withField("messages", new JSONArray(chatService.poll(interlocutorId, lastId)))
                .build())).start();
        return result;
    }

    @PostMapping("{interlocutorId}/send")
    public Response send(@PathVariable Long interlocutorId, @RequestBody String message) {
        return new Response.Builder()
                .withField("message", new JSONObject(chatService.send(interlocutorId, message)))
                .build();
    }

}