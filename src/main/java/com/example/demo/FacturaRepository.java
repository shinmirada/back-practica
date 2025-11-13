package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, String> {
    
    List<Factura> findByUsuarioDocumento(String documento);
    List<Factura>  findByPedidoId(int id);
}

