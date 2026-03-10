package SegundUM.Compraventas.repositorio.compraventa;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import SegundUM.Compraventas.dominio.Compraventa;

@Repository
public interface RepositorioCompraventaMongo extends RepositorioCompraventa, MongoRepository<Compraventa, String> {
	
	
}
