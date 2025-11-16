package com.example.demo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "facturas")
public class Factura {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal total;

	@Column(nullable = false)
	private LocalDateTime fecha = LocalDateTime.now();

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pedido_id", nullable = false, unique = true)
	@JsonIgnoreProperties({ "items", "cliente", "factura" })
	private Pedido pedido;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_doc", nullable = false)
	@JsonIgnoreProperties({ "pedidos", "contraseña" })
	private Usuario usuario;

	// Constructores
	public Factura() {
	}

	public Factura(Long id, BigDecimal total, LocalDateTime fecha, Pedido pedido, Usuario usuario) {
		this.id = id;
		this.total = total;
		this.fecha = fecha;
		this.pedido = pedido;
		this.usuario = usuario;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public LocalDateTime getFecha() {
		return fecha;
	}

	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}

	public Pedido getPedido() {
		return pedido;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
}