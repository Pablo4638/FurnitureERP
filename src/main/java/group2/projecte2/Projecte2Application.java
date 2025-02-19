package group2.projecte2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.stripe.Stripe;

@SpringBootApplication
public class Projecte2Application {
	public static void main(String[] args) {
		Stripe.apiKey = "UR STIRPE APIKEY";
		SpringApplication.run(Projecte2Application.class, args);
	}
}	