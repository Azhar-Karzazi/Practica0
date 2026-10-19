package modelo;

public class Bebida extends Producto {

    private Tamano tamano;

    public Bebida(String nombre, double precioBase, Tamano tamano) {
        super(nombre, precioBase);
        this.tamano = tamano == null ? Tamano.MEDIANA : tamano;
    }

    public Tamano getTamano() {
        return tamano;
    }

    public void setTamano(Tamano tamano) {
        this.tamano = tamano;
    }

    @Override
    public String toString() {
        return String.format("Bebida %s (%s) [precio=%.2f€]", getNombre(), tamano, getPrecioBase());
    }
}
