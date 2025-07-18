//Interfejs reprezentujący słuchacza zdarzeń (obserwatora), który reaguje na określony typ zdarzenia przekazywanego przez EventManager.
public interface EventListener {
    // Metoda wywoływana, gdy wystąpi dane zdarzenie.
    void update(String eventType, double amount, Object source);
}