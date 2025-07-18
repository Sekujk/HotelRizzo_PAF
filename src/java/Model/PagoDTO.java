package Model;

import java.time.LocalDateTime;
import java.math.BigDecimal;

public class PagoDTO {
    private int idPago;
    private String numeroComprobante;
    private int idReserva;
    private String numeroReserva; // Para mostrar en vistas
    private BigDecimal monto;
    private String metodoPago; // Efectivo, Tarjeta, Transferencia, Yape, Plin
    private String numeroOperacion; // Para transferencias
    private LocalDateTime fechaPago;
    private int idEmpleado;
    private String nombreEmpleado; // Para mostrar en vistas
    private String tipoPago; // Pago, Anticipo, Saldo
    private String estado; // Completado, Pendiente, Anulado
    private String observaciones;

    // Constructor vacío
    public PagoDTO() {
        this.monto = BigDecimal.ZERO;
        this.fechaPago = LocalDateTime.now();
        this.tipoPago = "Pago";
        this.estado = "Completado";
        this.metodoPago = "Efectivo";
    }

    // Constructor con parámetros básicos
    public PagoDTO(int idReserva, BigDecimal monto, String metodoPago, int idEmpleado) {
        this();
        this.idReserva = idReserva;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.idEmpleado = idEmpleado;
    }

    // Getters y Setters
    public int getIdPago() { return idPago; }
    public void setIdPago(int idPago) { this.idPago = idPago; }

    public String getNumeroComprobante() { return numeroComprobante; }
    public void setNumeroComprobante(String numeroComprobante) { this.numeroComprobante = numeroComprobante; }

    public int getIdReserva() { return idReserva; }
    public void setIdReserva(int idReserva) { this.idReserva = idReserva; }

    public String getNumeroReserva() { return numeroReserva; }
    public void setNumeroReserva(String numeroReserva) { this.numeroReserva = numeroReserva; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public String getNumeroOperacion() { return numeroOperacion; }
    public void setNumeroOperacion(String numeroOperacion) { this.numeroOperacion = numeroOperacion; }

    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }

    public int getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(int idEmpleado) { this.idEmpleado = idEmpleado; }

    public String getNombreEmpleado() { return nombreEmpleado; }
    public void setNombreEmpleado(String nombreEmpleado) { this.nombreEmpleado = nombreEmpleado; }

    public String getTipoPago() { return tipoPago; }
    public void setTipoPago(String tipoPago) { this.tipoPago = tipoPago; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    // Métodos de utilidad
    public boolean esEfectivo() {
        return "Efectivo".equalsIgnoreCase(metodoPago);
    }

    public boolean esTarjeta() {
        return "Tarjeta".equalsIgnoreCase(metodoPago);
    }

    public boolean esTransferencia() {
        return "Transferencia".equalsIgnoreCase(metodoPago);
    }

    public boolean esDigital() {
        return "Yape".equalsIgnoreCase(metodoPago) || "Plin".equalsIgnoreCase(metodoPago);
    }

    public boolean esAnticipo() {
        return "Anticipo".equalsIgnoreCase(tipoPago);
    }

    public boolean esPagoCompleto() {
        return "Pago".equalsIgnoreCase(tipoPago);
    }

    public boolean esSaldo() {
        return "Saldo".equalsIgnoreCase(tipoPago);
    }

    public boolean estaCompletado() {
        return "Completado".equalsIgnoreCase(estado);
    }

    public boolean estaPendiente() {
        return "Pendiente".equalsIgnoreCase(estado);
    }

    public boolean estaAnulado() {
        return "Anulado".equalsIgnoreCase(estado);
    }

    public boolean requiereOperacion() {
        return esTransferencia() || esDigital();
    }

    public boolean esValidoMonto() {
        return monto != null && monto.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean esPagoHoy() {
        if (fechaPago == null) return false;
        return fechaPago.toLocalDate().equals(java.time.LocalDate.now());
    }

    @Override
    public String toString() {
        return "PagoDTO{" +
                "idPago=" + idPago +
                ", numeroComprobante='" + numeroComprobante + '\'' +
                ", numeroReserva='" + numeroReserva + '\'' +
                ", monto=" + monto +
                ", metodoPago='" + metodoPago + '\'' +
                ", tipoPago='" + tipoPago + '\'' +
                ", estado='" + estado + '\'' +
                ", fechaPago=" + fechaPago +
                '}';
    }
}