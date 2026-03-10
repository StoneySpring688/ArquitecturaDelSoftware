package SegundUM.Compraventas.adaptadores.Retrofit;

import SegundUM.Compraventas.rest.dto.ProductoDTO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiProductosRetrofit {
	
	@GET("{id}")
	Call<ProductoDTO> getProducto(@Path("id") String id);
}
