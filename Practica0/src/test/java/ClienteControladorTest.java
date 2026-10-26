import controlador.ClienteControlador;
import modelo.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClienteControladorTest {

    private ClienteControlador controlador;

    @BeforeEach
    void preparar() {
        controlador = new ClienteControlador();
    }

    // ---------- registrar ----------

    @Test
    void registrarAnadeElClienteALaLista() {
        Cliente cliente = controlador.registrar("Ana", "ana@mail.com", "1234", "Calle 1");

        assertEquals(1, controlador.listarClientes().size());
        assertEquals(cliente, controlador.listarClientes().get(0));
    }

    @Test
    void registrarConEmailRepetidoLanzaExcepcion() {
        controlador.registrar("Ana", "ana@mail.com", "1234", "Calle 1");

        assertThrows(IllegalArgumentException.class,
                () -> controlador.registrar("Otra", "ANA@mail.com", "abcd", "Calle 2"));
        assertEquals(1, controlador.listarClientes().size());
    }

    @Test
    void registrarConNombreOEmailVacioLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> controlador.registrar(" ", "ana@mail.com", "1234", "Calle 1"));
        assertThrows(IllegalArgumentException.class,
                () -> controlador.registrar("Ana", "", "1234", "Calle 1"));
        assertTrue(controlador.listarClientes().isEmpty());
    }

    @Test
    void registrarGuardaElEmailEnMinusculasYSinEspacios() {
        Cliente cliente = controlador.registrar("Ana", "  Ana@Mail.COM ", "1234", "Calle 1");

        assertEquals("ana@mail.com", cliente.getEmail());
    }

    // ---------- login y búsqueda ----------

    @Test
    void loginCorrectoDevuelveElCliente() {
        Cliente cliente = controlador.registrar("Ana", "ana@mail.com", "1234", "Calle 1");

        assertEquals(cliente, controlador.login("ana@mail.com", "1234").orElseThrow());
    }

    @Test
    void loginConPasswordIncorrectaDevuelveVacio() {
        controlador.registrar("Ana", "ana@mail.com", "1234", "Calle 1");

        assertTrue(controlador.login("ana@mail.com", "mal").isEmpty());
    }

    @Test
    void loginConEmailInexistenteDevuelveVacio() {
        assertTrue(controlador.login("nadie@mail.com", "1234").isEmpty());
    }

    @Test
    void buscarPorEmailIgnoraMayusculas() {
        controlador.registrar("Ana", "ana@mail.com", "1234", "Calle 1");

        assertTrue(controlador.buscarPorEmail("ANA@MAIL.COM").isPresent());
        assertTrue(controlador.buscarPorEmail("otro@mail.com").isEmpty());
    }

    @Test
    void listarClientesDevuelveUnaCopia() {
        controlador.registrar("Ana", "ana@mail.com", "1234", "Calle 1");

        controlador.listarClientes().clear();

        assertEquals(1, controlador.listarClientes().size());
    }

    // ---------- actualizar ----------

    @Test
    void actualizarClienteModificaElClienteExistente() {
        Cliente original = controlador.registrar("Ana", "ana@mail.com", "1234", "Calle 1");
        Cliente datos = new Cliente("Ana Maria", "ana.maria@mail.com", "5678", "Calle 9");

        Cliente actualizado = controlador.actualizarCliente("ana@mail.com", datos);

        assertSame(original, actualizado);
        assertEquals("Ana Maria", controlador.buscarPorEmail("ana.maria@mail.com").orElseThrow().getNombre());
        assertEquals("5678", actualizado.getPassword());
        assertEquals("Calle 9", actualizado.getDireccion());
        assertEquals(1, controlador.listarClientes().size());
    }

    @Test
    void actualizarClienteQueNoExisteLanzaExcepcion() {
        Cliente datos = new Cliente("Ana", "ana@mail.com", "1234", "Calle 1");

        assertThrows(IllegalArgumentException.class, () -> controlador.actualizarCliente("nadie@mail.com", datos));
    }

    @Test
    void actualizarClienteConElEmailDeOtroLanzaExcepcion() {
        controlador.registrar("Ana", "ana@mail.com", "1234", "Calle 1");
        controlador.registrar("Luis", "luis@mail.com", "1234", "Calle 2");
        Cliente datos = new Cliente("Luis", "ana@mail.com", "1234", "Calle 2");

        assertThrows(IllegalArgumentException.class, () -> controlador.actualizarCliente("luis@mail.com", datos));
        assertEquals("luis@mail.com", controlador.buscarPorEmail("luis@mail.com").orElseThrow().getEmail());
    }

    @Test
    void actualizarClienteConservandoSuPropioEmailFunciona() {
        controlador.registrar("Ana", "ana@mail.com", "1234", "Calle 1");
        Cliente datos = new Cliente("Ana Nueva", "ana@mail.com", "1234", "Calle 1");

        controlador.actualizarCliente("ana@mail.com", datos);

        assertEquals("Ana Nueva", controlador.buscarPorEmail("ana@mail.com").orElseThrow().getNombre());
    }

    // ---------- eliminar ----------

    @Test
    void eliminarQuitaAlCliente() {
        Cliente cliente = controlador.registrar("Ana", "ana@mail.com", "1234", "Calle 1");

        controlador.eliminar(cliente);

        assertTrue(controlador.listarClientes().isEmpty());
    }

    @Test
    void eliminarUnClienteInexistenteOnuloLanzaExcepcion() {
        Cliente ajeno = new Cliente("Pepe", "pepe@mail.com", "1234", "Calle 3");

        assertThrows(IllegalArgumentException.class, () -> controlador.eliminar(ajeno));
        assertThrows(IllegalArgumentException.class, () -> controlador.eliminar(null));
    }
}