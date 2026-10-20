package modelo;

import java.util.ArrayList;
import java.util.List;

public class Pasta extends Producto {

    private List<modelo.Ingrediente> ingredientes;

    public Pasta() { this.ingredientes = new ArrayList<>(); }

    public Pasta(String nombre, double precioBase) {
        super(nombre, precioBase);
        this.ingredientes = new ArrayList<>();
    }

    public void addIngrediente(modelo.Ingrediente ingrediente) {
        if (ingrediente != null) {
            ingredientes.add(ingrediente);
        }
    }

    public List<modelo.Ingrediente> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(List<modelo.Ingrediente> ingredientes) {
        this.ingredientes = ingredientes;
    }

    @Override
    public String toString() {
        return String.format("Pasta %s precio = %.2f€", getNombre(), getPrecioBase());
    }

}