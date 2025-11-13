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

@RestController
@RequestMapping("/api/platos")
@Tag(name = "Platos", description = "API para la gestión de platos")
public class PlatoController {

    @Autowired
    private PlatoService platoService;

    @GetMapping
    @Operation(summary = "Obtener todos los platos", description = "Devuelve una lista de todos los platos existentes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de platos obtenida con éxito"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<Plato>> getAllPlatos() {
        return ResponseEntity.ok(platoService.getAllPlatos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un plato por ID", description = "Devuelve un plato específico por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Plato encontrado"),
        @ApiResponse(responseCode = "404", description = "Plato no encontrado")
    })
    public ResponseEntity<Plato> getPlatoById(
            @PathVariable @Parameter(description = "ID del plato") Integer id) {
        return platoService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo plato", description = "Crea un nuevo plato con los datos proporcionados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Plato creado con éxito"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<Plato> createPlato(
            @RequestBody @Parameter(description = "Datos del plato a crear") Plato plato) {
        Plato newPlato = platoService.savePlato(plato);
        return new ResponseEntity<>(newPlato, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar plato", description = "Actualiza la descripción y precio de un plato")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Plato actualizado"),
        @ApiResponse(responseCode = "404", description = "Plato no encontrado")
    })
    public ResponseEntity<Plato> updatePlato(
            @PathVariable @Parameter(description = "ID del plato") Integer id,
            @RequestBody @Parameter(description = "Datos a actualizar") Plato plato) {
        return platoService.updatePlato(id, plato)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un plato", description = "Elimina un plato basado en su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Plato eliminado con éxito"),
        @ApiResponse(responseCode = "404", description = "Plato no encontrado")
    })
    public ResponseEntity<Void> deletePlato(
            @PathVariable @Parameter(description = "ID del plato") Integer id) {
        if (platoService.deletePlato(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
        }
}
	

