package com.example.demo;



public class EstadoUpdateDTO {
    private Estado estado;

    // Constructor vacío (requerido por Jackson)
    public EstadoUpdateDTO() {}

    // Constructor con parámetro
    public EstadoUpdateDTO(Estado estado) {
        this.estado = estado;
    }

    // Getters y Setters
    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }
}
