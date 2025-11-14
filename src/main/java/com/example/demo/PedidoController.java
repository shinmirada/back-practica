package com.example.demo;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "API para la gestión de pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    @Operation(summary = "Obtener todos los pedidos", description = "Devuelve una lista de todos los pedidos existentes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de pedidos obtenida con éxito"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<Pedido>> getAllPedidos() {
        return ResponseEntity.ok(pedidoService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener pedido por ID", description = "Devuelve un pedido específico basado en su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    public ResponseEntity<Pedido> getPedidoById(
            @PathVariable @Parameter(description = "ID del pedido") Integer id) {
        return pedidoService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Obtener pedidos por estado", description = "Devuelve una lista de pedidos basado en su estado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedidos encontrados"),
        @ApiResponse(responseCode = "404", description = "No hay pedidos con ese estado")
    })
    public ResponseEntity<List<Pedido>> getPedidosByEstado(
            @PathVariable @Parameter(description = "Estado del pedido") Estado estado) {
        List<Pedido> pedidos = pedidoService.findByEstado(estado);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/cliente/{documento}")
    @Operation(summary = "Obtener pedidos por cliente", description = "Devuelve una lista de pedidos basado en el documento del cliente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedidos encontrados"),
        @ApiResponse(responseCode = "404", description = "No hay pedidos para este cliente")
    })
    public ResponseEntity<List<Pedido>> getPedidosByCliente(
            @PathVariable @Parameter(description = "Documento del cliente") String documento) {
        List<Pedido> pedidos = pedidoService.findByClienteDoc(documento);
        if (pedidos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
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
            Pedido nuevoPedido = pedidoService.realizarPedido(pedidoRequest);
            return new ResponseEntity<>(nuevoPedido, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado del pedido", description = "Actualiza el estado de un pedido específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado actualizado con éxito"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    public ResponseEntity<Pedido> updateEstado(
            @PathVariable @Parameter(description = "ID del pedido") Integer id,
            @RequestBody @Parameter(description = "Nuevo estado") Map<String, String> body) {
        try {
            Estado nuevoEstado = Estado.valueOf(body.get("estado"));
            return pedidoService.updateEstado(id, nuevoEstado)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
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
        if (pedidoService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
