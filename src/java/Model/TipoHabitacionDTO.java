package Model;

import java.time.LocalDateTime;

public class TipoHabitacionDTO {
    private int idTipo;
    private String nombre;
    private String descripcion;
    private double precioBase;
    private int capacidadPersonas;
    private boolean activo;
    private LocalDateTime createdAt;

    // Constructor vacío
    public TipoHabitacionDTO() {
        this.activo = true;
        this.createdAt = LocalDateTime.now();
        this.capacidadPersonas = 2;
    }

    // Constructor con parámetros básicos
    public TipoHabitacionDTO(String nombre, String descripcion, double precioBase, int capacidadPersonas) {
        this();
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioBase = precioBase;
        this.capacidadPersonas = capacidadPersonas;
    }

    // Getters y Setters
    public int getIdTipo() { return idTipo; }
    public void setIdTipo(int idTipo) { this.idTipo = idTipo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getPrecioBase() { return precioBase; }
    public void setPrecioBase(double precioBase) { this.precioBase = precioBase; }

    public int getCapacidadPersonas() { return capacidadPersonas; }
    public void setCapacidadPersonas(int capacidadPersonas) { this.capacidadPersonas = capacidadPersonas; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Métodos de utilidad
    public String getPrecioFormateado() {
        return String.format("S/. %.2f", precioBase);
    }

    public String getCapacidadTexto() {
        return capacidadPersonas + (capacidadPersonas == 1 ? " persona" : " personas");
    }

    public boolean puedeAlbergar(int numeroHuespedes) {
        return numeroHuespedes <= capacidadPersonas;
    }

    @Override
    public String toString() {
        return "TipoHabitacionDTO{" +
                "idTipo=" + idTipo +
                ", nombre='" + nombre + '\'' +
                ", precioBase=" + precioBase +
                ", capacidadPersonas=" + capacidadPersonas +
                ", activo=" + activo +
                '}';
    }
}