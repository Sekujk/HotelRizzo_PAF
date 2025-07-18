package Model;

import java.math.BigDecimal;

public class HabitacionDetalleDTO {
    private int idDetalle;
    private int idHabitacion;
    private String numeroHabitacion;
    private String tipoHabitacion;
    private double precioNoche;
    private int capacidadPersonas;
    private String descripcionTipo;
    
    // Campos adicionales necesarios para el servlet
    private String estadoHabitacion;
    private int totalNoches;
    private BigDecimal subtotal;
    private String observaciones;
    
    // Getters y Setters existentes
    public int getIdDetalle() { return idDetalle; }
    public void setIdDetalle(int idDetalle) { this.idDetalle = idDetalle; }
    
    public int getIdHabitacion() { return idHabitacion; }
    public void setIdHabitacion(int idHabitacion) { this.idHabitacion = idHabitacion; }
    
    public String getNumeroHabitacion() { return numeroHabitacion; }
    public void setNumeroHabitacion(String numeroHabitacion) { this.numeroHabitacion = numeroHabitacion; }
    
    public String getTipoHabitacion() { return tipoHabitacion; }
    public void setTipoHabitacion(String tipoHabitacion) { this.tipoHabitacion = tipoHabitacion; }
    
    public double getPrecioNoche() { return precioNoche; }
    public void setPrecioNoche(double precioNoche) { this.precioNoche = precioNoche; }
    
    public int getCapacidadPersonas() { return capacidadPersonas; }
    public void setCapacidadPersonas(int capacidadPersonas) { this.capacidadPersonas = capacidadPersonas; }
    
    public String getDescripcionTipo() { return descripcionTipo; }
    public void setDescripcionTipo(String descripcionTipo) { this.descripcionTipo = descripcionTipo; }
    
    // Método de compatibilidad
    public String getDescripcion() { return descripcionTipo; }
    public void setDescripcion(String descripcion) { this.descripcionTipo = descripcion; }
    
    // NUEVOS GETTERS Y SETTERS NECESARIOS:
    
    public String getEstadoHabitacion() { return estadoHabitacion; }
    public void setEstadoHabitacion(String estadoHabitacion) { this.estadoHabitacion = estadoHabitacion; }
    
    public int getTotalNoches() { return totalNoches; }
    public void setTotalNoches(int totalNoches) { this.totalNoches = totalNoches; }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}