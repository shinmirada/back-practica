package com.example.demo;

import java.math.BigDecimal;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "item_pedido")
public class ItemPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    @JsonBackReference 
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.EAGER)  // ← Cambiar a EAGER para que cargue el plato
    @JoinColumn(name = "plato_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})  // ← AGREGAR ESTO
    private Plato plato;

    // Constructores, getters y setters...
    public ItemPedido() {}

    public ItemPedido(Integer cantidad, BigDecimal precioUnitario, Pedido pedido, Plato plato) {
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.pedido = pedido;
        this.plato = plato;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }
    
    public Plato getPlato() { return plato; }
    public void setPlato(Plato plato) { this.plato = plato; }
}