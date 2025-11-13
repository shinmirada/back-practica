package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PlatoService {

    @Autowired
    private PlatoRepository platoRepository;

    // Inicializar datos de prueba
    @PostConstruct
    public void init() {
        if (platoRepository.count() == 0) {
            platoRepository.save(new Plato("Sushi clasico", 
                "Nigiri de salmón, makis de atún, roll california", new BigDecimal("25000")));
            platoRepository.save(new Plato("Ramen especial", 
                "Ramen de miso con cerdo chashu", new BigDecimal("25000")));
            platoRepository.save(new Plato("Bento Teriyaki", 
                "Pollo teriyaki, arroz blanco, ensalada de algas", new BigDecimal("20000")));
            platoRepository.save(new Plato("Tempura mixto", 
                "Langostinos tempura, verduras tempura, salsa tentsuyu", new BigDecimal("20000")));
            platoRepository.save(new Plato("Udon tradicional", 
                "Sopa de udon con dashi, tofu frito, kamaboko", new BigDecimal("25000")));
            platoRepository.save(new Plato("Yakisoba", 
                "Tallarines fritos con vegetales, cerdo o pollo, salsa yakisoba", new BigDecimal("15000")));
        }
    }

    public List<Plato> getAllPlatos() {
        return platoRepository.findAll();
    }

    public Optional<Plato> findById(Integer id) {
        return platoRepository.findById(id);
    }

    public Plato savePlato(Plato plato) {
        return platoRepository.save(plato);
    }

    public Optional<Plato> updatePlato(Integer id, Plato platoDetails) {
        return platoRepository.findById(id).map(plato -> {
            plato.setDescripcion(platoDetails.getDescripcion());
            plato.setPrecio(platoDetails.getPrecio());
            return platoRepository.save(plato);
        });
    }

    public boolean deletePlato(Integer id) {
        if (platoRepository.existsById(id)) {
            platoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}