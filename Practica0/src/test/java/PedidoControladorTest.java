import controlador.PedidoControlador;
import modelo.Bebida;
import modelo.Cliente;
import modelo.EstadoPedido;
import modelo.PagarEfectivo;
import modelo.PagarTarjeta;
import modelo.Pedido;
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

class PedidoControladorTest {

    private PedidoControlador controlador;
    private Cliente cliente;
    private Producto pizza;
    private Producto bebida;

    @BeforeEach
    void preparar() {
        new File("pedidos.json").delete();
        controlador = new PedidoControlador();
        cliente = new Cliente("Ana", "ana@mail.com", "1234", "Calle 1");
        pizza = new Pizza("Margarita", 7.5, Tamano.MEDIANA);
        bebida = new Bebida("Agua", 1.2, Tamano.PEQUENA);
    }

    @AfterEach
    void limpiar() {
        new File("pedidos.json").delete();
    }

    // ---------- anadirProducto ----------

    @Test
    void anadirProductoSinPedidoPendienteCreaElPedidoConLaLinea() {
        controlador.anadirProducto(pizza, 2, cliente);

        assertEquals(1, controlador.listarPedidos().size());
        Pedido pedido = controlador.listarPedidos().get(0);
        assertEquals(EstadoPedido.PENDIENTE, pedido.getEstado());
        assertEquals(1, pedido.getLineasPedido().size());
        assertEquals(2, pedido.getLineasPedido().get(0).getCantidad());
        assertEquals(15.0, pedido.calcularTotal(), 0.001);
    }

    @Test
    void elPedidoCreadoQuedaEnElHistorialDelCliente() {
        controlador.anadirProducto(pizza, 1, cliente);

        assertEquals(1, cliente.getHistorialPedidos().size());
        assertEquals(controlador.listarPedidos().get(0), cliente.getHistorialPedidos().get(0));
    }

    @Test
    void anadirVariosProductosReutilizaElPedidoPendiente() {
        controlador.anadirProducto(pizza, 1, cliente);
        controlador.anadirProducto(bebida, 3, cliente);

        assertEquals(1, controlador.listarPedidos().size());
        Pedido pedido = controlador.listarPedidos().get(0);
        assertEquals(2, pedido.getLineasPedido().size());
        assertEquals(7.5 + 3.6, pedido.calcularTotal(), 0.001);
    }

