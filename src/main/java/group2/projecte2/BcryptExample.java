package group2.projecte2;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptExample {
    public static void main(String[] args) {
    }

    public static void contrasenyaComp() {
        String passwordToHash = "adminpass";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(passwordToHash);
        Boolean isMatch = encoder.matches("", hashedPassword);
        System.out.println(isMatch);
    }
}