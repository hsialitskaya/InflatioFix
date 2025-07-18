import java.util.*;

public class CentralBank implements EventListener {
    // Docelowy poziom dochodów z podatków
    private final double targetTaxRevenue;
    private double currentInflation = 1.0;

    public double previousTurnover = 0.0;
    private final List<Double> turnoverHistory = new LinkedList<>();


    private final List<Seller> registeredSellers = new ArrayList<>();
    private final List<Buyer> registeredBuyers = new ArrayList<>();
    private final Map<Seller, List<Double>>  essentialPriceHistory = new HashMap<>();
    private final Map<Seller, List<Double>>  luxuryPriceHistory = new HashMap<>();
    private static final int PRICE_HISTORY_LIMIT = 5;


    private double pendingPriceImpact = 0.0;
    // Próg zmiany ceny (5%)
    private static final double PRICE_CHANGE_THRESHOLD = 0.05;
    // Kontrola maksymalnej zmiany inflacji w jednej turze
    private static final double MAX_INFLATION_CHANGE_RATE = 0.1;
    // Maksymalny dopuszczalny spadek wartości w reakcji na ceny
    private static final double MAX_DECLINE = 0.99;
    // Maksymalny wzrost w reakcji na zmiany cen
    private static final double MAX_INCREASE = 10.0;
    private int consecutiveZeroTurnovers = 0;


    private boolean isCalculatingInflation = false;

    private final Editor editor;


    public CentralBank(double targetTaxRevenue, Editor editor) {
        this.targetTaxRevenue = targetTaxRevenue;
        this.editor = editor;
        editor.events.addObserver("sale", this);
        editor.events.addObserver("price", this);
    }

    // EventListener
    @Override
    public void update(String eventType, double amount, Object source) {
        if ("sale".equals(eventType)) {
            handleSaleEvent(amount);
        } else if ("price".equals(eventType)) {
            handlePriceEvent(source, amount);
        }  else {
            throw new IllegalArgumentException("Nieobsługiwane zdarzenie: " + eventType);
        }
    }

    // Sale event
    private void handleSaleEvent(double amount) {
        recordSale(amount);
    }

    public void recordSale(double amount) {
        setPreviousTurnover(previousTurnover + amount);
    }


    // Price event
    private void handlePriceEvent(Object source, double amount) {
        if (!(source instanceof Seller)) {
            throw new IllegalArgumentException("Nieprawidłowe źródło zdarzenia");
        }
        Seller seller = (Seller) source;
        processSellerPrice(seller, amount);
    }

    private void processSellerPrice(Seller seller, double price) {
        String productType = seller.getProductType().toLowerCase();
        if ("essential".equals(productType)) {
            updatePriceHistory(seller, price, "essential");
            reactToPriceChanges("essential");
        } else if ("luxury".equals(productType)) {
            updatePriceHistory(seller, price, "luxury");
            reactToPriceChanges("luxury");
        } else {
            throw new IllegalArgumentException("Nieobsługiwany typ produktu: " + seller.getProductType());
        }
    }


    public void updatePriceHistory(Seller seller, double price, String type) {
        Map<Seller, List<Double>> targetMap;

        if ("essential".equalsIgnoreCase(type)) {
            targetMap = essentialPriceHistory;
        } else if ("luxury".equalsIgnoreCase(type)) {
            targetMap = luxuryPriceHistory;
        } else {
            // Jeśli typ jest nieznany, nie robimy nic
            return;
        }

        // Pobierz lub utwórz listę cen dla danego sprzedawcy
        List<Double> priceList = targetMap.computeIfAbsent(seller, k -> new ArrayList<>());

        // Jeśli lista przekracza limit, usuń najstarszy wpis
        if (priceList.size() >= PRICE_HISTORY_LIMIT) {
            priceList.remove(0);
        }
        // Dodaj nową cenę
        priceList.add(price);
    }


    private void reactToPriceChanges(String type) {
        Map<Seller, List<Double>> targetMap;

        if ("essential".equalsIgnoreCase(type)) {
            targetMap = essentialPriceHistory;
        } else if ("luxury".equalsIgnoreCase(type)) {
            targetMap = luxuryPriceHistory;
        } else {
            // Jeśli typ jest nieznany, nie robimy nic
            return;
        }
        double avgChange = computeAveragePriceChange(targetMap);
        reactToPriceChange(avgChange, type);
    }


    public double computeAveragePriceChange(Map<Seller, List<Double>> priceHistoryMap) {
        double totalChange = 0.0;
        int sellerCount = 0;

        for (Map.Entry<Seller, List<Double>> entry : priceHistoryMap.entrySet()) {
            List<Double> prices = entry.getValue();
            if (prices.size() < 2) {
                continue;
            }

            double sellerTotalChange = 0.0;
            int count = 0;

            for (int i = 1; i < prices.size(); i++) {
                double previous = prices.get(i - 1);
                double current = prices.get(i);
                if (previous > 0.01) {
                    sellerTotalChange += (current - previous) / previous;
                    count++;
                }
            }

            if (count > 0) {
                totalChange += sellerTotalChange / count; // średnia zmiana u sprzedawcy
                sellerCount++;
            }
        }

        return sellerCount > 0 ? totalChange / sellerCount : 0.0;
    }


        //    Jeśli średnia zmiana > próg → podniesienie inflacji
        //    Jeśli średnia zmiana < próg → obniżenie inflacji

    public void reactToPriceChange(double avgChange, String category) {
        if (previousTurnover == 0) return;
        if (Math.abs(avgChange) <= PRICE_CHANGE_THRESHOLD) return;
        if (isCalculatingInflation) return;

        double impactMultiplier = getImpactMultiplier(category);
        // Skorygowany wzrost
        double adjustedChange = avgChange * impactMultiplier;
        adjustedChange = Math.max(adjustedChange, -MAX_DECLINE);
        adjustedChange = Math.min(adjustedChange, MAX_INCREASE);

        pendingPriceImpact += adjustedChange;
    }

