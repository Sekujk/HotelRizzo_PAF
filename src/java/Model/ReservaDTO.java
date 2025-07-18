package Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

public class ReservaDTO {
    private int idReserva;
    private String numeroReserva;
    private int idCliente;
    private String nombreCliente; // Para mostrar en vistas
    private int idEmpleado;
    private String nombreEmpleado; // Para mostrar en vistas
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private int numHuespedes;
    
    // Check-in y Check-out
    private LocalDateTime fechaCheckin;
    private LocalDateTime fechaCheckout;
    private Integer empleadoCheckin;
    private Integer empleadoCheckout;
    private String nombreEmpleadoCheckin;
    private String nombreEmpleadoCheckout;
    
    // Estado y montos
    private String estado;
    private BigDecimal subtotal;
    private BigDecimal impuestos;
    private BigDecimal montoTotal;
    
    // Observaciones y fechas
    private String observaciones;
    private String motivoCancelacion;
    private LocalDateTime fechaCancelacion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor vacío
    public ReservaDTO() {
        this.subtotal = BigDecimal.ZERO;
        this.impuestos = BigDecimal.ZERO;
        this.montoTotal = BigDecimal.ZERO;
        this.estado = "Pendiente";
        this.numHuespedes = 1;
    }

    // Constructor con parámetros básicos
    public ReservaDTO(int idCliente, int idEmpleado, LocalDate fechaEntrada, LocalDate fechaSalida, int numHuespedes) {
        this();
        this.idCliente = idCliente;
        this.idEmpleado = idEmpleado;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.numHuespedes = numHuespedes;
    }

    // Getters y Setters
    public int getIdReserva() { return idReserva; }
    public void setIdReserva(int idReserva) { this.idReserva = idReserva; }

    public String getNumeroReserva() { return numeroReserva; }
    public void setNumeroReserva(String numeroReserva) { this.numeroReserva = numeroReserva; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public int getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(int idEmpleado) { this.idEmpleado = idEmpleado; }

    public String getNombreEmpleado() { return nombreEmpleado; }
    public void setNombreEmpleado(String nombreEmpleado) { this.nombreEmpleado = nombreEmpleado; }

    public LocalDate getFechaEntrada() { return fechaEntrada; }
    public void setFechaEntrada(LocalDate fechaEntrada) { this.fechaEntrada = fechaEntrada; }

    public LocalDate getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(LocalDate fechaSalida) { this.fechaSalida = fechaSalida; }

    public int getNumHuespedes() { return numHuespedes; }
    public void setNumHuespedes(int numHuespedes) { this.numHuespedes = numHuespedes; }

    public LocalDateTime getFechaCheckin() { return fechaCheckin; }
    public void setFechaCheckin(LocalDateTime fechaCheckin) { this.fechaCheckin = fechaCheckin; }

    public LocalDateTime getFechaCheckout() { return fechaCheckout; }
    public void setFechaCheckout(LocalDateTime fechaCheckout) { this.fechaCheckout = fechaCheckout; }

    public Integer getEmpleadoCheckin() { return empleadoCheckin; }
    public void setEmpleadoCheckin(Integer empleadoCheckin) { this.empleadoCheckin = empleadoCheckin; }

    public Integer getEmpleadoCheckout() { return empleadoCheckout; }
    public void setEmpleadoCheckout(Integer empleadoCheckout) { this.empleadoCheckout = empleadoCheckout; }

    public String getNombreEmpleadoCheckin() { return nombreEmpleadoCheckin; }
    public void setNombreEmpleadoCheckin(String nombreEmpleadoCheckin) { this.nombreEmpleadoCheckin = nombreEmpleadoCheckin; }

    public String getNombreEmpleadoCheckout() { return nombreEmpleadoCheckout; }
    public void setNombreEmpleadoCheckout(String nombreEmpleadoCheckout) { this.nombreEmpleadoCheckout = nombreEmpleadoCheckout; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getImpuestos() { return impuestos; }
    public void setImpuestos(BigDecimal impuestos) { this.impuestos = impuestos; }

    public BigDecimal getMontoTotal() { return montoTotal; }
    public void setMontoTotal(BigDecimal montoTotal) { this.montoTotal = montoTotal; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getMotivoCancelacion() { return motivoCancelacion; }
    public void setMotivoCancelacion(String motivoCancelacion) { this.motivoCancelacion = motivoCancelacion; }

    public LocalDateTime getFechaCancelacion() { return fechaCancelacion; }
    public void setFechaCancelacion(LocalDateTime fechaCancelacion) { this.fechaCancelacion = fechaCancelacion; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Métodos de utilidad
    public int getTotalNoches() {
        if (fechaEntrada != null && fechaSalida != null) {
            return (int) java.time.temporal.ChronoUnit.DAYS.between(fechaEntrada, fechaSalida);
        }
        return 0;
    }

    public boolean esCheckInHoy() {
        return fechaEntrada != null && fechaEntrada.equals(LocalDate.now());
    }

    public boolean esCheckOutHoy() {
        return fechaSalida != null && fechaSalida.equals(LocalDate.now());
    }

    public boolean estaVencida() {
        return fechaSalida != null && fechaSalida.isBefore(LocalDate.now()) && 
               !"CheckOut".equals(estado) && !"Cancelada".equals(estado);
    }

    @Override
    public String toString() {
        return "ReservaDTO{" +
                "idReserva=" + idReserva +
                ", numeroReserva='" + numeroReserva + '\'' +
                ", nombreCliente='" + nombreCliente + '\'' +
                ", fechaEntrada=" + fechaEntrada +
                ", fechaSalida=" + fechaSalida +
                ", estado='" + estado + '\'' +
                ", montoTotal=" + montoTotal +
                '}';
    }
}