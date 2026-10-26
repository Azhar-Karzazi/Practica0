package controlador;

import modelo.Cliente;
import modelo.EstadoPedido;
import modelo.Pagable;
import modelo.Pedido;
import modelo.Producto;
import utils.GestionDeArchivos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PedidoControlador {

    private final List<Pedido> pedidos;

    public PedidoControlador() {
        this.pedidos = new ArrayList<>();
    }

    public void anadirProducto(Producto producto, int cantidad, Cliente cliente) {
        Pedido pedidoPendiente = buscarPedidoPendiente(cliente);

        if (pedidoPendiente == null) {
            pedidoPendiente = new Pedido(cliente);
            cliente.addPedidoAlHistorial(pedidoPendiente);
            pedidos.add(pedidoPendiente);
        }
        pedidoPendiente.anadirLinea(producto, cantidad);
    }

    public void finalizarPedido(Pagable pagable, Cliente cliente) {
        Pedido pedidoPendiente = buscarPedidoPendiente(cliente);
        if (pedidoPendiente == null) {
            throw new IllegalStateException("El cliente no tiene ningún pedido pendiente");
        }
        pedidoPendiente.finalizarPedido(pagable);
    }

    private Pedido buscarPedidoPendiente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Debe indicarse un cliente");
        }

        List<Pedido> pendientes = cliente.getHistorialPedidos().stream()
                .filter(p -> p.getEstado() == EstadoPedido.PENDIENTE)
                .toList();

        if (pendientes.size() > 1) {
            throw new IllegalStateException("El cliente tiene más de un pedido pendiente, no se puede determinar cuál usar");
        }

        return pendientes.isEmpty() ? null : pendientes.get(0);
    }

    public void entregarPedido(Pedido pedido) {
        pedido.entregarPedido();
    }

    public void cancelarPedido(Pedido pedido) {
        pedido.cancelarPedido();
    }

    public Optional<Pedido> buscarPorId(int id) {
        return pedidos.stream().filter(p -> p.getId() == id).findFirst();
    }

    public List<Pedido> listarPedidos() {
        return new ArrayList<>(pedidos);
    }

    public void exportarPedidos() throws IOException {
        GestionDeArchivos.exportarPedidos(pedidos);
    }

    public List<Pedido> importarPedidos() throws IOException {
        return GestionDeArchivos.importarPedidos();
    }
}