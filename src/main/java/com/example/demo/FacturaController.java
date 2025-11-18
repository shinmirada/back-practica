package com.example.demo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/facturas")
@Tag(name = "Facturas", description = "API para la gestión de facturas")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    @GetMapping
    @Transactional(readOnly = true)
    @Operation(summary = "Obtener todas las facturas", description = "Devuelve una lista de todas las facturas existentes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de facturas obtenida con éxito"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<Factura>> getAllFacturas() {
        List<Factura> facturas = facturaService.findAllFacturas();
        // ✅ Forzar carga de relaciones dentro de la transacción
        facturas.forEach(f -> {
            if (f.getPedido() != null) {
                f.getPedido().getId();
                f.getPedido().getCliente().getNombre();
            }
            if (f.getUsuario() != null) {
                f.getUsuario().getNombre();
            }
        });
        return ResponseEntity.ok(facturas);
    }

    @GetMapping("/codigo/{codigo}")
    @Transactional(readOnly = true)
    @Operation(summary = "Obtener factura por código", description = "Devuelve una factura específica basada en su código")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Factura encontrada"),
        @ApiResponse(responseCode = "404", description = "Factura no encontrada")
    })
    public ResponseEntity<Factura> getFacturaByCodigo(
            @PathVariable @Parameter(description = "Código de la factura") String codigo) {
        return facturaService.findByCodigo(codigo)
            .map(factura -> {
                if (factura.getPedido() != null) {
                    factura.getPedido().getId();
                }
                if (factura.getUsuario() != null) {
                    factura.getUsuario().getNombre();
                }
                return ResponseEntity.ok(factura);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/documento/{documento}")
    @Transactional(readOnly = true)
    @Operation(summary = "Obtener facturas por documento de usuario", description = "Devuelve una lista de facturas de un usuario específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Facturas encontradas"),
        @ApiResponse(responseCode = "404", description = "No hay facturas para este usuario")
    })
    public ResponseEntity<List<Factura>> getFacturasByUsuario(
            @PathVariable @Parameter(description = "Documento del usuario") String documento) {
        List<Factura> facturas = facturaService.findAllFacturasByUsuarioDoc(documento);
        if (facturas.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        // ✅ Forzar carga de relaciones
        facturas.forEach(f -> {
            if (f.getPedido() != null) {
                f.getPedido().getId();
                f.getPedido().getCliente().getNombre();
            }
            if (f.getUsuario() != null) {
                f.getUsuario().getNombre();
            }
        });
        return ResponseEntity.ok(facturas);
    }

    @GetMapping("/pedido/{pedidoId}/usuario/{documento}")
    @Transactional(readOnly = true)
    @Operation(summary = "Obtener facturas por id de pedido", description = "Devuelve una lista de facturas de "
    		+ "un id de pedido específico de un cliente especifico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Facturas encontradas"),
            @ApiResponse(responseCode = "404", description = "No hay facturas para este pedido")
    })
    public ResponseEntity<Factura> getFacturaPorPedido(
            @PathVariable @Parameter(description = "id pedido") int pedidoId,
            @PathVariable @Parameter(description = "Documento del usuario") String documento) {
        return facturaService.findByPedidoIdAndUsuarioDoc(pedidoId, documento)
                .map(factura -> {
                    if (factura.getPedido() != null) {
                        factura.getPedido().getId();
                    }
                    if (factura.getUsuario() != null) {
                        factura.getUsuario().getNombre();
                    }
                    return ResponseEntity.ok(factura);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    
    @GetMapping("/codigo/{facturaId}/usuario/{documento}")
    @Transactional(readOnly = true)
    @Operation(summary = "Obtener facturas por id de factura", description = "Devuelve una factura de "
    		+ "un id de factura específico de un cliente especifico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Facturas encontradas"),
            @ApiResponse(responseCode = "404", description = "No hay facturas para este usuario")
    })
    public ResponseEntity<Factura> getFacturaPorCodigo(
            @PathVariable @Parameter(description = "id factura") long facturaId,
            @PathVariable @Parameter(description = "Documento del usuario") String documento) {
    	  return facturaService.findByFacturaIdAndUsuarioDoc(facturaId, documento)
                  .map(factura -> {
                      if (factura.getPedido() != null) {
                          factura.getPedido().getId();
                      }
                      if (factura.getUsuario() != null) {
                          factura.getUsuario().getNombre();
                      }
                      return ResponseEntity.ok(factura);
                  })
                  .orElse(ResponseEntity.notFound().build());
    }
    
    
    @PostMapping
    @Transactional  // ✅ IMPORTANTE: Sin readOnly para permitir escritura
    @Operation(summary = "Crear una factura", description = "Crea una nueva factura para un pedido específico. OBLIGATORIOS: usuarioDoc, pedidoId")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Factura creada con éxito"),
        @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados")
    })
    public ResponseEntity<Factura> createFactura(
            @RequestBody @Parameter(description = "Datos de la factura (usuarioDoc y pedidoId)") 
            Map<String, Object> facturaData) {
        try {
            String usuarioDoc = (String) facturaData.get("usuarioDoc");
            Integer pedidoId = (Integer) facturaData.get("pedidoId");
            
            Factura newFactura = facturaService.saveFactura(usuarioDoc, pedidoId);
            
            // ✅ Forzar carga de relaciones antes de serializar
            if (newFactura.getPedido() != null) {
                newFactura.getPedido().getId();
            }
            if (newFactura.getUsuario() != null) {
                newFactura.getUsuario().getNombre();
            }
            
            return new ResponseEntity<>(newFactura, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}

