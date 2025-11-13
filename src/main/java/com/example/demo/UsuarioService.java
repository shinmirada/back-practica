package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Inicializar datos de prueba
    @PostConstruct
    public void init() {
        if (usuarioRepository.count() == 0) {
            // Admin
            Usuario admin = new Usuario("Admin", "114", "309975", "hola3", "Admin", 
                                       passwordEncoder.encode("4"), Rol.ADMIN);
            usuarioRepository.save(admin);

            // Meseros
            Usuario mesero1 = new Usuario("Juan Pérez", "111", "302345", "hola", "Mesero1", 
                                         passwordEncoder.encode("1"), Rol.MESERO);
            Usuario mesero2 = new Usuario("Maria Lopez", "112", "303678", "hola1", "Mesero2", 
                                         passwordEncoder.encode("2"), Rol.MESERO);
            Usuario mesero3 = new Usuario("Carlos Ruiz", "113", "309874", "hola2", "Mesero3", 
                                         passwordEncoder.encode("3"), Rol.MESERO);
            
            usuarioRepository.save(mesero1);
            usuarioRepository.save(mesero2);
            usuarioRepository.save(mesero3);
        }
    }

    public List<Usuario> findAllUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> findByDocumento(String documento) {
        return usuarioRepository.findById(documento);
    }

    public Optional<Usuario> findByUsuario(String username) {
        return usuarioRepository.findByUsuario(username);
    }

    public List<Usuario> findByRol(Rol rol) {
        return usuarioRepository.findByRol(rol);
    }

    public Usuario saveUsuario(Usuario usuario) {
        // Verificar si el nombre de usuario ya existe
        if (usuarioRepository.existsByUsuario(usuario.getUsuario())) {
            return null;
        }
        
        // Encriptar contraseña
        usuario.setContraseña(passwordEncoder.encode(usuario.getContraseña()));
        
        // Asignar rol CLIENTE por defecto si no tiene
        if (usuario.getRol() == null) {
            usuario.setRol(Rol.CLIENTE);
        }
        
        return usuarioRepository.save(usuario);
    }

    public Usuario login(String username, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsuario(username);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (passwordEncoder.matches(password, usuario.getContraseña())) {
                return usuario;
            }
        }
        return null;
    }
}