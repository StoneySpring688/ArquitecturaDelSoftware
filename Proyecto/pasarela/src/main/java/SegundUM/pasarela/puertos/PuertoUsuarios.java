package SegundUM.pasarela.puertos;

import SegundUM.pasarela.rest.dto.UsuarioDTO;

/**
 * Puerto de salida para la comunicación con el servicio de Usuarios.
 */
public interface PuertoUsuarios {
    UsuarioDTO verificarCredenciales(String email, String clave);
}
