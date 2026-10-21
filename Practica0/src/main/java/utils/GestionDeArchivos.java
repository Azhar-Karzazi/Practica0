package utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import modelo.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GestionDeArchivos {
    private static File fichero = new File("productos.json");
    private final ObjectMapper mapper = new ObjectMapper();

    public void exportar(List<Producto> productos) throws IOException {
        mapper.writerFor(new TypeReference<List<Producto>>() {}).writeValue(fichero, productos);
    }

    public List<Producto> importar() throws IOException {
        return mapper.readValue(fichero, new TypeReference<List<Producto>>() {});
    }


}
