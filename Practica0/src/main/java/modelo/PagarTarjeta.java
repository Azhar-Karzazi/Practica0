package modelo;

public class PagarTarjeta implements Pagable {

    @Override
    public void pagar(double cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a pagar debe ser mayor que 0");
        }

        System.out.printf("Pago de %.2f€ realizado con tarjeta", cantidad);
    }

}
