
import controlador.*;
import modelo.Bebida;
import modelo.Pizza;
import modelo.Producto;
import modelo.Tamano;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductoControladorTest {

    private ProductoControlador controlador;

    @BeforeEach
    void preparar() {
        controlador = new ProductoControlador();
    }

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

    @Test
    void eliminarQuitaElProducto() {
        Producto primero = controlador.listarProductos().get(0);

        controlador.eliminar(primero);

        assertEquals(4, controlador.listarProductos().size());
        assertFalse(controlador.listarProductos().contains(primero));
    }

    @Test
    void eliminarProductoInexistenteOnuloLanzaExcepcion() {
        Producto ajeno = new Bebida("Fanta", 1.5, Tamano.MEDIANA);

        assertThrows(IllegalArgumentException.class, () -> controlador.eliminar(ajeno));
        assertThrows(IllegalArgumentException.class, () -> controlador.eliminar(null));
        assertEquals(5, controlador.listarProductos().size());
    }

    @Test
    void actualizarProductoDevuelveLaListaConElProductoSustituido() {
        Producto nuevo = new Pizza("Margarita Premium", 12.0, Tamano.GRANDE);

        List<Producto> resultado = controlador.actualizarProducto("Margarita", nuevo);

        assertEquals(5, resultado.size());
        assertEquals("Margarita Premium", resultado.get(0).getNombre());
        assertEquals("Carbonara", resultado.get(1).getNombre());
    }

    @Test
    void actualizarProductoModificaTambienLaListaDelControlador() {
        Producto nuevo = new Pizza("Margarita Premium", 12.0, Tamano.GRANDE);

        controlador.actualizarProducto("Margarita", nuevo);

        assertEquals("Margarita Premium", controlador.listarProductos().get(0).getNombre());
    }
}