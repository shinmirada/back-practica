package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FacturaService {

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Factura> findAllFacturas() {
        return facturaRepository.findAll();
    }

    public Optional<Factura> findByCodigo(String codigo) {
        return facturaRepository.findById(codigo);
    }

    public List<Factura> findAllFacturasByUsuarioDoc(String documento) {
        return facturaRepository.findByUsuarioDocumento(documento);
    }

    public List<Factura> findByPedidoId(int id) {
        return facturaRepository.findByPedidoId(id);
    }

    @Transactional
    public Factura saveFactura(String usuarioDoc, Integer pedidoId) {
        // 1. Validar que el pedido exista
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado con id: " + pedidoId));

        // 2. Validar que el usuario exista
        Usuario usuario = usuarioRepository.findById(usuarioDoc)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con documento: " + usuarioDoc));

        // 3. Calcular el total sumando todos los items del pedido
        BigDecimal total = BigDecimal.ZERO;
        for (ItemPedido item : pedido.getItems()) {
            BigDecimal subtotal = item.getPrecioUnitario()
                .multiply(new BigDecimal(item.getCantidad()));
            total = total.add(subtotal);
        }

        // 4. Crear la factura
        Factura factura = new Factura();
        factura.setCodigo(UUID.randomUUID().toString());
        factura.setUsuario(usuario);
        factura.setPedido(pedido);
        factura.setTotal(total);

        return facturaRepository.save(factura);
        }
}

