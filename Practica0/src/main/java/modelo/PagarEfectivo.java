package modelo;

public class PagarEfectivo implements Pagable {

    @Override
    public void pagar(double cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a pagar debe ser mayor que 0");
        }
        System.out.printf("Pago de %.2f€ pendiente de cobrar en efectivo en la entrega%n", cantidad);
    }
}
