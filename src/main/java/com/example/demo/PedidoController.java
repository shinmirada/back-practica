package com.example.demo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "API para la gestión de pedidos")
public class PedidoController {

    private static final Logger logger = LoggerFactory.getLogger(PedidoController.class);

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    @Transactional(readOnly = true)
    @Operation(summary = "Obtener todos los pedidos", description = "Devuelve una lista de todos los pedidos existentes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de pedidos obtenida con éxito"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<Pedido>> getAllPedidos() {
        try {
            logger.info("🔍 Obteniendo todos los pedidos...");
            List<Pedido> pedidos = pedidoService.findAll();
            logger.info("📦 Se encontraron {} pedidos", pedidos.size());
            
            pedidos.forEach(p -> {
                logger.debug("Cargando items del pedido #{}", p.getId());
                p.getItems().size();
                p.getCliente().getNombre();
            });
            
            logger.info("✅ Pedidos cargados correctamente");
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            logger.error("❌ Error al obtener pedidos: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @Operation(summary = "Obtener pedido por ID", description = "Devuelve un pedido específico basado en su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    public ResponseEntity<Pedido> getPedidoById(
            @PathVariable @Parameter(description = "ID del pedido") Integer id) {
        logger.info("🔍 Buscando pedido con ID: {}", id);
        return pedidoService.findById(id)
            .map(pedido -> {
                pedido.getItems().size();
                logger.info("✅ Pedido #{} encontrado", id);
                return ResponseEntity.ok(pedido);
            })
            .orElseGet(() -> {
                logger.warn("⚠️ Pedido #{} no encontrado", id);
                return ResponseEntity.notFound().build();
            });
    }

    @GetMapping("/estado/{estado}")
    @Transactional(readOnly = true)
    @Operation(summary = "Obtener pedidos por estado", description = "Devuelve una lista de pedidos basado en su estado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedidos encontrados"),
        @ApiResponse(responseCode = "404", description = "No hay pedidos con ese estado")
    })
    public ResponseEntity<List<Pedido>> getPedidosByEstado(
            @PathVariable @Parameter(description = "Estado del pedido") Estado estado) {
        logger.info("🔍 Buscando pedidos con estado: {}", estado);
        List<Pedido> pedidos = pedidoService.findByEstado(estado);
        pedidos.forEach(p -> p.getItems().size());
        logger.info("📦 Se encontraron {} pedidos con estado {}", pedidos.size(), estado);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/cliente/{documento}")
    @Transactional(readOnly = true)
    @Operation(summary = "Obtener pedidos por cliente", description = "Devuelve una lista de pedidos basado en el documento del cliente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedidos encontrados"),
        @ApiResponse(responseCode = "404", description = "No hay pedidos para este cliente")
    })
    public ResponseEntity<List<Pedido>> getPedidosByCliente(
            @PathVariable @Parameter(description = "Documento del cliente") String documento) {
        logger.info("🔍 Buscando pedidos del cliente: {}", documento);
        List<Pedido> pedidos = pedidoService.findByClienteDoc(documento);
        if (pedidos.isEmpty()) {
            logger.warn("⚠️ No se encontraron pedidos para el cliente {}", documento);
            return ResponseEntity.notFound().build();
        }
        pedidos.forEach(p -> p.getItems().size());
        logger.info("✅ Se encontraron {} pedidos para el cliente {}", pedidos.size(), documento);
        return ResponseEntity.ok(pedidos);
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo pedido", description = "Crea un nuevo pedido con múltiples platos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pedido creado con éxito"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<Pedido> createPedido(
            @RequestBody @Parameter(description = "Datos del pedido (clienteDoc, esDomicilio, items)") 
            PedidoRequestDTO pedidoRequest) {
        try {
            logger.info("📝 Creando nuevo pedido para cliente: {}", pedidoRequest.getClienteDoc());
            Pedido nuevoPedido = pedidoService.realizarPedido(pedidoRequest);
            logger.info("✅ Pedido #{} creado exitosamente", nuevoPedido.getId());
            return new ResponseEntity<>(nuevoPedido, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            logger.error("❌ Error al crear pedido: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado del pedido", description = "Actualiza el estado de un pedido específico usando DTO")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado actualizado con éxito"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
        @ApiResponse(responseCode = "400", description = "Estado inválido")
    })
    public ResponseEntity<Pedido> updateEstado(
            @PathVariable @Parameter(description = "ID del pedido") Integer id,
            @RequestBody @Parameter(description = "DTO con el nuevo estado") EstadoUpdateDTO dto) {
        try {
            logger.info("🔄 Actualizando estado del pedido #{} a {}", id, dto.getEstado());
            return pedidoService.updateEstado(id, dto.getEstado())
                .map(pedido -> {
                    logger.info("✅ Estado del pedido #{} actualizado a {}", id, dto.getEstado());
                    return ResponseEntity.ok(pedido);
                })
                .orElseGet(() -> {
                    logger.warn("⚠️ Pedido #{} no encontrado", id);
                    return ResponseEntity.notFound().build();
                });
        } catch (IllegalArgumentException e) {
            logger.error("❌ Estado inválido: {}", dto.getEstado());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un pedido", description = "Elimina un pedido SOLO si está FINALIZADO")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Pedido eliminado con éxito"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado o no está finalizado")
    })
    public ResponseEntity<Void> deletePedido(
            @PathVariable @Parameter(description = "ID del pedido") Integer id) {
        logger.info("🗑️ Intentando eliminar pedido #{}", id);
        if (pedidoService.delete(id)) {
            logger.info("✅ Pedido #{} eliminado", id);
            return ResponseEntity.noContent().build();
        }
        logger.warn("⚠️ No se pudo eliminar el pedido #{}", id);
        return ResponseEntity.notFound().build();
    }
}