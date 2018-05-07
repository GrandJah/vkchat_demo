package ru.ssnd.demo.vkchat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.ssnd.demo.vkchat.entity.VKMessage;

public interface MessagesRepository extends MongoRepository<VKMessage, String> {

}
