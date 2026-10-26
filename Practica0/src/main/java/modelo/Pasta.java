package modelo;

import java.util.ArrayList;
import java.util.List;

public class Pasta extends Producto {

    private final List<Ingrediente> ingredientes = new ArrayList<>();

    public Pasta() {
    }

    public Pasta(String nombre, double precioBase) {
        super(nombre, precioBase);
    }

    public void addIngrediente(Ingrediente ingrediente) {
        if (ingrediente != null) {
            ingredientes.add(ingrediente);
        }
    }

    public List<Ingrediente> getIngredientes() {
        return new ArrayList<>(ingredientes);
    }

    public void setIngredientes(List<Ingrediente> nuevosIngredientes) {
        ingredientes.clear();
        if (nuevosIngredientes != null) {
            ingredientes.addAll(nuevosIngredientes);
        }
    }

    @Override
    public String toString() {
        return String.format("Pasta %s precio = %.2f€", getNombre(), getPrecioBase());
    }

}