package group2.projecte2.serveis.implementacio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import group2.projecte2.model.Empleat;
import group2.projecte2.repositori.jpa.EmpleatRepositori;
import group2.projecte2.serveis.AsistenciaServei;
import group2.projecte2.serveis.EmpleatServei;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class TelegramServei extends TelegramLongPollingBot {

    @Autowired
    private EmpleatServei empleatService;

    @Autowired
    private AsistenciaServei asistenciaService;

    private final Map<String, String> userState = new HashMap<>();

    @Override
    public String getBotUsername() {
        return "NaturaMoblesBot";
    }

    @Override
    public String getBotToken() {
        return "7654190080:AAGhaX8fi1xNbDoEmDVmDOrKC-TzXB9LLKU";
    }

    @Autowired
    private EmpleatRepositori empleatRepository;

    @Override
    public void onUpdateReceived(Update update) {
        String chatId = null;

        if (update.hasMessage() && update.getMessage().hasText()) {
            chatId = update.getMessage().getChatId().toString();
            String text = update.getMessage().getText().trim();

            if (text.equals("/start")) {
                if (empleatRepository.findByChatId(chatId).isPresent()) {
                    enviarMenu(chatId);
                } else {
                    solicitarCorreo(chatId);
                }
            } else if (userState.containsKey(chatId)) {
                if ("esperandoCorreo".equals(userState.get(chatId))) {
                    manejarCorreoElectronico(chatId, text);
                } else if ("esperando_codigo".equals(userState.get(chatId))) {
                    verificarCodigo(chatId, text);
                } else {
                    enviarMensaje(chatId, "Si us plau, utilitza /start per a començar.");
                }
            } else {
                enviarMensaje(chatId, "Si us plau, utilitza /start per a registrar la teva assistència.");
            }
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId().toString();
            String data = update.getCallbackQuery().getData();

            if (data.equals("inicio") || data.equals("fin")) {
                manejarAccion(chatId, data);
            }
        }
    }

    private void verificarCodigo(String chatId, String codigoIntroducido) {
        Optional<Empleat> empleadoOpt = empleatRepository.findByChatId(chatId);

        if (empleadoOpt.isPresent()) {
            Empleat empleado = empleadoOpt.get();

            if (empleado.getCodigoVerificacion() != null
                    && empleado.getCodigoVerificacion().equals(codigoIntroducido)) {
                enviarMensaje(chatId, "Verificació completada! Sessió iniciada.");
                userState.remove(chatId);
                enviarMenu(chatId);
            } else {
                enviarMensaje(chatId, "Codi incorrecte. Torna-ho a intentar.");
            }
        } else {
            enviarMensaje(chatId,
                    "No hem trobat cap empleat associat amb aquesta sessió. Si us plau, utilitza /start.");
        }
    }

    private void enviarMenu(String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Selecciona una opció per a registrar la teva assistència:");

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(crearBoton("Iniciar assistència", "inicio"));
        row1.add(crearBoton("Finalitzar assistència", "fin"));

        rows.add(row1);
        keyboardMarkup.setKeyboard(rows);

        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void manejarAccion(String chatId, String accion) {
        Optional<Empleat> empleadoOpt = empleatRepository.findByChatId(chatId);

        if (empleadoOpt.isEmpty()) {
            enviarMensaje(chatId, "Si us plau, inicia sessió amb /start.");
            return;
        }

        Empleat empleado = empleadoOpt.get();
        if (accion.equals("inicio")) {
            asistenciaService.registrarInici(empleado);
            enviarMensaje(chatId, "Assistència iniciada correctament per a " + empleado.getEmail());
        } else if (accion.equals("fin")) {
            asistenciaService.registrarFi(empleado);
            enviarMensaje(chatId, "Assistència finalitzada correctament per a " + empleado.getEmail());
        }
        enviarMenu(chatId);
    }

    private InlineKeyboardButton crearBoton(String texto, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(texto);
        button.setCallbackData(callbackData);
        return button;
    }

    private void solicitarCorreo(String chatId) {
        enviarMensaje(chatId, "Si us plau, escriu el teu correu electrònic.");
        userState.put(chatId, "esperandoCorreo");
    }

    private void manejarCorreoElectronico(String chatId, String email) {
        if (!"esperandoCorreo".equals(userState.get(chatId))) {
            enviarMensaje(chatId, "No estem esperant un correu electrònic ara mateix.");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            enviarMensaje(chatId, "El correu electrònic no és vàlid. Intenta-ho de nou.");
            return;
        }

        Optional<Empleat> empleadoOpt = empleatRepository.findByEmail(email);
        if (empleadoOpt.isPresent()) {
            Empleat empleado = empleadoOpt.get();

            empleado.setChatId(chatId);
            empleatRepository.save(empleado);

            String codigo = empleatService.enviarCodigoVerificacion(email);
            empleado.setCodigoVerificacion(codigo);
            empleatRepository.save(empleado);

            userState.put(chatId, "esperando_codigo");
            enviarMensaje(chatId, "Hem enviat un codi de verificació al teu correu. Si us plau, introdueix el codi.");
        } else {
            enviarMensaje(chatId, "Empleat no trobat amb el correu: " + email);
        }
    }

    private void enviarMensaje(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}