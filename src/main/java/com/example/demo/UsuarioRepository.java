package com.example.demo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    
    Optional<Usuario> findByUsuario(String usuario);
    
    List<Usuario> findByRol(Rol rol);
    
    boolean existsByUsuario(String usuario);
}