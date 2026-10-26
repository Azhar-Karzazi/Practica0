package modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvIgnore;

public class Ingrediente {

    @CsvIgnore
    @JsonIgnore
    private int id;

    @CsvBindByName
    private String nombre;

    @CsvBindByName(column = "Stock disponible")
    private int stock;

    @CsvBindByName
    private boolean esVegano;

    public Ingrediente(){}

    public Ingrediente(int id, String nombre, int stock, boolean esVegano) {
        this.id = id;
        this.nombre = nombre;
        this.stock = stock;
        this.esVegano = esVegano;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public boolean isEsVegano() {
        return esVegano;
    }

    public void setEsVegano(boolean esVegano) {
        this.esVegano = esVegano;
    }
}
