package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    public Optional<Factura> findByPedidoIdAndUsuarioDoc(int pedidoId, String documento) {
        return facturaRepository.findByPedidoIdAndUsuarioDocumento(pedidoId, documento);
    }

    public Optional<Factura> findByFacturaIdAndUsuarioDoc(Long facturaId, String documento) {
        return facturaRepository.findByIdAndUsuarioDocumento(facturaId, documento);
    }


    @Transactional
    public Factura saveFactura(String usuarioDoc, Integer pedidoId) {

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con id: " + pedidoId));

        Usuario usuario = usuarioRepository.findById(usuarioDoc)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con documento: " + usuarioDoc));

        BigDecimal total = BigDecimal.ZERO;
        for (ItemPedido item : pedido.getItems()) {
            BigDecimal subtotal = item.getPrecioUnitario()
                    .multiply(new BigDecimal(item.getCantidad()));
            total = total.add(subtotal);
        }

        Factura factura = new Factura();
        factura.setUsuario(usuario);
        factura.setPedido(pedido);
        factura.setTotal(total);
        factura.setFecha(LocalDateTime.now());

        return facturaRepository.save(factura);
    }

}

