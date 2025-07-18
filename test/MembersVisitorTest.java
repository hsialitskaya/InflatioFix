import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MembersVisitorTest {
    private final MembersVisitor visitor = new MembersVisitor();
    private final Editor editor = new Editor();
    private final CentralBank centralBank = new CentralBank(1000.0, editor);

    @Test
    void visitBuyer_shouldIncreaseBudgetWhenLow() {
        Buyer buyer = new Buyer(5.0, 100, 0, editor, centralBank); // 5 < 10% z 100 (10)
        visitor.visitBuyer(buyer);
        assertEquals(7.5, buyer.getBudget()); // 5 * 1.5
    }

    @Test
    void visitBuyer_shouldNotIncreaseBudgetWhenSufficient() {
        Buyer buyer = new Buyer(20.0, 100, 0, editor, centralBank); // 20 > 10
        visitor.visitBuyer(buyer);
        assertEquals(20.0, buyer.getBudget());
    }

    @Test
    void visitSeller_shouldIncreaseMarginWhenStockIsLow() {
        Seller seller = new Seller("essential", 50.0, 10.0, editor, centralBank);
        seller.setStock(15); // < 20
        visitor.visitSeller(seller);
        assertEquals(10.5, seller.getMargin()); // 10 * 1.05
    }

    @Test
    void visitSeller_shouldIncreasePriceWhenStockIsLow() {
        Seller seller = new Seller("essential", 50.0, 10.0, editor, centralBank);
        seller.setStock(15); // < 20
        visitor.visitSeller(seller);
        assertEquals(60.5, seller.getPrice()); // (50 + 10.5) * 1.0
    }

    @Test
    void visitSeller_shouldNotChangeMarginWhenStockIsAdequate() {
        Seller seller = new Seller("luxury", 100.0, 20.0, editor, centralBank);
        seller.setStock(25); // >= 20
        visitor.visitSeller(seller);
        assertEquals(20.0, seller.getMargin());
    }

    @Test
    void visitSeller_shouldNotChangePriceWhenStockIsAdequate() {
        Seller seller = new Seller("luxury", 100.0, 20.0, editor, centralBank);
        seller.setStock(25); // >= 20
        visitor.visitSeller(seller);
        assertEquals(120.0, seller.getPrice()); // (100 + 20) * 1.0
    }
}