public class MembersVisitor implements Visitor {

    public void visitBuyer(Buyer buyer) {
        // Sprawdź, czy budżet nie był już modyfikowany w tej turze
        if (!buyer.isBudgetAdjustedThisTurn()
                && buyer.getBudget() < buyer.getRequiredEssentials() * 0.1) {

            buyer.setBudget(buyer.getBudget() * 1.5);
            buyer.setBudgetAdjustedThisTurn(true); // Zablokuj kolejne modyfikacje
        }
    }

    public void visitSeller(Seller seller){
        // Aktualizuj parametry sprzedawcy po sprzedaży
        if (seller.getStock() < 20) { // Zwiększ cenę przy niskim zapasie
            seller.setMargin(seller.getMargin() * 1.05); // Zwiększ marżę o 5%
        }

        if (seller.getStock() == 0) {
            seller.setStock(100);
            seller.setMargin(seller.getMargin() * 1.5); // Zwiększ marżę o 50%
        }
    }
}
