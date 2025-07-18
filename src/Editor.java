public class Editor {
    public EventManager events;


    //Konstruktor tworzy menedżera zdarzeń z predefiniowanymi typami zdarzeń
    public Editor() {
        this.events = new EventManager("inflation", "sale", "price", "price_proposal", "price_response");
    }

    // Bank informuje klientów o inflacji
    public void notifyInflation(double currentInflation, CentralBank bank) {
        events.notifyObservers("inflation", currentInflation, bank);
        //System.out.println("notify Inflation");
    }

    // Powiadamia bank o sprzedaze
    public void notifySaleFromSellers(double sale, Seller seller) {
        events.notifyObservers("sale", sale, seller);
        //System.out.println("notify Sale From Sellers");
    }

    // Powiadamia klientów o zmianie ceny przez sprzedawcę
    public void notifyPriceFromSellers(double price, Seller seller) {
        events.notifyObservers("price", price, seller);
        //System.out.println("notify Price From Sellers");
    }

    // Powiadom o propozycji ceny
    public void notifyPriceProposal(double price, Seller seller) {
        events.notifyObservers("price_proposal", price, seller);
        //System.out.println("notify Price Proposal");
    }

    // Powiadom o odpowiedzi kupującego
    public void notifyPriceResponse(double response, Buyer buyer) {
        events.notifyObservers("price_response", response, buyer);
        //System.out.println("notify Price Response");
    }
}