    @Test
    void anadirProductoConClienteNuloLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> controlador.anadirProducto(pizza, 1, null));
    }

    @Test
    void anadirProductoConCantidadInvalidaLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> controlador.anadirProducto(pizza, 0, cliente));
    }

    @Test
    void anadirProductoNuloLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> controlador.anadirProducto(null, 1, cliente));
    }

    @Test
    void pedidosDeClientesDistintosSonIndependientes() {
        Cliente otro = new Cliente("Luis", "luis@mail.com", "1234", "Calle 2");

        controlador.anadirProducto(pizza, 1, cliente);
        controlador.anadirProducto(bebida, 1, otro);

        assertEquals(2, controlador.listarPedidos().size());
        assertEquals(1, cliente.getHistorialPedidos().size());
        assertEquals(1, otro.getHistorialPedidos().size());
    }

    // ---------- finalizarPedido ----------

    @Test
    void finalizarPedidoCambiaElEstadoYGuardaElMetodoDePago() {
        controlador.anadirProducto(pizza, 1, cliente);
        PagarEfectivo efectivo = new PagarEfectivo();

        controlador.finalizarPedido(efectivo, cliente);

        Pedido pedido = controlador.listarPedidos().get(0);
        assertEquals(EstadoPedido.FINALIZADO, pedido.getEstado());
        assertSame(efectivo, pedido.getMetodoPago());
    }

    @Test
    void finalizarPedidoSinPedidoPendienteLanzaExcepcion() {
        assertThrows(IllegalStateException.class,
                () -> controlador.finalizarPedido(new PagarTarjeta(), cliente));
    }

    @Test
    void finalizarPedidoSinMetodoDePagoLanzaExcepcion() {
        controlador.anadirProducto(pizza, 1, cliente);

        assertThrows(IllegalArgumentException.class, () -> controlador.finalizarPedido(null, cliente));
        assertEquals(EstadoPedido.PENDIENTE, controlador.listarPedidos().get(0).getEstado());
    }

    @Test
    void despuesDeFinalizarSeCreaUnPedidoNuevoAlAnadirOtroProducto() {
        controlador.anadirProducto(pizza, 1, cliente);
        controlador.finalizarPedido(new PagarEfectivo(), cliente);

        controlador.anadirProducto(bebida, 1, cliente);

        assertEquals(2, controlador.listarPedidos().size());
        assertEquals(EstadoPedido.FINALIZADO, controlador.listarPedidos().get(0).getEstado());
        assertEquals(EstadoPedido.PENDIENTE, controlador.listarPedidos().get(1).getEstado());
    }

    // ---------- entregar y cancelar ----------

    @Test
    void entregarPedidoFinalizadoLoMarcaComoEntregado() {
        controlador.anadirProducto(pizza, 1, cliente);
        controlador.finalizarPedido(new PagarEfectivo(), cliente);
        Pedido pedido = controlador.listarPedidos().get(0);

        controlador.entregarPedido(pedido);

        assertEquals(EstadoPedido.ENTREGADO, pedido.getEstado());
    }

    @Test
    void entregarPedidoPendienteLanzaExcepcion() {
        controlador.anadirProducto(pizza, 1, cliente);
        Pedido pedido = controlador.listarPedidos().get(0);

        assertThrows(IllegalStateException.class, () -> controlador.entregarPedido(pedido));
    }

    @Test
    void cancelarPedidoPendienteLoMarcaComoCancelado() {
        controlador.anadirProducto(pizza, 1, cliente);
        Pedido pedido = controlador.listarPedidos().get(0);

        controlador.cancelarPedido(pedido);

        assertEquals(EstadoPedido.CANCELADO, pedido.getEstado());
    }

    @Test
    void cancelarPedidoFinalizadoLanzaExcepcion() {
        controlador.anadirProducto(pizza, 1, cliente);
        controlador.finalizarPedido(new PagarEfectivo(), cliente);
        Pedido pedido = controlador.listarPedidos().get(0);

        assertThrows(IllegalStateException.class, () -> controlador.cancelarPedido(pedido));
    }

    @Test
    void trasCancelarUnPedidoSeCreaUnoNuevoAlAnadirProducto() {
        controlador.anadirProducto(pizza, 1, cliente);
        controlador.cancelarPedido(controlador.listarPedidos().get(0));

        controlador.anadirProducto(bebida, 1, cliente);

        assertEquals(2, controlador.listarPedidos().size());
        assertEquals(EstadoPedido.PENDIENTE, controlador.listarPedidos().get(1).getEstado());
    }

    // ---------- buscar y listar ----------

    @Test
    void buscarPorIdEncuentraElPedido() {
        controlador.anadirProducto(pizza, 1, cliente);
        Pedido pedido = controlador.listarPedidos().get(0);

        assertEquals(pedido, controlador.buscarPorId(pedido.getId()).orElseThrow());
        assertTrue(controlador.buscarPorId(-1).isEmpty());
    }

    @Test
    void listarPedidosDevuelveUnaCopia() {
        controlador.anadirProducto(pizza, 1, cliente);

        controlador.listarPedidos().clear();

        assertEquals(1, controlador.listarPedidos().size());
    }

    // ---------- exportar e importar (Gson) ----------

    @Test
    void exportarEImportarPedidosConservaElPedidoSusLineasYElCliente() throws IOException {
        controlador.anadirProducto(pizza, 2, cliente);
        controlador.anadirProducto(bebida, 3, cliente);
        controlador.finalizarPedido(new PagarEfectivo(), cliente);
        Pedido original = controlador.listarPedidos().get(0);

        controlador.exportarPedidos();
        List<Pedido> importados = controlador.importarPedidos();

        assertEquals(1, importados.size());
        Pedido importado = importados.get(0);
        assertEquals(original.getId(), importado.getId());
        assertEquals(EstadoPedido.FINALIZADO, importado.getEstado());
        assertEquals(original.getFechaCreacion(), importado.getFechaCreacion());
        assertEquals("ana@mail.com", importado.getCliente().getEmail());
        assertEquals(2, importado.getLineasPedido().size());
        assertEquals(3, importado.getLineasPedido().get(1).getCantidad());
    }

    @Test
    void alImportarPedidosElProductoYElMetodoDePagoSonNulos() throws IOException {
        controlador.anadirProducto(pizza, 1, cliente);
        controlador.finalizarPedido(new PagarEfectivo(), cliente);

        controlador.exportarPedidos();
        Pedido importado = controlador.importarPedidos().get(0);

        assertNull(importado.getMetodoPago());
        assertNull(importado.getLineasPedido().get(0).getProducto());
    }

    @Test
    void importarPedidosSinFicheroLanzaIOException() {
        assertThrows(IOException.class, () -> controlador.importarPedidos());
    }
}