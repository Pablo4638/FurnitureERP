package group2.projecte2.serveis.implementacio;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import group2.projecte2.model.User;
import group2.projecte2.model.Enums.Role;
import group2.projecte2.repositori.jpa.UserRepository;
import group2.projecte2.serveis.UsuariServei;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class UsuariServeiImpl implements UsuariServei {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> obtenirTots() {
        return userRepository.findAll();
    }

    @Override
    public void guardar(User user) {
        userRepository.save(user);
    }

    @Override
    public void guardarCrear(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }
        userRepository.save(user);
    }

    @Override
    public Optional<User> obtenirPerId(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public void eliminar(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public String crearContrasenya() {
        String mayusculas = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String minusculas = "abcdefghijklmnopqrstuvwxyz";
        String numeros = "0123456789";
        String conjunto = mayusculas + minusculas + numeros;
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 15; i++) {
            int indice = random.nextInt(conjunto.length());
            password.append(conjunto.charAt(indice));
        }
        return password.toString();
    }

    @Override
    public void enviarMissatge(String usuari, String correu, String contrasenya) {
        final String username = "naturamobles@gmail.com";
        final String password = "kgtx iejz yadw seoa";
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
            message.setSubject("Usuari donat d'alta");
            message.setContent(
                    "El teu compte ha sigut donat d'alta a Natura Mobles.<br>" +
                            "El teu Usuari es:<br>" +
                            "<b>" + usuari + "</b><br>" +
                            "Contrasenya provisional es:<br>" +
                            "<b>" + contrasenya + "</b>",
                    "text/html; charset=utf-8");
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void encriptarContrasenya(User user) {
        String password = user.getPassword();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(password);
        user.setPassword(hashedPassword);
    }

    @Override
    public Long extractUserId(String userDetailsString) {
        Pattern pattern = Pattern.compile("id=(\\d+)");
        Matcher matcher = pattern.matcher(userDetailsString);
        if (matcher.find()) {
            String userIdString = matcher.group(1);
            return Long.parseLong(userIdString);
        } else {
            return null;
        }
    }

    @Override
    public Boolean comprobarPassword(String password1, String password2) {
        return password1.equals(password2);
    }

    public List<User> buscarAdmins() {
        return userRepository.findByRole(Role.ROLE_ADMIN);
    }

    @Override
    public List<User> buscarUsers() {
        return userRepository.findByRole(Role.ROLE_USER);
    }

    @Override
    public List<User> buscarRRHH() {
        return userRepository.findByRole(Role.ROLE_RRHH);
    }

    @Override
    public List<User> buscarInventari() {
        return userRepository.findByRole(Role.ROLE_INVENTARI);
    }

    @Override
    public List<User> buscarFinances() {
        return userRepository.findByRole(Role.ROLE_FINANCES);
    }

    @Override
    public User buscarPerUsuari(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            return userOpt.get();
        } else {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
    }
}