import controlador.*;
import modelo.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ClienteControladorTest {

    private ClienteControlador controlador;

    @BeforeEach
    void preparar() {
        controlador = new ClienteControlador();
    }

    @Test
    void registrarAnadeElClienteALaLista() {
        Cliente c = controlador.registrar("Ana", "ana@mail.com", "1234", "Calle 1");

        assertEquals(1, controlador.listarClientes().size());
        assertEquals(c, controlador.listarClientes().get(0));
    }

    @Test
    void registrarConEmailRepetidoLanzaExcepcion() {
        controlador.registrar("Ana", "ana@mail.com", "1234", "Calle 1");

        assertThrows(IllegalArgumentException.class,
                () -> controlador.registrar("Otra", "ana@mail.com", "abcd", "Calle 2"));
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
    void loginCorrectoDevuelveElCliente() {
        Cliente c = controlador.registrar("Ana", "ana@mail.com", "1234", "Calle 1");

        Optional<Cliente> resultado = controlador.login("ana@mail.com", "1234");

        assertTrue(resultado.isPresent());
        assertEquals(c, resultado.get());
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

    @Test
    void eliminarQuitaAlCliente() {
        Cliente c = controlador.registrar("Ana", "ana@mail.com", "1234", "Calle 1");

        controlador.eliminar(c);

        assertTrue(controlador.listarClientes().isEmpty());
    }

    @Test
    void eliminarClienteInexistenteOnuloLanzaExcepcion() {
        Cliente ajeno = new Cliente("Pepe", "pepe@mail.com", "1234", "Calle 3");

        assertThrows(IllegalArgumentException.class, () -> controlador.eliminar(ajeno));
        assertThrows(IllegalArgumentException.class, () -> controlador.eliminar(null));
    }

    @Test
    void actualizarClienteDevuelveLaListaConElClienteSustituido() {
        controlador.registrar("Ana", "ana@mail.com", "1234", "Calle 1");
        controlador.registrar("Luis", "luis@mail.com", "1234", "Calle 2");
        Cliente nuevo = new Cliente("Ana Maria", "ana@mail.com", "5678", "Calle 9");

        List<Cliente> resultado = controlador.actualizarCliente("ana@mail.com", nuevo);

        assertEquals(2, resultado.size());
        assertEquals("Ana Maria", resultado.get(0).getNombre());
        assertEquals("Luis", resultado.get(1).getNombre());
    }

    @Test
    void actualizarClienteModificaTambienLaListaDelControlador() {
        controlador.registrar("Ana", "ana@mail.com", "1234", "Calle 1");
        Cliente nuevo = new Cliente("Ana Maria", "ana@mail.com", "5678", "Calle 9");

        controlador.actualizarCliente("ana@mail.com", nuevo);

        assertEquals("Ana Maria", controlador.buscarPorEmail("ana@mail.com").get().getNombre());
    }
}