    public double getImpactMultiplier(String category) {
        if ("essential".equalsIgnoreCase(category)) {
            return 1.5;
        } else if ("luxury".equalsIgnoreCase(category)) {
            return 0.5;
        }
        throw new IllegalArgumentException("Nieznana kategoria: " + category);
    }



    public double validateInflation(double value) {
        return (value <= 0 || !Double.isFinite(value)) ? 0.01 : value;
    }

    // Koniec tury
    public void finishTurn() {
        if (isCalculatingInflation) return;

        try {
            isCalculatingInflation = true;

            updateTurnoverHistory();
            double avgTurnover = calculateAverageTurnover();

            double baseInflation;
            if (previousTurnover == 0) {
                baseInflation = Math.max(currentInflation * 0.9, 1.0);
                consecutiveZeroTurnovers++;
                if (consecutiveZeroTurnovers >= 5) { // Resetuj historię po 5 turach bez obrotu
                    turnoverHistory.clear();
                    for (Buyer buyer : registeredBuyers) {
                        int randomIncrease = (int) (Math.random() * 5) + 1; // Random 1-5

                        int currentRequired = buyer.getRequiredEssentials();
                        buyer.setRequiredEssentials(currentRequired + randomIncrease);

                        int currentLuxury = buyer.getDesiredLuxuries();
                        buyer.setDesiredLuxuries(currentLuxury + randomIncrease);
                    }
                }
            } else {
                baseInflation = calculateAdjustedInflation(avgTurnover);
                consecutiveZeroTurnovers = 0;
            }

            // Wpływ zmian cen (pendingPriceImpact)
            double targetInflation = baseInflation * (1 + pendingPriceImpact);

            // Ogranicz zmianę inflacji do +/- 10% w stosunku do currentInflation
            double maxChange = currentInflation * MAX_INFLATION_CHANGE_RATE;

            double delta = targetInflation - currentInflation;
            if (delta > maxChange) {
                targetInflation = currentInflation + maxChange;
            } else if (delta < -maxChange) {
                targetInflation = currentInflation - maxChange;
            }

            targetInflation = validateInflation(targetInflation);
            setCurrentInflation(targetInflation);
            editor.notifyInflation(currentInflation, this);

            resetTurnover();
            pendingPriceImpact = 0.0; // reset wpływu po zakończeniu tury

            MembersVisitor turnVisitor = new MembersVisitor();
            applyVisitorToParticipants(turnVisitor);

        } finally {
            isCalculatingInflation = false;
        }
    }


    private void updateTurnoverHistory() {
        turnoverHistory.add(previousTurnover);
        if (turnoverHistory.size() >= PRICE_HISTORY_LIMIT) {
            turnoverHistory.remove(0);
        }
    }

    private double calculateAverageTurnover() {
        return turnoverHistory.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    private double calculateAdjustedInflation(double avgTurnover) {
        double rawInflation = targetTaxRevenue / avgTurnover;
        return limitInflationChange(rawInflation);
    }

    private double limitInflationChange(double rawInflation) {
        double allowedChange = currentInflation * MAX_INFLATION_CHANGE_RATE;
        if (rawInflation - currentInflation > allowedChange) {
            return currentInflation + allowedChange;
        }
        if (currentInflation - rawInflation > allowedChange) {
            return currentInflation - allowedChange;
        }
        return rawInflation;
    }

    private void resetTurnover() {
        setPreviousTurnover(0.0);
    }


    private void applyVisitorToParticipants(MembersVisitor visitor) {
        List<Seller> allSellers = getRegisteredSellers();  // Metoda do pobrania sprzedawców
        List<Buyer> allBuyers = getRegisteredBuyers();     // Metoda do pobrania kupujących

        for (Seller seller : allSellers) {
            seller.accept(visitor);
        }

        for (Buyer buyer : allBuyers) {
            buyer.accept(visitor);
        }
    }

    // Rejestracja spredawcow
    public void registerSeller(Seller seller) {
        registeredSellers.add(seller);

        String productType = seller.getProductType();

        if ("essential".equals(productType)) {
            essentialPriceHistory.putIfAbsent(seller, new ArrayList<>());
            essentialPriceHistory.get(seller).add(seller.getPrice());
        } else if ("luxury".equals(productType)) {
            luxuryPriceHistory.putIfAbsent(seller, new ArrayList<>());
            luxuryPriceHistory.get(seller).add(seller.getPrice());
        } else {
            throw new IllegalArgumentException("Nieznany typ produktu: " + productType);
        }
    }

    public void registerBuyer(Buyer buyer) {
        registeredBuyers.add(buyer);
    }


    // Gettery
    public double getCurrentInflation() { return currentInflation; }

    public List<Seller> getRegisteredSellers() {
        return registeredSellers;
    }

    public List<Buyer> getRegisteredBuyers() {
        return registeredBuyers;
    }

    public  Map<Seller, List<Double>> getEssentialPriceHistory() {
        return essentialPriceHistory;
    }

    public  Map<Seller, List<Double>> getLuxuryPriceHistory() {
        return luxuryPriceHistory;
    }

    public double getPendingPriceImpact() { return pendingPriceImpact; }

    //Settery
    public void setCurrentInflation(double inflation) {
        if (inflation <= 0) throw new IllegalArgumentException("Inflacja musi być dodatnia");
        this.currentInflation = inflation;
    }

    public void setPreviousTurnover(double turnover) {
        if (turnover < 0) throw new IllegalArgumentException("Obrót nie może być ujemny");
        this.previousTurnover = turnover;
    }
}