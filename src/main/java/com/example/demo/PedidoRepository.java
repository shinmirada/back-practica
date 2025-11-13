package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    
    List<Pedido> findByCliente(Usuario cliente);
    
    List<Pedido> findByEstado(Estado estado);
    
    List<Pedido> findByClienteDocumento(String documento);
}
