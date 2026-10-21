import utils.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import modelo.Bebida;
import modelo.Ingrediente;
import modelo.Pasta;
import modelo.Pizza;
import modelo.Producto;
import modelo.Tamano;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GestionArchivosTest {

    private static final File FICHERO = new File("productos.json");
    private static final File COPIA = new File("productos.json.bak");

    private GestionDeArchivos gestion;
    private List<Producto> productos;

    @BeforeEach
    void preparar() throws IOException {
        // Si ya existe un productos.json real, se guarda y se restaura al terminar
        if (FICHERO.exists()) {
            Files.move(FICHERO.toPath(), COPIA.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        gestion = new GestionDeArchivos();

        Pizza margarita = new Pizza("Margarita", 7.5, Tamano.MEDIANA);
        margarita.addIngrediente(new Ingrediente(1, "Tomate", 10, true));
        margarita.addIngrediente(new Ingrediente(2, "Mozzarella", 5, false));

        Pasta pasta = new Pasta("Espaguetis", 8.5);
        pasta.addIngrediente(new Ingrediente(3, "Carne", 4, false));

        Bebida agua = new Bebida("Agua", 1.2, Tamano.PEQUENA);

        productos = List.of(margarita, pasta, agua);
    }

    @AfterEach
    void limpiar() throws IOException {
        FICHERO.delete();
        if (COPIA.exists()) {
            Files.move(COPIA.toPath(), FICHERO.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @Test
    void exportarCreaElFicheroProductosJson() throws IOException {
        gestion.exportar(productos);

        assertTrue(FICHERO.exists());
        assertTrue(FICHERO.length() > 0);
    }

    @Test
    void elJsonNoContieneNingunId() throws IOException {
        gestion.exportar(productos);

        JsonNode raiz = new ObjectMapper().readTree(FICHERO);
        assertTrue(raiz.isArray());
        assertEquals(3, raiz.size());

        for (JsonNode producto : raiz) {
            assertFalse(producto.has("id"), "El producto no debe llevar id");
            if (producto.has("ingredientes")) {
                for (JsonNode ingrediente : producto.get("ingredientes")) {
                    assertFalse(ingrediente.has("id"), "El ingrediente no debe llevar id");
                }
            }
        }
    }

    @Test
    void elJsonNoContieneAtributosCalculados() throws IOException {
        gestion.exportar(productos);

        JsonNode raiz = new ObjectMapper().readTree(FICHERO);
        for (JsonNode producto : raiz) {
            // Solo deben aparecer datos almacenados
            producto.fieldNames().forEachRemaining(campo ->
                    assertTrue(List.of("tipo", "nombre", "precioBase", "tamano", "ingredientes").contains(campo),
                            "Campo inesperado en el JSON: " + campo));
        }
    }

    @Test
    void elJsonIncluyeElTipoDeCadaProducto() throws IOException {
        gestion.exportar(productos);

        JsonNode raiz = new ObjectMapper().readTree(FICHERO);
        assertEquals("pizza", raiz.get(0).get("tipo").asText());
        assertEquals("pasta", raiz.get(1).get("tipo").asText());
        assertEquals("bebida", raiz.get(2).get("tipo").asText());
    }

    @Test
    void elJsonLlevaLosIngredientesAnidados() throws IOException {
        gestion.exportar(productos);

        JsonNode pizza = new ObjectMapper().readTree(FICHERO).get(0);
        assertEquals(2, pizza.get("ingredientes").size());
        assertEquals("Tomate", pizza.get("ingredientes").get(0).get("nombre").asText());
        assertEquals(10, pizza.get("ingredientes").get(0).get("stock").asInt());
        assertTrue(pizza.get("ingredientes").get(0).get("esVegano").asBoolean());
    }

    @Test
    void importarDevuelveLaMismaCantidadYLosTiposCorrectos() throws IOException {
        gestion.exportar(productos);

        List<Producto> importados = gestion.importar();

        assertEquals(3, importados.size());
        assertInstanceOf(Pizza.class, importados.get(0));
        assertInstanceOf(Pasta.class, importados.get(1));
        assertInstanceOf(Bebida.class, importados.get(2));
    }

    @Test
    void importarReconstruyeLosDatosDeLaPizzaYSusIngredientes() throws IOException {
        gestion.exportar(productos);

        Pizza pizza = (Pizza) gestion.importar().get(0);

        assertEquals("Margarita", pizza.getNombre());
        assertEquals(7.5, pizza.getPrecioBase(), 0.001);
        assertEquals(Tamano.MEDIANA, pizza.getTamano());
        assertEquals(2, pizza.getIngredientes().size());

        Ingrediente mozzarella = pizza.getIngredientes().get(1);
        assertEquals("Mozzarella", mozzarella.getNombre());
        assertEquals(5, mozzarella.getStock());
        assertFalse(mozzarella.isEsVegano());
    }

    @Test
    void importarReconstruyeLaPastaYLaBebida() throws IOException {
        gestion.exportar(productos);
        List<Producto> importados = gestion.importar();

        Pasta pasta = (Pasta) importados.get(1);
        assertEquals("Espaguetis", pasta.getNombre());
        assertEquals(1, pasta.getIngredientes().size());
        assertEquals("Carne", pasta.getIngredientes().get(0).getNombre());

        Bebida bebida = (Bebida) importados.get(2);
        assertEquals("Agua", bebida.getNombre());
        assertEquals(1.2, bebida.getPrecioBase(), 0.001);
        assertEquals(Tamano.PEQUENA, bebida.getTamano());
    }

    @Test
    void losIdsNoSeConservanAlImportar() throws IOException {
        gestion.exportar(productos);

        List<Producto> importados = gestion.importar();

        for (int i = 0; i < productos.size(); i++) {
            assertNotEquals(productos.get(i).getId(), importados.get(i).getId(),
                    "El id del producto importado debe ser nuevo");
        }
        Pizza pizza = (Pizza) importados.get(0);
        assertEquals(0, pizza.getIngredientes().get(0).getId());
    }

    @Test
    void exportarYImportarUnaListaVaciaFunciona() throws IOException {
        gestion.exportar(List.of());

        assertTrue(gestion.importar().isEmpty());
    }

    @Test
    void importarSinFicheroLanzaIOException() {
        assertFalse(FICHERO.exists());

        assertThrows(IOException.class, () -> gestion.importar());
    }
}