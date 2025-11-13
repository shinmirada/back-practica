package com.example.demo;
import java.util.List;

public class PedidoRequestDTO {
    private String clienteDoc;
    private Boolean esDomicilio;
    private List<ItemPedidoDTO> items;

    // Constructores
    public PedidoRequestDTO() {}

    public PedidoRequestDTO(String clienteDoc, Boolean esDomicilio, List<ItemPedidoDTO> items) {
        this.clienteDoc = clienteDoc;
        this.esDomicilio = esDomicilio;
        this.items = items;
    }

    // Getters y Setters
    public String getClienteDoc() {
        return clienteDoc;
    }

    public void setClienteDoc(String clienteDoc) {
        this.clienteDoc = clienteDoc;
    }

    public Boolean getEsDomicilio() {
        return esDomicilio;
    }

    public void setEsDomicilio(Boolean esDomicilio) {
        this.esDomicilio = esDomicilio;
    }

    public List<ItemPedidoDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemPedidoDTO> items) {
        this.items = items;
    }
}
