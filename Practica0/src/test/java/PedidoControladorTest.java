import controlador.*;
import modelo.Bebida;
import modelo.Cliente;
import modelo.EstadoPedido;
import modelo.PagarEfectivo;
import modelo.Pedido;
import modelo.Pizza;
import modelo.Producto;
import modelo.Tamano;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PedidoControladorTest {

    private PedidoControlador controlador;
    private Cliente cliente;
    private Producto pizza;
    private Producto bebida;

    @BeforeEach
    void preparar() {
        controlador = new PedidoControlador();
        cliente = new Cliente("Ana", "ana@mail.com", "1234", "Calle 1");
        pizza = new Pizza("Margarita", 7.5, Tamano.MEDIANA);
        bebida = new Bebida("Agua", 1.2, Tamano.PEQUENA);
    }

    @Test
    void anadirProductoSinPedidoPendienteCreaUnoNuevo() {
        controlador.anadirProducto(pizza, 2, cliente);

        assertEquals(1, controlador.listarPedidos().size());
        Pedido pedido = controlador.listarPedidos().get(0);
        assertEquals(EstadoPedido.PENDIENTE, pedido.getEstado());
        assertEquals(1, pedido.getLineasPedido().size());
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
        assertEquals(2, controlador.listarPedidos().get(0).getLineasPedido().size());
        assertEquals(7.5 + 3.6, controlador.listarPedidos().get(0).calcularTotal(), 0.001);
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
                () -> controlador.finalizarPedido(new PagarEfectivo(), cliente));
    }

    @Test
    void finalizarPedidoSinMetodoDePagoLanzaExcepcion() {
        controlador.anadirProducto(pizza, 1, cliente);

        assertThrows(IllegalArgumentException.class, () -> controlador.finalizarPedido(null, cliente));
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

    @Test
    void pedidosDeClientesDistintosSonIndependientes() {
        Cliente otro = new Cliente("Luis", "luis@mail.com", "1234", "Calle 2");

        controlador.anadirProducto(pizza, 1, cliente);
        controlador.anadirProducto(bebida, 1, otro);

        assertEquals(2, controlador.listarPedidos().size());
        assertEquals(1, cliente.getHistorialPedidos().size());
        assertEquals(1, otro.getHistorialPedidos().size());
    }
}