package com.restaurante.model;

import java.time.LocalDateTime;

public class Pedido {
    private int id;
    private int idMesa;
    private int idUsuario;
    private String estado;
    private LocalDateTime fechaApertura;
    private LocalDateTime fechaCierre;
    private double total;

    public Pedido() {}

    public Pedido(int id, int idMesa, int idUsuario, String estado, LocalDateTime fechaApertura, double total) {
        this.id = id;
        this.idMesa = idMesa;
        this.idUsuario = idUsuario;
        this.estado = estado;
        this.fechaApertura = fechaApertura;
        this.total = total;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdMesa() { return idMesa; }
    public void setIdMesa(int idMesa) { this.idMesa = idMesa; }
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getFechaApertura() { return fechaApertura; }
    public void setFechaApertura(LocalDateTime fechaApertura) { this.fechaApertura = fechaApertura; }
    public LocalDateTime getFechaCierre() { return fechaCierre; }
    public void setFechaCierre(LocalDateTime fechaCierre) { this.fechaCierre = fechaCierre; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}