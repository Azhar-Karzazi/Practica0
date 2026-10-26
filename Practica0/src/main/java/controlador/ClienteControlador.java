package controlador;

import modelo.Cliente;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClienteControlador {

    private final List<Cliente> clientes;

    public ClienteControlador() {
        this.clientes = new ArrayList<>();
    }

    public Cliente registrar(String nombre, String email, String password, String direccion) {
        if (buscarPorEmail(email).isPresent()) {
            throw new IllegalArgumentException("Ya existe un cliente registrado con ese email");
        }
        Cliente cliente = new Cliente(nombre, email, password, direccion);
        clientes.add(cliente);
        return cliente;
    }

    public Optional<Cliente> login(String email, String password) {
        return buscarPorEmail(email).filter(c -> c.getPassword() != null && c.getPassword().equals(password));
    }

    public Optional<Cliente> buscarPorEmail(String email) {
        return clientes.stream().filter(c -> c.getEmail().equalsIgnoreCase(email)).findFirst();
    }

    public List<Cliente> listarClientes() {
        return new ArrayList<>(clientes);
    }

    public Cliente actualizarCliente(String email, Cliente datos) {
        Cliente cliente = buscarPorEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("El cliente indicado no existe"));

        cliente.setNombre(datos.getNombre());
        cliente.setEmail(datos.getEmail());
        cliente.setPassword(datos.getPassword());
        cliente.setDireccion(datos.getDireccion());
        return cliente;
    }

    public void eliminar(Cliente cliente) {
        if (cliente == null || !clientes.remove(cliente)) {
            throw new IllegalArgumentException("El cliente indicado no existe");
        }
    }
}