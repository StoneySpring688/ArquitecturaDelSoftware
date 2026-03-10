package SegundUM.Compraventas.adaptadores.Spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import SegundUM.Compraventas.puertos.PuertoAutenticacion;
import SegundUM.Compraventas.puertos.PuertoUsuarios;
import SegundUM.Compraventas.rest.dto.UsuarioDTO;

@Component
@ConditionalOnProperty(name="usuarios.adaptador", havingValue="spring")
public class AdaptadorUsuariosSpring implements PuertoUsuarios, PuertoAutenticacion{
	
private final RestTemplate restTemplate;
	
	@Value("${api.usuarios.url}")
	private String urlBase;
	
	public AdaptadorUsuariosSpring(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	@Override
	public UsuarioDTO obtenerDatosUsuario(String idUsuario, String token) {
		HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        
        ResponseEntity<UsuarioDTO> response = restTemplate.exchange(
                urlBase + idUsuario,
                HttpMethod.GET,
                requestEntity,
                UsuarioDTO.class
        );
        
		return response.getBody();
	}
	
	@Override
	public String login(String email, String clave) {

		String authUrl = urlBase.replace("usuarios/", "auth/login");
		
		// 2. Configuramos las cabeceras para enviar un formulario HTML estándar (requerido por @FormParam)
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> credenciales = new LinkedMultiValueMap<>();
		credenciales.add("email", email);
		credenciales.add("clave", clave);

		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(credenciales, headers);

		try {
			return restTemplate.postForObject(authUrl, requestEntity, String.class);
		} catch (Exception e) {
			throw new RuntimeException("Error al hacer login con Spring RestTemplate: " + e.getMessage(), e);
		}
	}
	
}

