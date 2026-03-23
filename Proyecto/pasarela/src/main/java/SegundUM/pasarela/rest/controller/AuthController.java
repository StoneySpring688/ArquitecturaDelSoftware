package SegundUM.pasarela.rest.controller;

import SegundUM.pasarela.puertos.PuertoUsuarios;
import SegundUM.pasarela.rest.dto.LoginRequest;
import SegundUM.pasarela.rest.dto.LoginResponse;
import SegundUM.pasarela.rest.dto.UsuarioDTO;
import SegundUM.pasarela.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final PuertoUsuarios puertoUsuarios;
    private final JwtUtils jwtUtils;

    @Autowired
    public AuthController(PuertoUsuarios puertoUsuarios, JwtUtils jwtUtils) {
        this.puertoUsuarios = puertoUsuarios;
        this.jwtUtils = jwtUtils;
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

            String token = jwtUtils.generateToken(usuario.getId(), usuario.getNombreCompleto(), roles);

            // Enviar token en cookie
            Cookie jwtCookie = new Cookie("jwt", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge((int) (JwtUtils.EXPIRATION_TIME / 1000));
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
