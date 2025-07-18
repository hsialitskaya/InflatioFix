import java.util.Locale;

public class Seller implements EventListener, Member {
    private final String productType;
    private final double cost;
    private double margin;
    private double price;
    private double lastReportedPrice;

    private int stock = 50;
    private int maxQuantityPerTurn = 10;
    private int actualQuantityPerTurn = 0;

    // Flaga oczekiwania na odpowiedź
    private boolean awaitingPriceResponse = false;
    private boolean newTurnMarging = false;

    private static final int MAX_CONSECUTIVE_NO_SALES_TURNS = 10;
    private int consecutiveNoSalesTurns = 0;
    private int previousTurnSales = 0;

    private final Editor editor;
    private final CentralBank centralBank;

    public Seller(String productType, double cost, double margin, Editor editor, CentralBank centralBank) {
        this.productType = normalizeProductType(productType);
        validateProductType(this.productType);

        this.cost = cost;
        this.margin = margin;

        this.editor = editor;
        this.centralBank = centralBank;

        editor.events.addObserver("inflation", this);
        editor.events.addObserver("price_response", this);

        recalculatePrice();
        this.lastReportedPrice = price;
        centralBank.registerSeller(this);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitSeller(this);
    }

    @Override
    public void update(String eventType, double amount, Object source) {
        if ("inflation".equals(eventType)) {
            handleInflationUpdate();
        } else if ("price_response".equals(eventType)) {
            handlePriceResponse(amount, source);
        } else {
            throw new IllegalArgumentException("Nieobsługiwane zdarzenie: " + eventType);
        }
    }


    public void handleInflationUpdate() {
        recalculatePrice();
    }

    private void handlePriceResponse(double response, Object source) {
        if (source instanceof Buyer) {
            if (response == 0) { // 0 = odrzucenie ceny
                awaitingPriceResponse = true;
                this.setMargin(this.margin * 0.95); // Zmniejsz marżę o 5%
            }
            awaitingPriceResponse = false;
        }
    }

    public void sellToBuyer(Buyer buyer, int requestedQuantity) {
        int sellableQuantity = calculateSellableQuantity(requestedQuantity);
        if (sellableQuantity == 0) return;

        updateStock(sellableQuantity);
        updateTurnSales(sellableQuantity);
        processTransaction(buyer, sellableQuantity);
        this.setMargin(this.margin * 1.02);
    }

    public int calculateSellableQuantity(int requested) {
        return Math.min(
                Math.min(requested, stock),
                maxQuantityPerTurn - actualQuantityPerTurn
        );
    }

    private void updateStock(int sold) {
        setStock(stock - sold);
    }

    private void updateTurnSales(int sold) {
        setActualQuantityPerTurn(actualQuantityPerTurn + sold);
    }

    private void processTransaction(Buyer buyer, int quantity) {
        double totalPrice = quantity * price;
        buyer.handlePurchase(quantity, price, productType);
        notifyEditor(totalPrice);
    }

    private void notifyEditor(double revenue) {
        if (revenue > 0) {
            editor.notifySaleFromSellers(revenue, this);
        }
    }

    public void recalculatePrice() {
        setPrice((cost + margin) * centralBank.getCurrentInflation());
    }

    private String normalizeProductType(String rawType) {
        if (rawType == null) {
            throw new IllegalArgumentException("Typ produktu nie może być null");
        }
        return rawType.trim().toLowerCase(Locale.ROOT);
    }

    private void validateProductType(String type) {
        if (!"essential".equals(type) && !"luxury".equals(type)) {
            throw new IllegalArgumentException("Nieprawidłowy typ produktu: " + type);
        }
    }

    public void startNewTurn() {
        // Zapisz liczbę sprzedaży z poprzedniej tury przed resetem
        previousTurnSales = actualQuantityPerTurn;

        // Resetuj licznik sprzedaży na nową turę
        this.actualQuantityPerTurn = 0;

        // Sprawdź, czy w poprzedniej turze nie było sprzedaży
        if (previousTurnSales == 0) {
            consecutiveNoSalesTurns++;
            if (consecutiveNoSalesTurns >= MAX_CONSECUTIVE_NO_SALES_TURNS) {
                newTurnMarging = true;
                this.setMargin(this.margin * 0.9); // Zmniejsz marżę o 10%
                consecutiveNoSalesTurns = 0;     // Zresetuj licznik
                newTurnMarging = false;
            }
        } else {
            consecutiveNoSalesTurns = 0; // Zresetuj, jeśli była sprzedaż
        }

        // Sprawdź, czy cena się zmieniła i wyślij powiadomienie
        if (Math.abs(price - lastReportedPrice) > 0.001) { // Tolerancja dla błędów zaokrągleń
            editor.notifyPriceFromSellers(price, this);
            lastReportedPrice = price;
        }
    }

    // Gettery i settery
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public double getCost() { return cost; }
    public String getProductType() { return productType; }
    public double getMargin() { return margin; }
    public int getMaxQuantityPerTurn() { return maxQuantityPerTurn; }
    public int getActualQuantityPerTurn() { return actualQuantityPerTurn; }

    // Settery
    public void setStock(int stock) {
        if (stock < 0) throw new IllegalArgumentException("Stan magazynu nie może być ujemny");
        this.stock = stock;
    }

    public void setMargin(double margin) {
        if (margin < 0) throw new IllegalArgumentException("Marża nie może być ujemna");
        this.margin = margin;
        recalculatePrice();
        if (!newTurnMarging && !awaitingPriceResponse) {
            editor.notifyPriceProposal(price, this);
        }
        awaitingPriceResponse = true;

    }

    public void setPrice(double price) {
        if (price < 0) throw new IllegalArgumentException("Cena nie może być ujemna");
        this.price = price;
    }

    public void setMaxQuantityPerTurn(int limit) {
        if (limit < 0) throw new IllegalArgumentException("Limit nie może być ujemny");
        this.maxQuantityPerTurn = limit;
    }

    public void setActualQuantityPerTurn(int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("Ilość nie może być ujemna");
        this.actualQuantityPerTurn = quantity;
    }

    public boolean isAwaitingPriceResponse() {
        return awaitingPriceResponse;
    }

    public void setAwaitingPriceResponse(boolean awaitingPriceResponse) {
        this.awaitingPriceResponse = awaitingPriceResponse;
    }
}