package group2.projecte2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import group2.projecte2.serveis.implementacio.UserDetailsServiceImplement;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final AuthenticationSuccessHandler authenticationSuccessHandler;

    public WebSecurityConfig(AuthenticationSuccessHandler authenticationSuccessHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImplement();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/styles/**", "/imatges/**", "/js/**").permitAll()
                        .requestMatchers("/taulerDeControlAdmin").hasRole("ADMIN")
                        .requestMatchers("/taulerDeControlRRHH").hasRole("RRHH")
                        .requestMatchers("/taulerDeControlINVENTARI").hasRole("INVENTARI")
                        .requestMatchers("/taulerDeControlFINANCES").hasRole("FINANCES")
                        .requestMatchers("/usuaris/**").hasRole("ADMIN")
                        .requestMatchers("/productes/**").hasAnyRole("INVENTARI", "ADMIN")
                        .requestMatchers("/finances/**").hasAnyRole("FINANCES", "ADMIN")
                        .requestMatchers("/empleats/**").hasAnyRole("RRHH", "ADMIN")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(authenticationSuccessHandler)
                        .permitAll())
                .logout(logout -> logout
                        .permitAll())
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/access-denied"));
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}