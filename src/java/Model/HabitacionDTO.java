package Model;

import java.time.LocalDateTime;

public class HabitacionDTO {
    private int idHabitacion;
    private String numero;
    private int piso;
    private int idTipo;
    private String nombreTipo; // NUEVO
    private String estado;
    private String observaciones;
    private boolean activo;
    private LocalDateTime createdAt;

    // Getters y Setters
    public int getIdHabitacion() { return idHabitacion; }
    public void setIdHabitacion(int idHabitacion) { this.idHabitacion = idHabitacion; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public int getPiso() { return piso; }
    public void setPiso(int piso) { this.piso = piso; }

    public int getIdTipo() { return idTipo; }
    public void setIdTipo(int idTipo) { this.idTipo = idTipo; }

    public String getNombreTipo() { return nombreTipo; }
    public void setNombreTipo(String nombreTipo) { this.nombreTipo = nombreTipo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
