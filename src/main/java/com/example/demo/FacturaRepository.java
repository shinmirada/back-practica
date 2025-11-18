package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {  // ✅ Cambió de String a Long
    
    List<Factura> findByUsuarioDocumento(String documento);

    Optional<Factura> findByPedidoIdAndUsuarioDocumento(int pedidoId, String documento);

    Optional<Factura> findByIdAndUsuarioDocumento(Long id, String documento);
}
