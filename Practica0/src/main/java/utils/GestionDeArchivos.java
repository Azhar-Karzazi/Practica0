package utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvException;
import modelo.Ingrediente;
import modelo.Pedido;
import modelo.Producto;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GestionDeArchivos {

    // ACTIVIDAD 1: Jackson (productos.json)
    public static void exportarProductos(List<Producto> productos) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerFor(new TypeReference<List<Producto>>() {}).writeValue(new File("productos.json"), productos);
    }

    public static List<Producto> importarProductos() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File fichero = new File("productos.json");

        if (!fichero.exists() || fichero.length() == 0) {
            return new ArrayList<>();
        }
        return mapper.readValue(fichero, new TypeReference<List<Producto>>() {});
    }

    // ACTIVIDAD 2: Gson (pedidos.json)
    public static void exportarPedidos(List<Pedido> pedidos) throws IOException {
        Gson gson = crearGson();
        try (FileWriter writer = new FileWriter("pedidos.json")) {
            gson.toJson(pedidos, writer);
        }
    }

    public static List<Pedido> importarPedidos() throws IOException {
        File fichero = new File("pedidos.json");

        if (!fichero.exists() || fichero.length() == 0) {
            return new ArrayList<>();
        }

        Gson gson = crearGson();
        try (FileReader reader = new FileReader(fichero)) {
            Pedido[] pedidos = gson.fromJson(reader, Pedido[].class);
            return pedidos == null ? new ArrayList<>() : Arrays.asList(pedidos);
        }
    }

    private static Gson crearGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdaptador())
                .create();
    }

    // ACTIVIDAD 3: OpenCSV (ingredientes.csv)
    public static void exportarIngredientes(List<Ingrediente> ingredientes) throws IOException, CsvException {
        try (FileWriter writer = new FileWriter("ingredientes.csv")) {
            StatefulBeanToCsv<Ingrediente> beanToCsv = new StatefulBeanToCsvBuilder<Ingrediente>(writer)
                    .withSeparator(';')
                    .build();
            beanToCsv.write(ingredientes);
        }
    }

    public static List<Ingrediente> importarIngredientes() throws IOException {
        try (FileReader reader = new FileReader("ingredientes.csv")) {
            CsvToBean<Ingrediente> csvToBean = new CsvToBeanBuilder<Ingrediente>(reader)
                    .withType(Ingrediente.class)
                    .withSeparator(';')
                    .build();
            return csvToBean.parse();
        }
    }
}
