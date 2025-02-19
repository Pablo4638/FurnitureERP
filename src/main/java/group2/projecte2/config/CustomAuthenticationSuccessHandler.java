package group2.projecte2.config;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
            Authentication authentication) throws IOException, ServletException {
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        String info = authentication.getPrincipal().toString();
        boolean isEsNouTrue = parseEsNouValue(info);
        if (!isEsNouTrue) {
            httpServletResponse.sendRedirect("/canviContrasenya");
        } else {
            if (roles.contains("ROLE_ADMIN")) {
                httpServletResponse.sendRedirect("taulerDeControlAdmin");
            } else if (roles.contains("ROLE_RRHH")) {
                httpServletResponse.sendRedirect("taulerDeControlRRHH");
            } else if (roles.contains("ROLE_FINANCES")) {
                httpServletResponse.sendRedirect("taulerDeControlFINANCES");
            } else if (roles.contains("ROLE_INVENTARI")) {
                httpServletResponse.sendRedirect("taulerDeControlINVENTARI");
            } else {
                httpServletResponse.sendRedirect("taulerDeControlUser");
            }
        }
    }

    private static boolean parseEsNouValue(String userDetailsString) {
        Pattern pattern = Pattern.compile("esNou=(true|false)");
        Matcher matcher = pattern.matcher(userDetailsString);
        if (matcher.find()) {
            String esNouValue = matcher.group(1);
            return esNouValue.equals("true");
        } else {
            return false;
        }
    }

}