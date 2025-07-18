import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SellerTest {
    private CentralBank centralBank;
    private Editor editor;
    private Seller seller;


    @BeforeEach
    public void setUp() {
         editor = new Editor();
         centralBank = new CentralBank(1000.0, editor);
         seller = new Seller("essential", 10.0, 5.0, editor, centralBank);
    }

    @Test
    public void testPriceCalculation() {
        assertEquals((10.0 + 5.0) * centralBank.getCurrentInflation(), seller.getPrice());
    }

    @Test
    public void testStockInitialization() {
        assertEquals(100, seller.getStock());
    }

    @Test
    public void testMarginInitialization() {
        assertEquals(5.0, seller.getMargin());
    }

    @Test
    public void testProductTypeInitialization() {
        assertEquals("essential", seller.getProductType());
    }

    @Test
    public void testStockCannotBeNegative() {
        assertThrows(IllegalArgumentException.class, () -> seller.setStock(-10));
    }

    @Test
    public void testMarginCannotBeNegative() {
        assertThrows(IllegalArgumentException.class, () -> seller.setMargin(-5));
    }

    @Test
    public void testPriceCannotBeNegative() {
        assertThrows(IllegalArgumentException.class, () -> seller.setPrice(-1));
    }

    @Test
    public void testMaxQuantityPerTurnCannotBeNegative() {
        assertThrows(IllegalArgumentException.class, () -> seller.setMaxQuantityPerTurn(-5));
    }

    @Test
    public void testActualQuantityPerTurnCannotBeNegative() {
        assertThrows(IllegalArgumentException.class, () -> seller.setActualQuantityPerTurn(-3));
    }

    @Test
    public void testSellToBuyerReducesStock() {
        Buyer buyer = new Buyer(100.0, 10, 5, editor, centralBank);
        seller.sellToBuyer(buyer, 5);
        assertEquals(95, seller.getStock());
    }

    @Test
    public void testSellToBuyerIncreasesTurnSales() {
        Buyer buyer = new Buyer(100.0, 10, 5, editor, centralBank);
        seller.sellToBuyer(buyer, 5);
        assertEquals(5, seller.getActualQuantityPerTurn());
    }

    @Test
    public void testStartNewTurnResetsTurnSales() {
        seller.setActualQuantityPerTurn(10);
        seller.startNewTurn();
        assertEquals(0, seller.getActualQuantityPerTurn());
    }
}
