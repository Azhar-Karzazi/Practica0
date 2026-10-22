package utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvException;
import modelo.*;
import java.io.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class GestionDeArchivos {

    // ACTIVIDAD 1: Jackson (productos.json)
    public void exportarProductos(List<Producto> productos) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerFor(new TypeReference<List<Producto>>() {}).writeValue(new File("productos.json"), productos);
    }

    public List<Producto> importarProductos() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File("productos.json"), new TypeReference<List<Producto>>() {});
    }

    // ACTIVIDAD 2: Gson (pedidos.json)
    public void exportarPedidos(List<Pedido> pedidos) throws IOException {
        Gson gson = crearGson();
        try (FileWriter writer = new FileWriter("pedidos.json")) {
            gson.toJson(pedidos, writer);
        }
    }

    public List<Pedido> importarPedidos() throws IOException {
        Gson gson = crearGson();
        try (FileReader reader = new FileReader("pedidos.json")) {
            return Arrays.asList(gson.fromJson(reader, Pedido[].class));
        }
    }

    private Gson crearGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdaptador())
                .create();
    }

    // ACTIVIDAD 3: OpenCSV (ingredientes.csv)
    public void exportarIngredientes(List<Ingrediente> ingredientes) throws IOException, CsvException {
        try (FileWriter writer = new FileWriter("ingredientes.csv")) {
            StatefulBeanToCsv<Ingrediente> beanToCsv = new StatefulBeanToCsvBuilder<Ingrediente>(writer)
                    .withSeparator(';')
                    .build();
            beanToCsv.write(ingredientes);
        }
    }

    public List<Ingrediente> importarIngredientes() throws IOException {
        try (FileReader reader = new FileReader("ingredientes.csv")) {
            CsvToBean<Ingrediente> csvToBean = new CsvToBeanBuilder<Ingrediente>(reader)
                    .withType(Ingrediente.class)
                    .withSeparator(';')
                    .build();
            return csvToBean.parse();
        }
    }
}
