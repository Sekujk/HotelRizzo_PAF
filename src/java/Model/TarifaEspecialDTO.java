package Model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TarifaEspecialDTO {
    private int idTarifa;
    private int idTipoHabitacion;
    private String nombreTipoHabitacion; // NUEVO
    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private double precioEspecial;
    private String tipoTarifa;
    private boolean activo;
    private LocalDateTime createdAt;

    // Getters y Setters
    public int getIdTarifa() { return idTarifa; }
    public void setIdTarifa(int idTarifa) { this.idTarifa = idTarifa; }

    public int getIdTipoHabitacion() { return idTipoHabitacion; }
    public void setIdTipoHabitacion(int idTipoHabitacion) { this.idTipoHabitacion = idTipoHabitacion; }

    public String getNombreTipoHabitacion() { return nombreTipoHabitacion; }
    public void setNombreTipoHabitacion(String nombreTipoHabitacion) { this.nombreTipoHabitacion = nombreTipoHabitacion; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public double getPrecioEspecial() { return precioEspecial; }
    public void setPrecioEspecial(double precioEspecial) { this.precioEspecial = precioEspecial; }

    public String getTipoTarifa() { return tipoTarifa; }
    public void setTipoTarifa(String tipoTarifa) { this.tipoTarifa = tipoTarifa; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
