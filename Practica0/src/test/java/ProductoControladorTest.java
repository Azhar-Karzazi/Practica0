import controlador.ProductoControlador;
import modelo.Bebida;
import modelo.Ingrediente;
import modelo.Pasta;
import modelo.Pizza;
import modelo.Producto;
import modelo.Tamano;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductoControladorTest {

    private ProductoControlador controlador;

    @BeforeEach
    void preparar() {
        borrarFicheros();
        controlador = new ProductoControlador();
    }

    @AfterEach
    void limpiar() {
        borrarFicheros();
    }

    /** Los métodos de exportar crean estos ficheros en la carpeta del proyecto. */
    private void borrarFicheros() {
        new File("productos.json").delete();
        new File("ingredientes.csv").delete();
    }

    // ---------- catálogo, añadir y listar ----------

    @Test
    void alCrearseCargaElCatalogoInicial() {
        List<Producto> productos = controlador.listarProductos();

        assertEquals(5, productos.size());
        assertEquals("Margarita", productos.get(0).getNombre());
        assertEquals("Espaguetis Boloñesa", productos.get(4).getNombre());
    }

    @Test
    void addProductoLoAnadeYLoDevuelve() {
        Producto pizza = new Pizza("Cuatro Quesos", 10.0, Tamano.GRANDE);

        Producto devuelto = controlador.addProducto(pizza);

        assertSame(pizza, devuelto);
        assertEquals(6, controlador.listarProductos().size());
        assertTrue(controlador.listarProductos().contains(pizza));
    }

    @Test
    void listarProductosEsDeSoloLectura() {
        List<Producto> productos = controlador.listarProductos();

        assertThrows(UnsupportedOperationException.class, productos::clear);
        assertThrows(UnsupportedOperationException.class,
                () -> productos.add(new Bebida("Fanta", 1.5, Tamano.MEDIANA)));
    }

    // ---------- actualizar ----------

    @Test
    void actualizarProductoCambiaElNombreYElPrecio() {
        Producto original = controlador.listarProductos().get(0);
        Producto datos = new Pizza("Margarita Premium", 12.0, Tamano.GRANDE);

        Producto actualizado = controlador.actualizarProducto(original.getId(), datos);

        assertSame(original, actualizado);
        assertEquals("Margarita Premium", original.getNombre());
        assertEquals(12.0, original.getPrecioBase(), 0.001);
        assertEquals(5, controlador.listarProductos().size());
    }

    @Test
    void actualizarProductoConUnIdInexistenteLanzaExcepcion() {
        Producto datos = new Pizza("Nueva", 5.0, Tamano.MEDIANA);

        assertThrows(IllegalArgumentException.class, () -> controlador.actualizarProducto(-1, datos));
    }

    // ---------- eliminar ----------

    @Test
    void eliminarQuitaElProducto() {
        Producto primero = controlador.listarProductos().get(0);

        controlador.eliminar(primero);

        assertEquals(4, controlador.listarProductos().size());
        assertFalse(controlador.listarProductos().contains(primero));
    }

    @Test
    void eliminarUnProductoInexistenteOnuloLanzaExcepcion() {
        Producto ajeno = new Bebida("Fanta", 1.5, Tamano.MEDIANA);

        assertThrows(IllegalArgumentException.class, () -> controlador.eliminar(ajeno));
        assertThrows(IllegalArgumentException.class, () -> controlador.eliminar(null));
        assertEquals(5, controlador.listarProductos().size());
    }

    // ---------- exportar e importar productos (Jackson) ----------

    @Test
    void exportarEImportarProductosConservaLosDatosYLosTipos() throws Exception {
        Pizza pizza = (Pizza) controlador.listarProductos().get(0);
        pizza.addIngrediente(new Ingrediente(1, "Tomate", 10, true));

        controlador.exportarProductos();
        List<Producto> importados = controlador.importarProductos();

        assertEquals(5, importados.size());
        Pizza pizzaImportada = assertInstanceOf(Pizza.class, importados.get(0));
        assertEquals("Margarita", pizzaImportada.getNombre());
        assertEquals(1, pizzaImportada.getIngredientes().size());
        assertEquals("Tomate", pizzaImportada.getIngredientes().get(0).getNombre());
        assertInstanceOf(Bebida.class, importados.get(2));
        assertInstanceOf(Pasta.class, importados.get(4));
    }

    @Test
    void importarProductosSinFicheroLanzaIOException() {
        assertThrows(IOException.class, () -> controlador.importarProductos());
    }

    // ---------- exportar e importar ingredientes (OpenCSV) ----------

    @Test
    void exportarEImportarIngredientesConservaLosDatos() throws Exception {
        List<Ingrediente> ingredientes = List.of(
                new Ingrediente(1, "Tomate", 10, true),
                new Ingrediente(2, "Mozzarella", 5, false));

        controlador.exportarIngredientes(ingredientes);
        List<Ingrediente> importados = controlador.importarIngredientes();

        assertEquals(2, importados.size());
        assertEquals("Tomate", importados.get(0).getNombre());
        assertEquals(10, importados.get(0).getStock());
        assertTrue(importados.get(0).isEsVegano());
        assertEquals("Mozzarella", importados.get(1).getNombre());
        assertFalse(importados.get(1).isEsVegano());
    }

    @Test
    void importarIngredientesSinFicheroLanzaIOException() {
        assertThrows(IOException.class, () -> controlador.importarIngredientes());
    }
}