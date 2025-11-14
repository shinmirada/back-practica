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
import java.util.stream.Collectors;

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
    public ResponseEntity<List<PedidoResponseDTO>> getAllPedidos() {
        List<Pedido> pedidos = pedidoService.findAll();
        List<PedidoResponseDTO> response = pedidos.stream()
            .map(PedidoResponseDTO::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener pedido por ID", description = "Devuelve un pedido específico basado en su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    public ResponseEntity<PedidoResponseDTO> getPedidoById(@PathVariable Integer id) {
        return pedidoService.findById(id)
            .map(pedido -> ResponseEntity.ok(new PedidoResponseDTO(pedido)))
            .orElse(ResponseEntity.notFound().build());
    }
    
    
    @GetMapping("/estado/{estado}")
    @Operation(summary = "Obtener pedidos por estado", description = "Devuelve una lista de pedidos basado en su estado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedidos encontrados"),
        @ApiResponse(responseCode = "404", description = "No hay pedidos con ese estado")
    })
    public ResponseEntity<List<PedidoResponseDTO>> getPedidosByEstado(@PathVariable Estado estado) {
        List<Pedido> pedidos = pedidoService.findByEstado(estado);
        List<PedidoResponseDTO> response = pedidos.stream()
            .map(PedidoResponseDTO::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cliente/{documento}")
    @Operation(summary = "Obtener pedidos por cliente", description = "Devuelve una lista de pedidos basado en el documento del cliente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedidos encontrados"),
        @ApiResponse(responseCode = "404", description = "No hay pedidos para este cliente")
    })
    public ResponseEntity<List<PedidoResponseDTO>> getPedidosByCliente(@PathVariable String documento) {
        List<Pedido> pedidos = pedidoService.findByClienteDoc(documento);
        if (pedidos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<PedidoResponseDTO> response = pedidos.stream()
            .map(PedidoResponseDTO::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }


    @PostMapping
    @Operation(summary = "Crear un nuevo pedido", description = "Crea un nuevo pedido con múltiples platos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pedido creado con éxito"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<PedidoResponseDTO> createPedido(@RequestBody PedidoRequestDTO pedidoRequest) {
        try {
            Pedido nuevoPedido = pedidoService.realizarPedido(pedidoRequest);
            return new ResponseEntity<>(new PedidoResponseDTO(nuevoPedido), HttpStatus.CREATED);
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
    public ResponseEntity<PedidoResponseDTO> updateEstado(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        try {
            Estado nuevoEstado = Estado.valueOf(body.get("estado"));
            return pedidoService.updateEstado(id, nuevoEstado)
                .map(pedido -> ResponseEntity.ok(new PedidoResponseDTO(pedido)))
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
    public ResponseEntity<Void> deletePedido(@PathVariable Integer id) {
        if (pedidoService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
