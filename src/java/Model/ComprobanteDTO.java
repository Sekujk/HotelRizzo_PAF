package Model;

import java.time.LocalDateTime;
import java.math.BigDecimal;

public class ComprobanteDTO {
    private int idComprobante;
    private int idReserva;
    private String numeroReserva; // Para mostrar en vistas
    private String tipoComprobante; // Boleta, Factura
    private String serie; // B001, F001
    private int numero;
    private String numeroCompleto; // Se calculará con trigger
    
    // Datos del cliente
    private String rucCliente; // Solo para facturas
    private String razonSocial; // Solo para facturas
    private String direccionCliente;
    
    // Montos
    private BigDecimal subtotal;
    private BigDecimal impuesto;
    private BigDecimal total;
    
    // Control
    private LocalDateTime fechaEmision;
    private int idEmpleado;
    private String nombreEmpleado; // Para mostrar en vistas
    private String estado; // Emitido, Anulado
    private String motivoAnulacion;
    private LocalDateTime fechaAnulacion;
    private String observaciones;

    // Constructor vacío
    public ComprobanteDTO() {
        this.subtotal = BigDecimal.ZERO;
        this.impuesto = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
        this.fechaEmision = LocalDateTime.now();
        this.estado = "Emitido";
        this.tipoComprobante = "Boleta";
        this.serie = "B001";
    }

    // Constructor con parámetros básicos
    public ComprobanteDTO(int idReserva, String tipoComprobante, BigDecimal subtotal, 
                         BigDecimal impuesto, int idEmpleado) {
        this();
        this.idReserva = idReserva;
        this.tipoComprobante = tipoComprobante;
        this.subtotal = subtotal;
        this.impuesto = impuesto;
        this.idEmpleado = idEmpleado;
        calcularTotal();
        establecerSerie();
    }

    // Getters y Setters
    public int getIdComprobante() { return idComprobante; }
    public void setIdComprobante(int idComprobante) { this.idComprobante = idComprobante; }

    public int getIdReserva() { return idReserva; }
    public void setIdReserva(int idReserva) { this.idReserva = idReserva; }

    public String getNumeroReserva() { return numeroReserva; }
    public void setNumeroReserva(String numeroReserva) { this.numeroReserva = numeroReserva; }

    public String getTipoComprobante() { return tipoComprobante; }
    public void setTipoComprobante(String tipoComprobante) { 
        this.tipoComprobante = tipoComprobante; 
        establecerSerie();
    }

    public String getSerie() { return serie; }
    public void setSerie(String serie) { this.serie = serie; }

    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    public String getNumeroCompleto() { return numeroCompleto; }
    public void setNumeroCompleto(String numeroCompleto) { this.numeroCompleto = numeroCompleto; }

    public String getRucCliente() { return rucCliente; }
    public void setRucCliente(String rucCliente) { this.rucCliente = rucCliente; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getDireccionCliente() { return direccionCliente; }
    public void setDireccionCliente(String direccionCliente) { this.direccionCliente = direccionCliente; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { 
        this.subtotal = subtotal; 
        calcularTotal();
    }

    public BigDecimal getImpuesto() { return impuesto; }
    public void setImpuesto(BigDecimal impuesto) { 
        this.impuesto = impuesto; 
        calcularTotal();
    }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDateTime fechaEmision) { this.fechaEmision = fechaEmision; }

    public int getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(int idEmpleado) { this.idEmpleado = idEmpleado; }

    public String getNombreEmpleado() { return nombreEmpleado; }
    public void setNombreEmpleado(String nombreEmpleado) { this.nombreEmpleado = nombreEmpleado; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getMotivoAnulacion() { return motivoAnulacion; }
    public void setMotivoAnulacion(String motivoAnulacion) { this.motivoAnulacion = motivoAnulacion; }

    public LocalDateTime getFechaAnulacion() { return fechaAnulacion; }
    public void setFechaAnulacion(LocalDateTime fechaAnulacion) { this.fechaAnulacion = fechaAnulacion; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    // Métodos de utilidad
    private void calcularTotal() {
        if (subtotal != null && impuesto != null) {
            this.total = subtotal.add(impuesto);
        } else {
            this.total = BigDecimal.ZERO;
        }
    }

    private void establecerSerie() {
        if ("Factura".equalsIgnoreCase(tipoComprobante)) {
            this.serie = "F001";
        } else {
            this.serie = "B001";
        }
    }

    public void recalcularTotal() {
        calcularTotal();
    }

    public boolean esBoleta() {
        return "Boleta".equalsIgnoreCase(tipoComprobante);
    }

    public boolean esFactura() {
        return "Factura".equalsIgnoreCase(tipoComprobante);
    }

    public boolean estaEmitido() {
        return "Emitido".equalsIgnoreCase(estado);
    }

    public boolean estaAnulado() {
        return "Anulado".equalsIgnoreCase(estado);
    }

    public boolean esValidoParaFactura() {
        return esFactura() && rucCliente != null && !rucCliente.trim().isEmpty() &&
               razonSocial != null && !razonSocial.trim().isEmpty();
    }

    public boolean esEmisionHoy() {
        if (fechaEmision == null) return false;
        return fechaEmision.toLocalDate().equals(java.time.LocalDate.now());
    }

    public void calcularImpuesto(BigDecimal porcentajeImpuesto) {
        if (subtotal != null && porcentajeImpuesto != null) {
            this.impuesto = subtotal.multiply(porcentajeImpuesto).divide(BigDecimal.valueOf(100));
            calcularTotal();
        }
    }

    // Método para generar número completo manualmente si no se usa trigger
    public void generarNumeroCompleto() {
        if (serie != null && numero > 0) {
            this.numeroCompleto = serie + "-" + String.format("%08d", numero);
        }
    }

    @Override
    public String toString() {
        return "ComprobanteDTO{" +
                "idComprobante=" + idComprobante +
                ", numeroCompleto='" + numeroCompleto + '\'' +
                ", tipoComprobante='" + tipoComprobante + '\'' +
                ", numeroReserva='" + numeroReserva + '\'' +
                ", total=" + total +
                ", estado='" + estado + '\'' +
                ", fechaEmision=" + fechaEmision +
                '}';
    }
}