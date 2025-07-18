package Model;

import java.time.LocalDate;
import java.math.BigDecimal;

public class ReservaHabitacionDTO {
    private int idDetalle;
    private int idReserva;
    private int idHabitacion;
    private String numeroHabitacion; // Para mostrar en vistas
    private String nombreTipoHabitacion; // Para mostrar en vistas
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private BigDecimal precioNoche;
    private int totalNoches; // Campo calculado
    private BigDecimal subtotal; // Campo calculado
    private String observaciones;

    // Constructor vacío
    public ReservaHabitacionDTO() {
        this.precioNoche = BigDecimal.ZERO;
        this.subtotal = BigDecimal.ZERO;
    }

    // Constructor con parámetros básicos
    public ReservaHabitacionDTO(int idReserva, int idHabitacion, LocalDate fechaEntrada, 
                               LocalDate fechaSalida, BigDecimal precioNoche) {
        this();
        this.idReserva = idReserva;
        this.idHabitacion = idHabitacion;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.precioNoche = precioNoche;
        calcularTotales();
    }

    // Getters y Setters
    public int getIdDetalle() { return idDetalle; }
    public void setIdDetalle(int idDetalle) { this.idDetalle = idDetalle; }

    public int getIdReserva() { return idReserva; }
    public void setIdReserva(int idReserva) { this.idReserva = idReserva; }

    public int getIdHabitacion() { return idHabitacion; }
    public void setIdHabitacion(int idHabitacion) { this.idHabitacion = idHabitacion; }

    public String getNumeroHabitacion() { return numeroHabitacion; }
    public void setNumeroHabitacion(String numeroHabitacion) { this.numeroHabitacion = numeroHabitacion; }

    public String getNombreTipoHabitacion() { return nombreTipoHabitacion; }
    public void setNombreTipoHabitacion(String nombreTipoHabitacion) { this.nombreTipoHabitacion = nombreTipoHabitacion; }

    public LocalDate getFechaEntrada() { return fechaEntrada; }
    public void setFechaEntrada(LocalDate fechaEntrada) { 
        this.fechaEntrada = fechaEntrada; 
        calcularTotales();
    }

    public LocalDate getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(LocalDate fechaSalida) { 
        this.fechaSalida = fechaSalida; 
        calcularTotales();
    }

    public BigDecimal getPrecioNoche() { return precioNoche; }
    public void setPrecioNoche(BigDecimal precioNoche) { 
        this.precioNoche = precioNoche; 
        calcularTotales();
    }

    public int getTotalNoches() { return totalNoches; }
    public void setTotalNoches(int totalNoches) { this.totalNoches = totalNoches; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    // Método para calcular totales automáticamente
    private void calcularTotales() {
        if (fechaEntrada != null && fechaSalida != null && precioNoche != null) {
            this.totalNoches = (int) java.time.temporal.ChronoUnit.DAYS.between(fechaEntrada, fechaSalida);
            this.subtotal = precioNoche.multiply(BigDecimal.valueOf(totalNoches));
        } else {
            this.totalNoches = 0;
            this.subtotal = BigDecimal.ZERO;
        }
    }

    // Método público para forzar recálculo
    public void recalcularTotales() {
        calcularTotales();
    }

    // Validaciones
    public boolean esValidaRangoFechas() {
        return fechaEntrada != null && fechaSalida != null && fechaSalida.isAfter(fechaEntrada);
    }

    public boolean esFechaEntradaFutura() {
        return fechaEntrada != null && fechaEntrada.isAfter(LocalDate.now());
    }

    @Override
    public String toString() {
        return "ReservaHabitacionDTO{" +
                "idDetalle=" + idDetalle +
                ", idReserva=" + idReserva +
                ", numeroHabitacion='" + numeroHabitacion + '\'' +
                ", fechaEntrada=" + fechaEntrada +
                ", fechaSalida=" + fechaSalida +
                ", totalNoches=" + totalNoches +
                ", subtotal=" + subtotal +
                '}';
    }
}