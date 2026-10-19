package modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;

public abstract class Producto {

    private static int contadorId = 0;

    @JsonIgnore
    private final int id;

    private String nombre;
    private double precioBase;

    protected Producto() {
        this.id = ++contadorId;
    }

    protected Producto(String nombre, double precioBase) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío");
        }
        if (precioBase < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        this.id = ++contadorId;
        this.nombre = nombre;
        this.precioBase = precioBase;
    }


    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío");
        }
        this.nombre = nombre;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(double precioBase) {
        if (precioBase < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        this.precioBase = precioBase;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Producto producto = (Producto) o;
        return id == producto.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s [id=%d, precio=%.2f€]", nombre, id, getPrecioBase());
    }
}
