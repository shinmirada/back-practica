package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PlatoRepository platoRepository;

    public List<Pedido> findAll() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> findById(Integer id) {
        return pedidoRepository.findById(id);
    }

    public List<Pedido> findByEstado(Estado estado) {
        return pedidoRepository.findByEstado(estado);
    }

    public List<Pedido> findByClienteDoc(String documento) {
        return pedidoRepository.findByClienteDocumento(documento);
    }

    @Transactional
    public Pedido realizarPedido(PedidoRequestDTO pedidoRequest) {
        // 1. Validar que el usuario exista
        Usuario cliente = usuarioRepository.findById(pedidoRequest.getClienteDoc())
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado con documento: " 
                + pedidoRequest.getClienteDoc()));

        // 2. Crear la cabecera del Pedido
        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setCliente(cliente);
        nuevoPedido.setEsDomicilio(pedidoRequest.getEsDomicilio() != null ? 
            pedidoRequest.getEsDomicilio() : false);
        nuevoPedido.setEstado(Estado.PENDIENTE);

        List<ItemPedido> items = new ArrayList<>();

        // 3. Procesar cada item del carrito (DTO)
        for (ItemPedidoDTO itemDTO : pedidoRequest.getItems()) {
            // 3.1 Validar que el plato exista
            Plato plato = platoRepository.findById(itemDTO.getPlatoId())
                .orElseThrow(() -> new RuntimeException("Plato no encontrado con id: " 
                    + itemDTO.getPlatoId()));

            // 3.2 Crear el ItemPedido
            ItemPedido item = new ItemPedido();
            item.setPlato(plato);
            item.setCantidad(itemDTO.getCantidad());
            item.setPrecioUnitario(plato.getPrecio()); // Guarda el precio del momento
            item.setPedido(nuevoPedido); // Vincula este item al nuevo pedido

            items.add(item);
        }

        // 4. Asignar la lista de items al pedido
        nuevoPedido.setItems(items);

        // 5. Guardar el Pedido (y sus items, en cascada)
        return pedidoRepository.save(nuevoPedido);
    }

    public Optional<Pedido> updateEstado(Integer id, Estado estado) {
        return pedidoRepository.findById(id).map(pedido -> {
            pedido.setEstado(estado);
            return pedidoRepository.save(pedido);
        });
    }

    public boolean delete(Integer id) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
        if (pedidoOpt.isPresent() && pedidoOpt.get().getEstado() == Estado.FINALIZADO) {
            pedidoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}