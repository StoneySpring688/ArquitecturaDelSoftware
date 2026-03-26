package SegundUM.Productos.rest.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Filtro que extrae la identidad del usuario desde un token JWT
 * presente en la cabecera Authorization o en una cookie.
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    // Secreto compartido con la Pasarela
    private final String SECRETO = "secreto_compartido_segundum_2026";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String jwt = null;

        // 1. Intentar obtener el token de la cabecera Authorization
        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
        } 
        // 2. Intentar obtenerlo de la cookie 'jwt'
        else if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("jwt")) {
                    jwt = cookie.getValue();
                }
            }
        }

        if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                Claims claims = Jwts.parser()
                        .setSigningKey(SECRETO.getBytes())
                        .parseClaimsJws(jwt)
                        .getBody();

                String username = claims.getSubject(); // ID del usuario
                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) claims.get("roles");

                if (username != null) {
                    ArrayList<GrantedAuthority> authorities = new ArrayList<>();
                    if (roles != null) {
                        for (String rol : roles) {
                            authorities.add(new SimpleGrantedAuthority(rol));
                        }
                    }

                    UsernamePasswordAuthenticationToken auth = 
                            new UsernamePasswordAuthenticationToken(username, null, authorities);
                    
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception e) {
                logger.error("Error al extraer info del token JWT: " + e.getMessage());
            }
        }

        chain.doFilter(request, response);
    }
}
