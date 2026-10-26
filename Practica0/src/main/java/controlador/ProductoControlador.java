package controlador;

import modelo.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ProductoControlador {
    private final List<Producto> productos;

    public ProductoControlador() {
        this.productos = new ArrayList<>();
        cargarCatalogoInicial();
    }

    private void cargarCatalogoInicial() {
        productos.add(new Pizza("Margarita", 7.5, Tamano.MEDIANA));
        productos.add(new Pizza("Carbonara", 9.0, Tamano.MEDIANA));
        productos.add(new Bebida("Coca-Cola", 1.8, Tamano.MEDIANA));
        productos.add(new Bebida("Agua", 1.2, Tamano.PEQUENA));
        productos.add(new Pasta("Espaguetis Boloñesa", 8.5));
    }

    public Producto addProducto(Producto producto) {
        productos.add(producto);
        return producto;
    }

    public List<Producto> listarProductos() {
        // Muestra la lista sin poder modificarla (readonly)
        return Collections.unmodifiableList(productos);
    }

    public Producto actualizarProducto(int id, Producto datos) {
        Producto producto = productos.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("El producto indicado no existe"));

        producto.setNombre(datos.getNombre());
        producto.setPrecioBase(datos.getPrecioBase());
        return producto;
    }

    public void eliminar(Producto producto) {
        if (producto == null || !productos.remove(producto)) {
            throw new IllegalArgumentException("El producto indicado no existe");
        }
        productos.remove((producto));
    }
}