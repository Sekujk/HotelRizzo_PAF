package Model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.math.BigDecimal;

public class ConsumoServicioDTO {
    private int idConsumo;
    private int idReserva;
    private String numeroReserva; // Para mostrar en vistas
    private int idServicio;
    private String nombreServicio; // Para mostrar en vistas
    private BigDecimal cantidad; // Puede ser decimal para servicios como horas de spa
    private BigDecimal precioUnitario;
    private BigDecimal subtotal; // Campo calculado
    private LocalDate fechaConsumo;
    private LocalTime horaConsumo;
    private int idEmpleado;
    private String nombreEmpleado; // Para mostrar en vistas
    private String observaciones;

    // Constructor vacío
    public ConsumoServicioDTO() {
        this.cantidad = BigDecimal.ONE;
        this.precioUnitario = BigDecimal.ZERO;
        this.subtotal = BigDecimal.ZERO;
        this.fechaConsumo = LocalDate.now();
        this.horaConsumo = LocalTime.now();
    }

    // Constructor con parámetros básicos
    public ConsumoServicioDTO(int idReserva, int idServicio, BigDecimal cantidad, 
                             BigDecimal precioUnitario, int idEmpleado) {
        this();
        this.idReserva = idReserva;
        this.idServicio = idServicio;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.idEmpleado = idEmpleado;
        calcularSubtotal();
    }

    // Getters y Setters
    public int getIdConsumo() { return idConsumo; }
    public void setIdConsumo(int idConsumo) { this.idConsumo = idConsumo; }

    public int getIdReserva() { return idReserva; }
    public void setIdReserva(int idReserva) { this.idReserva = idReserva; }

    public String getNumeroReserva() { return numeroReserva; }
    public void setNumeroReserva(String numeroReserva) { this.numeroReserva = numeroReserva; }

    public int getIdServicio() { return idServicio; }
    public void setIdServicio(int idServicio) { this.idServicio = idServicio; }

    public String getNombreServicio() { return nombreServicio; }
    public void setNombreServicio(String nombreServicio) { this.nombreServicio = nombreServicio; }

    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) { 
        this.cantidad = cantidad; 
        calcularSubtotal();
    }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { 
        this.precioUnitario = precioUnitario; 
        calcularSubtotal();
    }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public LocalDate getFechaConsumo() { return fechaConsumo; }
    public void setFechaConsumo(LocalDate fechaConsumo) { this.fechaConsumo = fechaConsumo; }

    public LocalTime getHoraConsumo() { return horaConsumo; }
    public void setHoraConsumo(LocalTime horaConsumo) { this.horaConsumo = horaConsumo; }

    public int getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(int idEmpleado) { this.idEmpleado = idEmpleado; }

    public String getNombreEmpleado() { return nombreEmpleado; }
    public void setNombreEmpleado(String nombreEmpleado) { this.nombreEmpleado = nombreEmpleado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    // Método para calcular subtotal automáticamente
    private void calcularSubtotal() {
        if (precioUnitario != null && cantidad != null && 
            cantidad.compareTo(BigDecimal.ZERO) > 0) {
            this.subtotal = precioUnitario.multiply(cantidad);
        } else {
            this.subtotal = BigDecimal.ZERO;
        }
    }

    // Método público para forzar recálculo
    public void recalcularSubtotal() {
        calcularSubtotal();
    }

    // Validaciones
    public boolean esValidaCantidad() {
        return cantidad != null && cantidad.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean esValidoPrecio() {
        return precioUnitario != null && precioUnitario.compareTo(BigDecimal.ZERO) >= 0;
    }

    public boolean esFechaHoy() {
        return fechaConsumo != null && fechaConsumo.equals(LocalDate.now());
    }

    // Método para establecer cantidad como entero (conveniencia)
    public void setCantidadInt(int cantidad) {
        this.setCantidad(BigDecimal.valueOf(cantidad));
    }

    // Método para obtener cantidad como entero (conveniencia)
    public int getCantidadInt() {
        return cantidad != null ? cantidad.intValue() : 0;
    }

    @Override
    public String toString() {
        return "ConsumoServicioDTO{" +
                "idConsumo=" + idConsumo +
                ", numeroReserva='" + numeroReserva + '\'' +
                ", nombreServicio='" + nombreServicio + '\'' +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", subtotal=" + subtotal +
                ", fechaConsumo=" + fechaConsumo +
                '}';
    }
}