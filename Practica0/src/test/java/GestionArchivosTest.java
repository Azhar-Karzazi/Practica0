import utils.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import modelo.Bebida;
import modelo.Cliente;
import modelo.EstadoPedido;
import modelo.Ingrediente;
import modelo.PagarEfectivo;
import modelo.Pasta;
import modelo.Pedido;
import modelo.Pizza;
import modelo.Producto;
import modelo.Tamano;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GestionArchivosTest {

    private static final File PRODUCTOS = new File("productos.json");
    private static final File PEDIDOS = new File("pedidos.json");
    private static final File INGREDIENTES = new File("ingredientes.csv");

    private GestionDeArchivos archivos;
    private List<Producto> productos;
    private List<Pedido> pedidos;
    private List<Ingrediente> ingredientes;

    @BeforeEach
    void preparar() {
        borrarFicheros();
        archivos = new GestionDeArchivos();

        Pizza margarita = new Pizza("Margarita", 7.5, Tamano.MEDIANA);
        margarita.addIngrediente(new Ingrediente(1, "Tomate", 10, true));
        margarita.addIngrediente(new Ingrediente(2, "Mozzarella", 5, false));
        Pasta pasta = new Pasta("Espaguetis", 8.5);
        pasta.addIngrediente(new Ingrediente(3, "Carne", 4, false));
        Bebida agua = new Bebida("Agua", 1.2, Tamano.PEQUENA);
        productos = List.of(margarita, pasta, agua);

        Cliente ana = new Cliente("Ana", "ana@mail.com", "1234", "Calle 1");
        Pedido pedido = new Pedido(ana);
        pedido.anadirLinea(margarita, 2);
        pedido.anadirLinea(agua, 3);
        pedido.finalizarPedido(new PagarEfectivo());
        ana.addPedidoAlHistorial(pedido);   // referencia circular Cliente <-> Pedido
        pedidos = List.of(pedido);

        ingredientes = List.of(
                new Ingrediente(1, "Tomate", 10, true),
                new Ingrediente(2, "Mozzarella", 5, false));
    }

    @AfterEach
    void limpiar() {
        borrarFicheros();
    }

    private void borrarFicheros() {
        PRODUCTOS.delete();
        PEDIDOS.delete();
        INGREDIENTES.delete();
    }

    // ---------- Actividad 1: Jackson ----------

    @Test
    void exportarProductosCreaElFichero() throws IOException {
        archivos.exportarProductos(productos);

        assertTrue(PRODUCTOS.exists());
    }

    @Test
    void elJsonDeProductosNoLlevaIds() throws IOException {
        archivos.exportarProductos(productos);

        for (JsonNode producto : new ObjectMapper().readTree(PRODUCTOS)) {
            assertFalse(producto.has("id"));
            for (JsonNode ingrediente : producto.path("ingredientes")) {
                assertFalse(ingrediente.has("id"));
            }
        }
    }

    @Test
    void elJsonDeProductosLlevaLosIngredientesAnidados() throws IOException {
        archivos.exportarProductos(productos);

        JsonNode pizza = new ObjectMapper().readTree(PRODUCTOS).get(0);
        assertEquals("pizza", pizza.get("tipo").asText());
        assertEquals(2, pizza.get("ingredientes").size());
        assertEquals("Tomate", pizza.get("ingredientes").get(0).get("nombre").asText());
    }

    @Test
    void importarProductosDevuelveLosTiposYLosDatos() throws IOException {
        archivos.exportarProductos(productos);

        List<Producto> importados = archivos.importarProductos();

        assertEquals(3, importados.size());
        Pizza pizza = assertInstanceOf(Pizza.class, importados.get(0));
        assertEquals("Margarita", pizza.getNombre());
        assertEquals(7.5, pizza.getPrecioBase(), 0.001);
        assertEquals(2, pizza.getIngredientes().size());
        assertEquals("Mozzarella", pizza.getIngredientes().get(1).getNombre());
        assertInstanceOf(Pasta.class, importados.get(1));
        assertInstanceOf(Bebida.class, importados.get(2));
    }

    @Test
    void losIdsDeLosProductosImportadosSonNuevos() throws IOException {
        archivos.exportarProductos(productos);

        List<Producto> importados = archivos.importarProductos();

        assertNotEquals(productos.get(0).getId(), importados.get(0).getId(), "id repetido");
    }

    @Test
    void importarProductosSinFicheroLanzaIOException() {
        assertThrows(IOException.class, () -> archivos.importarProductos());
    }

    // ---------- Actividad 2: Gson ----------

    @Test
    void exportarPedidosCreaElFicheroSinFallarPorLaReferenciaCircular() throws IOException {
        archivos.exportarPedidos(pedidos);

        assertTrue(PEDIDOS.exists());
    }

    @Test
    void elJsonDePedidosNoLlevaMetodoDePagoNiProductoNiHistorial() throws IOException {
        archivos.exportarPedidos(pedidos);

        String json = Files.readString(PEDIDOS.toPath());
        assertFalse(json.contains("metodoPago"));
        assertFalse(json.contains("\"producto\""));
        assertFalse(json.contains("historialPedidos"));
        assertTrue(json.contains("lineasPedido"));
        assertTrue(json.contains("\"cantidad\""));
    }

    @Test
    void importarPedidosReconstruyeElPedidoSusLineasYElCliente() throws IOException {
        archivos.exportarPedidos(pedidos);

        Pedido importado = archivos.importarPedidos().get(0);

        assertEquals(pedidos.get(0).getId(), importado.getId());
        assertEquals(EstadoPedido.FINALIZADO, importado.getEstado());
        assertEquals("ana@mail.com", importado.getCliente().getEmail());
        assertEquals(2, importado.getLineasPedido().size());
        assertEquals(2, importado.getLineasPedido().get(0).getCantidad());
        assertEquals(3, importado.getLineasPedido().get(1).getCantidad());
    }

    @Test
    void importarPedidosConservaLaFecha() throws IOException {
        archivos.exportarPedidos(pedidos);

        Pedido importado = archivos.importarPedidos().get(0);

        assertEquals(pedidos.get(0).getFechaCreacion(), importado.getFechaCreacion());
    }

    @Test
    void alImportarPedidosElProductoYElMetodoDePagoSonNulos() throws IOException {
        archivos.exportarPedidos(pedidos);

        Pedido importado = archivos.importarPedidos().get(0);

        assertNull(importado.getMetodoPago());
        assertNull(importado.getLineasPedido().get(0).getProducto());
    }

    @Test
    void importarPedidosSinFicheroLanzaIOException() {
        assertThrows(IOException.class, () -> archivos.importarPedidos());
    }

    // ---------- Actividad 3: OpenCSV ----------

    @Test
    void exportarIngredientesCreaElFicheroConPuntoYComa() throws Exception {
        archivos.exportarIngredientes(ingredientes);

        List<String> lineas = Files.readAllLines(INGREDIENTES.toPath());
        assertEquals(3, lineas.size());   // cabecera + 2 ingredientes
        for (String linea : lineas) {
            assertTrue(linea.contains(";"));
            assertFalse(linea.contains(","));
        }
    }

    @Test
    void elCsvNoLlevaIdYLaColumnaStockSeLlamaStockDisponible() throws Exception {
        archivos.exportarIngredientes(ingredientes);

        String cabecera = Files.readAllLines(INGREDIENTES.toPath()).get(0).toLowerCase();
        assertTrue(cabecera.contains("stock disponible"));
        assertFalse(cabecera.contains("\"id\""));
        assertFalse(cabecera.contains("\"stock\""));
    }

    @Test
    void importarIngredientesReconstruyeLosDatos() throws Exception {
        archivos.exportarIngredientes(ingredientes);

        List<Ingrediente> importados = archivos.importarIngredientes();

        assertEquals(2, importados.size());
        assertEquals("Tomate", importados.get(0).getNombre());
        assertEquals(10, importados.get(0).getStock());
        assertTrue(importados.get(0).isEsVegano());
        assertEquals("Mozzarella", importados.get(1).getNombre());
        assertFalse(importados.get(1).isEsVegano());
        assertEquals(0, importados.get(0).getId());   // el id no se guarda
    }

    @Test
    void importarIngredientesSinFicheroLanzaIOException() {
        assertThrows(IOException.class, () -> archivos.importarIngredientes());
    }
}