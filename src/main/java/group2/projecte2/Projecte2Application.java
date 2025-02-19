package group2.projecte2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.stripe.Stripe;

@SpringBootApplication
public class Projecte2Application {
	public static void main(String[] args) {
		Stripe.apiKey = "sk_test_51QVFTTFYX4xkM0hhplA9kalCEaCEgl2pOJvMZJcvX7wPlbh805xJ50BchK3yamaoBJHG1SYAZneTSSENMUJjgWwR00J2hpJZto";
		SpringApplication.run(Projecte2Application.class, args);
	}
}	