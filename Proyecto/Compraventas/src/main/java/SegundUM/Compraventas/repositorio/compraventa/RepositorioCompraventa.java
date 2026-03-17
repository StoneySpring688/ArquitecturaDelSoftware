package SegundUM.Compraventas.repositorio.compraventa;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import SegundUM.Compraventas.dominio.Compraventa;

@NoRepositoryBean
public interface RepositorioCompraventa extends PagingAndSortingRepository<Compraventa, String> {

	/**
	 * Busca las compraventas realizadas por un vendedor específico.
	 **/
	Page<Compraventa> findByIdVendedor(String idVendedor, Pageable pageable);

	/**
	 * Busca las compraventas realizadas por un comprador específico.
	 **/
	Page<Compraventa> findByIdComprador(String idComprador, Pageable pageable);

	/**
	 * Busca las compraventas realizadas entre un comprador y un vendedor específicos.
	 **/
	Page<Compraventa> findByIdCompradorAndIdVendedor(String idComprador, String idVendedor, Pageable pageable);

	List<Compraventa> findByIdVendedor(String idVendedor);

	List<Compraventa> findByIdComprador(String idComprador);

	List<Compraventa> findByIdProducto(String idProducto);
}
