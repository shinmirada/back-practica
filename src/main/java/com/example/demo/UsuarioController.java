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
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "API para la gestión de usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    @Operation(summary = "Obtener todos los usuarios", description = "Devuelve una lista de todos los usuarios existentes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida con éxito"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        return ResponseEntity.ok(usuarioService.findAllUsuarios());
    }

    @GetMapping("/documento/{documento}")
    @Operation(summary = "Obtener usuario por documento", description = "Devuelve un usuario específico basado en su documento")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Usuario> getUsuarioByDocumento(
            @PathVariable @Parameter(description = "Documento del usuario") String documento) {
        return usuarioService.findByDocumento(documento)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nombreUsuario/{username}")
    @Operation(summary = "Obtener usuario por nombre de usuario", description = "Devuelve un usuario específico basado en su nombre de usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Usuario> getUsuarioByUsername(
            @PathVariable @Parameter(description = "Nombre de usuario") String username) {
        return usuarioService.findByUsuario(username)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/rol/{rol}")
    @Operation(summary = "Obtener usuarios por rol", description = "Devuelve una lista de usuarios según su rol")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuarios encontrados"),
        @ApiResponse(responseCode = "404", description = "No hay usuarios con ese rol")
    })
    public ResponseEntity<List<Usuario>> getUsuariosByRol(
            @PathVariable @Parameter(description = "Rol del usuario") Rol rol) {
        List<Usuario> usuarios = usuarioService.findByRol(rol);
        if (usuarios.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuarios);
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo usuario", description = "Crea un nuevo usuario con los datos proporcionados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario creado con éxito"),
        @ApiResponse(responseCode = "409", description = "Conflicto - El usuario ya existe")
    })
    public ResponseEntity<Usuario> createUsuario(
            @RequestBody @Parameter(description = "Datos del usuario a crear") Usuario usuario) {
        Usuario newUsuario = usuarioService.saveUsuario(usuario);
        if (newUsuario == null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(newUsuario, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Inicio de sesión", description = "Inicia sesión con usuario y contraseña")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login exitoso"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    public ResponseEntity<Usuario> login(
            @RequestBody @Parameter(description = "Credenciales (usuario y contraseña)") Map<String, String> credentials) {
        String username = credentials.get("usuario");
        String password = credentials.get("contraseña");
        
        Usuario usuario = usuarioService.login(username, password);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
}
