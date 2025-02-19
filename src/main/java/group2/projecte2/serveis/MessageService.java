package group2.projecte2.serveis;

import group2.projecte2.model.Message;
import group2.projecte2.repositori.mongo.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public String getMessage(String key, String locale) {
        try {
            Message message = messageRepository.findByMessageKeyAndLocale(key, locale);

            if (message != null) {
                return message.getMessage();
            } else {
                System.out.println("Mensaje no encontrado para la clave: " + key + " y locale: " + locale);
                return "Mensaje no encontrado";
            }
        } catch (Exception e) {
            System.err.println("Error al buscar el mensaje: " + e.getMessage());
            return "Error al recuperar el mensaje";
        }
    }
}