package SegundUM.pasarela.rest.controller;

import SegundUM.pasarela.puertos.PuertoUsuarios;
import SegundUM.pasarela.rest.dto.LoginRequest;
import SegundUM.pasarela.rest.dto.LoginResponse;
import SegundUM.pasarela.rest.dto.UsuarioDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final PuertoUsuarios puertoUsuarios;
    private static final String SECRET_KEY = "secreto_compartido_segundum_2026";
    private static final long EXPIRATION_TIME = 864_000_000; // 10 days

    @Autowired
    public AuthController(PuertoUsuarios puertoUsuarios) {
        this.puertoUsuarios = puertoUsuarios;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse httpResponse) throws IOException {
        
        UsuarioDTO usuario = puertoUsuarios.verificarCredenciales(
                loginRequest.getEmail(), loginRequest.getClave());

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

            // Enviar token en cookie
            Cookie jwtCookie = new Cookie("jwt", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            // jwtCookie.setSecure(true); // para https
            httpResponse.addCookie(jwtCookie);

            LoginResponse responseBody = new LoginResponse(token, usuario.getId(), usuario.getNombreCompleto(), roles);
            return ResponseEntity.ok(responseBody);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        Cookie jwtCookie = new Cookie("jwt", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);
        return ResponseEntity.ok().build();
    }
}
