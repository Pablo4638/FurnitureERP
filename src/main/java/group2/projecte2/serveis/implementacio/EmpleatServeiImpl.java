package group2.projecte2.serveis.implementacio;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import group2.projecte2.model.Empleat;
import group2.projecte2.repositori.jpa.EmpleatRepositori;
import group2.projecte2.serveis.EmpleatServei;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;

@Service
public class EmpleatServeiImpl implements EmpleatServei {
    @Autowired
    EmpleatRepositori empleatRepositori;

    public List<Empleat> obtenirTots() {
        return empleatRepositori.findAll();
    }

    @Override
    public void guardar(Empleat empleat) {
        if (empleat.getId_empleat() != null) {
            Empleat empleatExistente = empleatRepositori.findById(empleat.getId_empleat()).orElse(null);
            if (empleatExistente != null && !empleatExistente.getEmail().equals(empleat.getEmail()) &&
                    empleatRepositori.existsByEmail(empleat.getEmail())) {
                throw new IllegalArgumentException("El correo electrónico ya está en uso.");
            }
        } else {
            if (empleatRepositori.existsByEmail(empleat.getEmail())) {
                throw new IllegalArgumentException("El correo electrónico ya está en uso.");
            }
        }
        empleatRepositori.save(empleat);
    }

    @Override
    public Optional<Empleat> obtenirPerId(Long id) {
        return empleatRepositori.findById(id);
    }

    @Override
    public void eliminar(Long id) {
        empleatRepositori.deleteById(id);
    }

    @Override
    public Long recompte() {
        return empleatRepositori.count();
    }

    public Empleat obtenerEmpleadoPorCorreo(String email) {
        return empleatRepositori.findByemail(email);
    }

    @Override
    public String enviarCodigoVerificacion(String correu) {
        final String username = "naturamobles@gmail.com";
        final String password = "kgtx iejz yadw seoa";

        String codigo = String.format("%06d", new Random().nextInt(999999));

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new jakarta.mail.Authenticator() {
                    @Override
                    protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
                        return new jakarta.mail.PasswordAuthentication(username, password);
                    }
                });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correu));
            message.setSubject("Codi de Verificació");
            message.setContent(
                    "Hola,<br>" +
                            "El teu codi de verificació per Natura Mobles és:<br>" +
                            "<h2><b>" + codigo + "</b></h2><br>" +
                            "Aquest codi caducarà en uns minuts. Si no has sol·licitat aquest correu, ignora’l.",
                    "text/html; charset=utf-8");
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return codigo;
    }

    public List<Empleat> filtrarYOrdenar(String filtro, String valor, String orden) {
        // Map de comparadores basado en los campos del filtro
        Map<String, Comparator<Empleat>> comparadores = new HashMap<>();

        comparadores.put("nom", Comparator.comparing(
                Empleat::getNom,
                Comparator.nullsFirst(String::compareTo)));

        comparadores.put("email", Comparator.comparing(
                Empleat::getEmail,
                Comparator.nullsFirst(String::compareTo)));

        comparadores.put("telefon", Comparator.comparing(
                Empleat::getTelefon,
                Comparator.nullsFirst(String::compareTo)));

        comparadores.put("salari", Comparator.comparing(
                Empleat::getSalari,
                Comparator.nullsFirst(BigDecimal::compareTo)));

        comparadores.put("datacontractacio", Comparator.comparing(
                Empleat::getData_contractacio,
                Comparator.nullsFirst(Date::compareTo)));

        comparadores.put("departament", Comparator.comparing(
                Empleat::getDepartament,
                Comparator.nullsFirst(Comparator.naturalOrder())));

        // Obtén el comparador correspondiente o un valor predeterminado
        Comparator<Empleat> comparador = comparadores.getOrDefault(
                filtro != null ? filtro.toLowerCase() : "",
                Comparator.comparing(
                        Empleat::getNom,
                        Comparator.nullsFirst(String::compareTo)));

        // Invierte el orden si es "desc"
        if ("desc".equalsIgnoreCase(orden)) {
            comparador = comparador.reversed();
        }

        // Filtra por el estado "pendent" y aplica el filtro adicional solo en el campo
        // seleccionado
        return empleatRepositori.findAll().stream()
                .filter(e -> {
                    // Si el filtro o valor es null, no se filtra por campo específico
                    if (filtro == null || valor == null || valor.isEmpty()) {
                        return true; // No filtra si no hay filtro o valor
                    }

                    // Filtra solo en el campo que coincide con el filtro especificado
                    switch (filtro.toLowerCase()) {
                        case "nom":
                            return e.getNom().toString().toLowerCase().contains(valor.toLowerCase());
                        case "email":
                            return e.getEmail().toString().toLowerCase().contains(valor.toLowerCase());
                        case "telefon":
                            return e.getTelefon().toString().toLowerCase().contains(valor.toLowerCase());
                        case "salari":
                            return e.getSalari().toString().toLowerCase().contains(valor.toLowerCase());
                        case "datacontractacio":
                            return e.getData_contractacio().toString().toLowerCase().contains(valor.toLowerCase());
                        case "departament":
                            return e.getDepartament().toString().toLowerCase().contains(valor.toLowerCase());
                        default:
                            return true; // Si no hay coincidencia en el filtro, no aplica el filtro
                    }
                })
                .sorted(comparador) // Ordena según el comparador seleccionado
                .collect(Collectors.toList()); // Recoge el resultado como lista
    }
}
