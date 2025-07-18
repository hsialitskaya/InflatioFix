import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CentralBankTest {
    private Editor editor;
    private CentralBank centralBank;

    @BeforeEach
    public void setUp() {
        editor = new Editor();
        centralBank = new CentralBank(1000.0, editor);
    }

    @Test
    public void testInitialInflation() {
        assertEquals(1.0, centralBank.getCurrentInflation());
    }

    @Test
    public void testRegisterSeller() {
        Seller seller = new Seller("essential", 10.0, 5.0, editor, centralBank);
        centralBank.registerSeller(seller);
        assertTrue(centralBank.getRegisteredSellers().contains(seller));
    }

    @Test
    public void testRegisterBuyer() {
        Buyer buyer = new Buyer(500.0, 10, 5, editor, centralBank);
        centralBank.registerBuyer(buyer);
        assertTrue(centralBank.getRegisteredBuyers().contains(buyer));
    }

    @Test
    public void testSetInflation() {
        centralBank.setCurrentInflation(1.5);
        assertEquals(1.5, centralBank.getCurrentInflation());
    }

    @Test
    public void testInflationCannotBeNegative() {
        assertThrows(IllegalArgumentException.class, () -> centralBank.setCurrentInflation(-1.0));
    }

    @Test
    public void testPreviousTurnoverCannotBeNegative() {
        assertThrows(IllegalArgumentException.class, () -> centralBank.setPreviousTurnover(-100));
    }

    @Test
    public void testUpdateTurnover() {
        centralBank.recordSale(200.0);
        assertEquals(200.0, centralBank.previousTurnover);
    }

    @Test
    public void testPriceHistoryUpdate() {
        Seller seller = new Seller("luxury", 20.0, 10.0, editor, centralBank);
        centralBank.registerSeller(seller);
        centralBank.updatePriceHistory(seller, 35.0, "luxury");
        assertTrue(centralBank.getLuxuryPriceHistory().get(seller).contains(35.0));
    }

    @Test
    public void testComputeAveragePriceChange() {
        Seller seller = new Seller("essential", 10.0, 5.0, editor, centralBank);
        editor.notifyPriceFromSellers(40.0, seller);
        assertTrue(centralBank.computeAveragePriceChange(centralBank.getEssentialPriceHistory()) > 0);
    }

    @Test
    public void testFinishTurnResetsPendingImpact() {
        centralBank.finishTurn();
        assertEquals(0.0, centralBank.getPendingPriceImpact());
    }
}
