package modelo;

import java.util.ArrayList;
import java.util.List;

public class Pizza extends Producto {

    private Tamano tamano;
    private final List<modelo.Ingrediente> ingredientes;

    public Pizza(String nombre, double precioBase, Tamano tamano) {
        super(nombre, precioBase);
        this.tamano = tamano;
        this.ingredientes = new ArrayList<>();
    }

    public Pizza(String nombre, double precioBase, Tamano tamano, List<modelo.Ingrediente> ingredientes) {
        this(nombre, precioBase, tamano);
        if (ingredientes != null) {
            this.ingredientes.addAll(ingredientes);
        }
    }

    public void addIngrediente(modelo.Ingrediente ingrediente) {
        if (ingrediente != null) {
            ingredientes.add(ingrediente);
        }
    }

    public Tamano getTamano() {
        return tamano;
    }

    public void setTamano(Tamano tamano) {
        this.tamano = tamano;
    }

    public List<modelo.Ingrediente> getIngredientes() {
        return new ArrayList<>(ingredientes);
    }

    @Override
    public String toString() {
        return String.format("Pizza %s (%s) precio = %.2f€", getNombre(), tamano, getPrecioBase());
    }
}
