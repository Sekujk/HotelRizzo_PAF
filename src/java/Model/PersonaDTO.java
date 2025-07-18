package Model;

import java.util.Date;

public class PersonaDTO {
    private int id;
    private String nombre;
    private String apellido;
    private String dni;
    private String telefono;
    private String correo;
    private String direccion;
    private Date fechaNacimiento;
    private String genero;
    private boolean activo;
    private Date createdAt;

    // Constructor vacío
    public PersonaDTO() {
        this.activo = true;
        this.createdAt = new Date();
    }

    // Constructor con parámetros básicos
    public PersonaDTO(String nombre, String apellido, String dni) {
        this();
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdPersona() { return id; }
    public void setIdPersona(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public Date getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(Date fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    // Métodos de utilidad
    public String getNombreCompleto() {
        return (nombre != null ? nombre : "") + " " + (apellido != null ? apellido : "");
    }

    public boolean esValido() {
        return nombre != null && !nombre.trim().isEmpty() &&
               apellido != null && !apellido.trim().isEmpty() &&
               dni != null && !dni.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "PersonaDTO{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", dni='" + dni + '\'' +
                ", activo=" + activo +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PersonaDTO persona = (PersonaDTO) obj;
        return dni != null && dni.equals(persona.dni);
    }

    @Override
    public int hashCode() {
        return dni != null ? dni.hashCode() : 0;
    }
}