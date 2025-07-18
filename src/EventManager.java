import java.util.*;

// Klasa EventManager zarządza subskrypcjami i powiadamianiem obserwatorów (słuchaczy) na podstawie typu zdarzenia.
public class EventManager {
    // Mapa przechowująca listy słuchaczy dla każdego typu zdarzenia
    Map<String, List<EventListener>> listeners = new HashMap<>();


    // Konstruktor tworzy puste listy słuchaczy dla zadeklarowanych typów zdarzeń.
    public EventManager(String... operations) {
        for (String operation : operations) {
            this.listeners.put(operation, new ArrayList<>());
        }
    }

    //  Dodaje słuchacza (obserwatora) do danego typu zdarzenia.
    public void addObserver(String eventType, EventListener listener) {
        List<EventListener> users = listeners.get(eventType);
        if (users != null && !users.contains(listener)) {
            users.add(listener);
        }
    }

    // Usuwa słuchacza z danego typu zdarzenia.
    public void removeObserver(String eventType, EventListener listener) {
        List<EventListener> users = listeners.get(eventType);
        if (users != null) {
            users.remove(listener);
        }
    }

    // Powiadamia wszystkich zarejestrowanych słuchaczy danego typu zdarzenia.
    public void notifyObservers(String eventType, double amount, Object source) {
        List<EventListener> users = listeners.get(eventType);
        if (users != null) {
            for (EventListener listener : users) {
                listener.update(eventType, amount, source);
            }
        }
    }
}
