package com.example.demo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class PedidoResponseDTO {
    private Integer id;
    private LocalDateTime fecha;
    private Boolean esDomicilio;
    private Estado estado;
    private String clienteDoc;
    private String nombrePlato; 

 
    public PedidoResponseDTO() {}


    public PedidoResponseDTO(Pedido pedido) {
        this.id = pedido.getId();
        this.fecha = pedido.getFecha();
        this.esDomicilio = pedido.getEsDomicilio();
        this.estado = pedido.getEstado();
        this.clienteDoc = pedido.getCliente().getDocumento();
        
     
        if (pedido.getItems() != null && !pedido.getItems().isEmpty()) {
            List<String> platos = pedido.getItems().stream()
                .map(item -> item.getPlato().getNombre())
                .collect(Collectors.toList());
            this.nombrePlato = String.join(", ", platos);
        } else {
            this.nombrePlato = "Sin plato";
        }
    }


    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    
    public Boolean getEsDomicilio() { return esDomicilio; }
    public void setEsDomicilio(Boolean esDomicilio) { this.esDomicilio = esDomicilio; }
    
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    
    public String getClienteDoc() { return clienteDoc; }
    public void setClienteDoc(String clienteDoc) { this.clienteDoc = clienteDoc; }
    
    public String getNombrePlato() { return nombrePlato; }
    public void setNombrePlato(String nombrePlato) { this.nombrePlato = nombrePlato; }
}