package modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Pedido {

    private static int contadorId = 0;

    private final int id;
    private final Cliente cliente;
    private final List<LineaPedido> lineasPedido;
    private EstadoPedido estado;
    private transient Pagable metodoPago;
    private final LocalDate fechaCreacion;

    public Pedido(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El pedido debe tener un cliente asociado");
        }
        this.id = ++contadorId;
        this.cliente = cliente;
        this.lineasPedido = new ArrayList<>();
        this.estado = modelo.EstadoPedido.PENDIENTE;
        this.fechaCreacion = LocalDate.now();
    }

    public LineaPedido anadirLinea(Producto producto, int cantidad) {
        if (estado != EstadoPedido.PENDIENTE) {
            throw new IllegalStateException("No se pueden añadir líneas a un pedido que no está PENDIENTE (estado actual: " + estado + ")");
        }

        LineaPedido linea = new LineaPedido(producto, cantidad);
        lineasPedido.add(linea);
        return linea;
    }

    public void eliminarLinea(LineaPedido linea) {
        if (estado != EstadoPedido.PENDIENTE) {
            throw new IllegalStateException("No se pueden eliminar líneas de un pedido que no está PENDIENTE");
        }
        lineasPedido.remove(linea);
    }

    public double calcularTotal() {
        return lineasPedido.stream()
                .mapToDouble(LineaPedido::calcularSubtotal)
                .sum();
    }

    public void finalizarPedido(Pagable metodoPago) {
        if (estado != EstadoPedido.PENDIENTE) {
            throw new IllegalStateException("Solo se puede finalizar un pedido PENDIENTE (estado actual: " + estado + ")");
        }
        if (lineasPedido.isEmpty()) {
            throw new IllegalStateException("No se puede finalizar un pedido sin líneas de pedido");
        }
        if (metodoPago == null) {
            throw new IllegalArgumentException("Debe indicarse un método de pago");
        }
        this.metodoPago = metodoPago;
        metodoPago.pagar(calcularTotal());
        this.estado = EstadoPedido.FINALIZADO;
    }

    public void entregarPedido() {
        if (estado != modelo.EstadoPedido.FINALIZADO) {
            throw new IllegalStateException("Solo se puede entregar un pedido FINALIZADO (estado actual: " + estado + ")");
        }
        this.estado = modelo.EstadoPedido.ENTREGADO;
    }

    public void cancelarPedido() {
        if (estado != modelo.EstadoPedido.PENDIENTE) {
            throw new IllegalStateException(
                    "Solo se pueden cancelar pedidos PENDIENTES (estado actual: " + estado + ")");
        }
        this.estado = modelo.EstadoPedido.CANCELADO;
    }

    public int getId() {
        return id;
    }

    public modelo.Cliente getCliente() {
        return cliente;
    }

    public List<modelo.LineaPedido> getLineasPedido() {
        return new ArrayList<>(lineasPedido);
    }

    public modelo.EstadoPedido getEstado() {
        return estado;
    }

    public modelo.Pagable getMetodoPago() {
        return metodoPago;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Pedido pedido = (Pedido) o;
        return id == pedido.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Pedido [id=%d, cliente=%s, estado=%s, total=%.2f€, lineas=%d]",
                id, cliente.getNombre(), estado, calcularTotal(), lineasPedido.size());
    }
}
