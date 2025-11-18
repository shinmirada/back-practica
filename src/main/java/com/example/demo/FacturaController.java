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

@RestController
@RequestMapping("/api/facturas")
@Tag(name = "Facturas", description = "API para la gestión de facturas del restaurante")
public class FacturaController {

	@Autowired
	private FacturaService facturaService;

	@GetMapping
	@Transactional(readOnly = true)
	@Operation(summary = "Obtener todas las facturas", description = "Devuelve una lista completa de todas las facturas generadas en el sistema. "
			+ "Incluye información del pedido asociado y el usuario que realizó la transacción.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Lista de facturas obtenida exitosamente"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor al procesar la solicitud") })
	public ResponseEntity<List<Factura>> getAllFacturas() {
		List<Factura> facturas = facturaService.findAllFacturas();
		return ResponseEntity.ok(facturas);
	}

	@GetMapping("/codigo/{codigo}")
	@Transactional(readOnly = true)
	@Operation(summary = "Obtener factura por código", description = "Busca y devuelve una factura específica utilizando su código único de identificación.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Factura encontrada exitosamente"),
			@ApiResponse(responseCode = "404", description = "No se encontró ninguna factura con el código especificado") })
	public ResponseEntity<Factura> getFacturaByCodigo(
			@PathVariable @Parameter(description = "Código único de la factura", example = "1") String codigo) {
		return facturaService.findByCodigo(codigo).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/documento/{documento}")
	@Transactional(readOnly = true)
	@Operation(summary = "Obtener facturas por documento de usuario", description = "Devuelve todas las facturas asociadas a un usuario específico mediante su número de documento. "
			+ "Útil para que los clientes consulten su historial de compras.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Facturas del usuario encontradas"),
			@ApiResponse(responseCode = "404", description = "El usuario no tiene facturas registradas o no existe") })
	public ResponseEntity<List<Factura>> getFacturasByUsuario(
			@PathVariable @Parameter(description = "Número de documento del usuario", example = "111") String documento) {
		List<Factura> facturas = facturaService.findAllFacturasByUsuarioDoc(documento);
		if (facturas.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(facturas);
	}

	@GetMapping("/pedido/{pedidoId}/usuario/{documento}")
	@Transactional(readOnly = true)
	@Operation(summary = "Obtener factura por pedido y usuario", description = "Busca una factura específica utilizando el ID del pedido y el documento del usuario. "
			+ "Este endpoint es útil para verificar si un pedido ya tiene factura generada.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Factura encontrada exitosamente"),
			@ApiResponse(responseCode = "404", description = "No existe factura para la combinación de pedido y usuario especificada") })
	public ResponseEntity<Factura> getFacturaPorPedido(
			@PathVariable @Parameter(description = "ID del pedido", example = "1") int pedidoId,
			@PathVariable @Parameter(description = "Número de documento del usuario", example = "111") String documento) {
		return facturaService.findByPedidoIdAndUsuarioDoc(pedidoId, documento).map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/codigo/{facturaId}/usuario/{documento}")
	@Transactional(readOnly = true)
	@Operation(summary = "Obtener factura por ID de factura y usuario", description = "Busca una factura específica mediante su ID único y valida que pertenezca al usuario especificado. "
			+ "Proporciona una capa adicional de seguridad al verificar la propiedad de la factura.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Factura encontrada y validada correctamente"),
			@ApiResponse(responseCode = "404", description = "La factura no existe o no pertenece al usuario especificado") })
	public ResponseEntity<Factura> getFacturaPorCodigo(
			@PathVariable @Parameter(description = "ID único de la factura", example = "1") long facturaId,
			@PathVariable @Parameter(description = "Número de documento del usuario", example = "111") String documento) {
		return facturaService.findByFacturaIdAndUsuarioDoc(facturaId, documento).map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	@Transactional
	@Operation(summary = "Crear una nueva factura", description = "Genera una nueva factura para un pedido específico. El sistema calcula automáticamente "

			+ "- El pedido debe existir y estar en estado FINALIZADO\n"

			+ "- No puede existir una factura previa para el mismo pedido")
	
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Factura creada exitosamente. Retorna el objeto completo con ID generado y total calculado"),
			@ApiResponse(responseCode = "400", description = "Error en los datos proporcionados. Causas posibles:\n"),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor al procesar la creación") })
	public ResponseEntity<Factura> createFactura(
			@RequestBody @Parameter(description = "Datos necesarios para crear la factura. Debe contener 'usuarioDoc' y 'pedidoId'", required = true) Map<String, Object> facturaData) {
		try {
			String usuarioDoc = (String) facturaData.get("usuarioDoc");
			Integer pedidoId = (Integer) facturaData.get("pedidoId");

			// Validación de campos requeridos
			if (usuarioDoc == null || usuarioDoc.trim().isEmpty()) {
				return ResponseEntity.badRequest().body(null);
			}
			if (pedidoId == null) {
				return ResponseEntity.badRequest().body(null);
			}

			Factura newFactura = facturaService.saveFactura(usuarioDoc, pedidoId);

			// Verificar que la factura se creó correctamente con ID
			if (newFactura != null && newFactura.getId() != null) {
				return new ResponseEntity<>(newFactura, HttpStatus.CREATED);
			} else {
				return ResponseEntity.badRequest().body(null);
			}
		} catch (RuntimeException e) {
			// Log del error para debugging
			System.err.println("Error al crear factura: " + e.getMessage());
			return ResponseEntity.badRequest().body(null);
		}
	}
}
