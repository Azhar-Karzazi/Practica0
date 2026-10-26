package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Cliente {

    private static int contadorId = 0;

    private final int id;
    private String nombre;
    private String email;
    private transient String  password;
    private String direccion;
    private final transient List<Pedido> historialPedidos;

    public Cliente(int id, List<Pedido> historialPedidos){
        this.id = id;
        this.historialPedidos = historialPedidos;
    }

    public Cliente(String nombre, String email, String password, String direccion) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del cliente no puede estar vacío");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email del cliente no puede estar vacío");
        }
        this.id = ++contadorId;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.direccion = direccion;
        this.historialPedidos = new ArrayList<>();
    }

    public Pedido realizarPedido(Producto producto, int cantidad) {
        Pedido pedido = new Pedido(this);
        pedido.anadirLinea(producto, cantidad);
        historialPedidos.add(pedido);
        return pedido;
    }

    public void addPedidoAlHistorial(Pedido pedido) {
        if (pedido != null && !historialPedidos.contains(pedido)) {
            historialPedidos.add(pedido);
        }
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío");
        }
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public List<Pedido> getHistorialPedidos() {
        return new ArrayList<>(historialPedidos);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(email, cliente.email);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(email);
    }

    @Override
    public String toString() {
        return String.format("Cliente  %s, con el email: %s", nombre, email);
    }
}
