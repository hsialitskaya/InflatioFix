import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EventManagerTest {
    private EventManager eventManager;
    private TestListener testListener1;
    private TestListener testListener2;

    @BeforeEach
    void setUp() {
        eventManager = new EventManager("sale", "price");
        testListener1 = new TestListener();
        testListener2 = new TestListener();
    }

    @Test
    void shouldNotifySubscribedListeners_eventCountIncreases() {
        eventManager.addObserver("sale", testListener1);
        Object source = new Object();

        eventManager.notifyObservers("sale", 100.0, source);

        // Weryfikacja: liczba zdarzeń powinna wzrosnąć do 1
        assertEquals(1, testListener1.getCount());
    }

    @Test
    void shouldNotifySubscribedListeners_eventTypeIsCorrect() {
        eventManager.addObserver("sale", testListener1);
        Object source = new Object();

        eventManager.notifyObservers("sale", 100.0, source);

        // Weryfikacja: typ zdarzenia powinien być "sale"
        assertEquals("sale", testListener1.getLastEventType());
    }

    @Test
    void shouldNotifySubscribedListeners_amountIsCorrect() {
        eventManager.addObserver("sale", testListener1);
        Object source = new Object();

        eventManager.notifyObservers("sale", 100.0, source);

        // Weryfikacja: kwota powinna być 100.0
        assertEquals(100.0, testListener1.getLastAmount());
    }

    @Test
    void shouldNotifySubscribedListeners_sourceIsCorrect() {
        eventManager.addObserver("sale", testListener1);
        Object source = new Object();

        eventManager.notifyObservers("sale", 100.0, source);

        // Weryfikacja: źródło zdarzenia powinno być takie samo, jak obiekt "source"
        assertSame(source, testListener1.getLastSource());
    }


    @Test
    void shouldNotNotifyUnsubscribedListeners() {
        eventManager.addObserver("sale", testListener1);
        eventManager.removeObserver("sale", testListener1);

        eventManager.notifyObservers("sale", 100.0, this);

        // Weryfikacja: 0 zdarzeń
        assertEquals(0, testListener1.getCount());
    }

    @Test
    void shouldHandleMultipleEventTypes_listener1ReceivesSaleEvent() {
        eventManager.addObserver("sale", testListener1);
        eventManager.addObserver("price", testListener2);

        eventManager.notifyObservers("sale", 200.0, this);
        eventManager.notifyObservers("price", 300.0, this);

        // Weryfikacja: listener1 powinien otrzymać jedno zdarzenie "sale"
        assertEquals(1, testListener1.getCount());
    }

    @Test
    void shouldHandleMultipleEventTypes_listener2ReceivesPriceEvent() {
        eventManager.addObserver("sale", testListener1);
        eventManager.addObserver("price", testListener2);

        eventManager.notifyObservers("sale", 200.0, this);
        eventManager.notifyObservers("price", 300.0, this);

        // Weryfikacja: listener2 powinien otrzymać jedno zdarzenie "price"
        assertEquals(1, testListener2.getCount());
    }


    @Test
    void shouldIgnoreUnknownEventTypes() {
        eventManager.addObserver("inflation", testListener1);
        eventManager.notifyObservers("inflation", 500.0, this);

        // Weryfikacja: 0 zdarzeń
        assertEquals(0, testListener1.getCount());
    }

    @Test
    void shouldAddListenerOnlyOnce() {
        eventManager.addObserver("sale", testListener1);
        eventManager.addObserver("sale", testListener1);

        eventManager.notifyObservers("sale", 100.0, this);

        // Weryfikacja: 1 zdarzenie
        assertEquals(1, testListener1.getCount());
    }

    static class TestListener implements EventListener {
        private int count = 0;
        private String lastEventType;
        private double lastAmount;
        private Object lastSource;

        @Override
        public void update(String eventType, double amount, Object source) {
            count++;
            lastEventType = eventType;
            lastAmount = amount;
            lastSource = source;
        }

        public int getCount() { return count; }
        public String getLastEventType() { return lastEventType; }
        public double getLastAmount() { return lastAmount; }
        public Object getLastSource() { return lastSource; }
    }
}