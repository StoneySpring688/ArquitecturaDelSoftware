package SegundUM.pasarela.config;

import SegundUM.pasarela.puertos.PuertoUsuarios;
import SegundUM.pasarela.rest.dto.UsuarioDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private PuertoUsuarios puertoUsuarios;

    private static final String SECRET_KEY = "secreto_compartido_segundum_2026";
    private static final long EXPIRATION_TIME = 864_000_000; // 10 days

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/auth/**").permitAll()
            .antMatchers("/login/**", "/oauth2/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .oauth2Login()
                .successHandler(githubSuccessHandler());
    }

    @Bean
    public AuthenticationSuccessHandler githubSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
                String githubId = oAuth2User.getAttribute("id").toString();
                
                UsuarioDTO usuario = puertoUsuarios.verificarGitHub(githubId);
                
                if (usuario != null) {
                    List<String> roles = new ArrayList<>();
                    roles.add("USUARIO");
                    if (usuario.isAdministrador()) {
                        roles.add("ADMINISTRADOR");
                    }

                    String token = Jwts.builder()
                            .setSubject(usuario.getId())
                            .claim("name", usuario.getNombreCompleto())
                            .claim("roles", roles)
                            .setIssuedAt(new Date())
                            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                            .signWith(SignatureAlgorithm.HS512, SECRET_KEY.getBytes())
                            .compact();

                    Cookie jwtCookie = new Cookie("jwt", token);
                    jwtCookie.setHttpOnly(true);
                    jwtCookie.setPath("/");
                    response.addCookie(jwtCookie);
                    
                    // Redirigir al frontend o a una página de éxito
                    response.sendRedirect("http://localhost:8090/success-login.html?token=" + token);
                } else {
                    response.sendRedirect("http://localhost:8090/error-login.html?error=usuario_no_vinculado");
                }
            }
        };
    }
}
