import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BuyerTest {

    private Buyer buyer;

    @BeforeEach
    public void setUp() {
        Editor editor = new Editor();
        CentralBank centralBank = new CentralBank(1000.0, editor);
        buyer = new Buyer(100.0, 10, 5, editor, centralBank);
    }

    @Test
    public void testBudgetInitialization() {
        assertEquals(100.0, buyer.getBudget());
    }

    @Test
    public void testRequiredEssentialsInitialization() {
        assertEquals(10, buyer.getRequiredEssentials());
    }

    @Test
    public void testDesiredLuxuriesInitialization() {
        assertEquals(5, buyer.getDesiredLuxuries());
    }

    @Test
    public void testBudgetCannotBeNegative() {
        assertThrows(IllegalArgumentException.class, () -> buyer.setBudget(-10));
    }

    @Test
    public void testRequiredEssentialsCannotBeNegative() {
        assertThrows(IllegalArgumentException.class, () -> buyer.setRequiredEssentials(-5));
    }

    @Test
    public void testDesiredLuxuriesCannotBeNegative() {
        assertThrows(IllegalArgumentException.class, () -> buyer.setDesiredLuxuries(-3));
    }

    @Test
    public void testPurchaseEssentialUpdatesEssentialsCount() {
        buyer.handlePurchase(2, 5.0, "essential");
        assertEquals(2, buyer.getPurchasedEssentials());
    }

    @Test
    public void testPurchaseLuxuryUpdatesLuxuriesCount() {
        buyer.handlePurchase(3, 10.0, "luxury");
        assertEquals(3, buyer.getPurchasedLuxuries());
    }

    @Test
    public void testPurchaseReducesBudget() {
        buyer.handlePurchase(2, 5.0, "essential");
        assertEquals(90.0, buyer.getBudget());
    }

    @Test
    public void testStartNewTurnResetsValues() {
        buyer.setLuxuryThreshold(1.5);
        buyer.setBudgetAdjustedThisTurn(true);
        buyer.startNewTurn();
        assertEquals(1.2, buyer.getLuxuryThreshold());
    }
}
