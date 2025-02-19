package group2.projecte2.repositori.mongo;

import group2.projecte2.model.Message;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findByLocale(String locale);

    // Cambiar esta firma de método para usar el campo correcto
    @Query("{ 'message_key': ?0, 'locale': ?1 }")
    Message findByMessageKeyAndLocale(String messageKey, String locale);
}
