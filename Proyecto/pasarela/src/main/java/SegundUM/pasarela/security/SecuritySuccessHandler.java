package SegundUM.pasarela.security;

import SegundUM.pasarela.puertos.PuertoUsuarios;
import SegundUM.pasarela.rest.dto.UsuarioDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SecuritySuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(SecuritySuccessHandler.class);

    private final PuertoUsuarios puertoUsuarios;
    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public SecuritySuccessHandler(PuertoUsuarios puertoUsuarios, JwtUtils jwtUtils) {
        this.puertoUsuarios = puertoUsuarios;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();
        
        // El ID de GitHub suele venir en el atributo "login" o "id"
        String githubId = oauthUser.getAttributes().get("id").toString();
        
        UsuarioDTO usuario = puertoUsuarios.verificarGitHub(githubId);

        if (usuario == null) {
            // Si el usuario de GitHub no está registrado en SegundUM, lo registramos automáticamente
            String nombre = oauthUser.getAttribute("name");
            if (nombre == null) nombre = oauthUser.getAttribute("login");
            String email = oauthUser.getAttribute("email");
            
            usuario = puertoUsuarios.registrarUsuarioGitHub(githubId, nombre, email);
        }

        if (usuario != null) {
            List<String> roles = new ArrayList<>();
            roles.add("USUARIO");
            if (usuario.isAdministrador()) {
                roles.add("ADMINISTRADOR");
            }

            String token = jwtUtils.generateToken(usuario.getId(), usuario.getNombreCompleto(), roles);
            logger.info("JWT generado para usuario GitHub {}: {}", githubId, usuario.getId());

            // Enviar token en cookie HTTP-Only
            Cookie jwtCookie = new Cookie("jwt", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge((int) (JwtUtils.EXPIRATION_TIME / 1000));
            response.addCookie(jwtCookie);

            // Devolver JSON con la información solicitada en el enunciado
            Map<String, Object> body = new HashMap<>();
            body.put("token", token);
            body.put("id", usuario.getId());
            body.put("nombre", usuario.getNombreCompleto());
            body.put("roles", roles);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(body));
        } else {
            // Si el usuario de GitHub no está registrado en SegundUM
            logger.warn("Usuario de GitHub no vinculado a SegundUM: {}", githubId);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Usuario de GitHub no vinculado a SegundUM");
        	
        	//TODO para desarrollo
			/*
			 * DefaultOAuth2User oauthUserOnError = (DefaultOAuth2User)
			 * authentication.getPrincipal(); Map<String, Object> atributos =
			 * oauthUserOnError.getAttributes();
			 * 
			 * response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			 * response.setContentType("application/json;charset=UTF-8");
			 * 
			 * Map<String, Object> errorDebug = new HashMap<>(); errorDebug.put("error",
			 * "Usuario de GitHub no vinculado en la BD de SegundUM");
			 * errorDebug.put("instrucciones",
			 * "Verifica que el 'login' o 'id' de abajo coincida con lo que tienes en tu tabla de usuarios"
			 * ); errorDebug.put("datos_recibidos_de_github", atributos);
			 * 
			 * // 4. Lo escribimos en la pantalla
			 * response.getWriter().write(objectMapper.writerWithDefaultPrettyPrinter().
			 * writeValueAsString(errorDebug));
			 */
        }
    }
}
