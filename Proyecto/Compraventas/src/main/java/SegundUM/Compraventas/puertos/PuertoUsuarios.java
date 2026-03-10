package SegundUM.Compraventas.puertos;

import SegundUM.Compraventas.rest.dto.UsuarioDTO;

public interface PuertoUsuarios {
	
	UsuarioDTO obtenerDatosUsuario(String idUsuario, String token);
	
}
