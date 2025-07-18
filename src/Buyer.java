import java.util.*;
import java.util.stream.Collectors;

public class Buyer implements EventListener, Member {
    private double budget;
    private int requiredEssentials;
    private int desiredLuxuries;

    private double luxuryThreshold = 1.2;

    private int purchasedEssentials = 0;
    private int purchasedLuxuries = 0;

    private boolean budgetAdjustedThisTurn = false;

    private final Editor editor;
    private final CentralBank centralBank;

    public Buyer(double budget, int requiredEssentials, int desiredLuxuries, Editor editor, CentralBank centralBank) {
        this.budget = budget;
        this.requiredEssentials = requiredEssentials;
        this.desiredLuxuries = desiredLuxuries;

        this.editor = editor;
        this.centralBank = centralBank;

        editor.events.addObserver("inflation", this);
        editor.events.addObserver("price_proposal", this);

        centralBank.registerBuyer(this);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitBuyer(this);
    }

    // EventListener
    @Override
    public void update(String eventType, double amount, Object source) {
        if ("inflation".equals(eventType)) {
            handleInflationUpdate(amount);
        } else if ("price_proposal".equals(eventType)) {
            handlePriceProposal(source, amount);
        } else {
            throw new IllegalArgumentException("Nieobsługiwane zdarzenie: " + eventType);
        }
    }

    public void handleInflationUpdate(double newInflation) {
       setLuxuryThreshold(this.luxuryThreshold * newInflation);
    }

    private void handlePriceProposal(Object source, double price) {
        if (!(source instanceof Seller)) {
            throw new IllegalArgumentException("Nieprawidłowe źródło zdarzenia");
        }
        Seller seller = (Seller) source;

        String type = seller.getProductType();
        int remainingToBuy;

        if ("essential".equalsIgnoreCase(type)) {
            remainingToBuy = requiredEssentials - purchasedEssentials;
        } else if ("luxury".equalsIgnoreCase(type)) {
            remainingToBuy = desiredLuxuries - purchasedLuxuries;
        } else {
            return; // nieznany typ produktu, ignoruj
        }

        double totalCost = price * remainingToBuy;
        double chanceToAccept;
        if (totalCost <= budget) {
            chanceToAccept = 0.9; // 90% szansy na akceptację
        } else {
            chanceToAccept = 0.1; // 10% szansy na akceptację mimo braku środków
        }

        boolean accepted = Math.random() < chanceToAccept;
        double response = accepted ? 1 : 0;

        editor.notifyPriceResponse(response, this);
    }


    public void makePurchases() {
        purchaseEssential("essential");
        purchaseEssential("luxury");
    }

    public void purchaseEssential(String type) {
        if (!"essential".equalsIgnoreCase(type) && !"luxury".equalsIgnoreCase(type)) {
            throw new IllegalArgumentException("Nieznany typ produktu: " + type);
        }

        boolean isEssential = "essential".equalsIgnoreCase(type);
        if ((isEssential && purchasedEssentials >= requiredEssentials) ||
                (!isEssential && purchasedLuxuries >= desiredLuxuries)) {
            return;
        }

        List<Seller> sellers = centralBank.getRegisteredSellers().stream()
                .filter(seller -> type.equalsIgnoreCase(seller.getProductType()))
                .collect(Collectors.toList());

        if (!isEssential) {
            sellers = sellers.stream()
                    .filter(this::isValidLuxuryOffer)
                    .collect(Collectors.toList());
        }

        if (sellers.isEmpty()) return;

        Map<Seller, List<Double>> priceHistory = isEssential
                ? centralBank.getEssentialPriceHistory()
                : centralBank.getLuxuryPriceHistory();

        Seller cheapest = findCheapestSeller(sellers, priceHistory);
        if (cheapest != null) {
            double price = getLastPrice(priceHistory.get(cheapest));
            if (budget >= price) {
                cheapest.sellToBuyer(this, 1);
            }
        }
    }

    private Seller findCheapestSeller(List<Seller> sellers, Map<Seller, List<Double>> priceHistory) {
        return sellers.stream()
                .filter(seller -> {
                    List<Double> prices = priceHistory.get(seller);
                    return prices != null && !prices.isEmpty();
                })
                .min(Comparator.comparingDouble(seller -> getLastPrice(priceHistory.get(seller))))
                .orElse(null);
    }

    private double getLastPrice(List<Double> prices) {
        return prices.get(prices.size() - 1);
    }


    public boolean isValidLuxuryOffer(Seller seller) {
        List<Double> prices = centralBank.getLuxuryPriceHistory().getOrDefault(seller, Collections.emptyList());
        if (prices.size() < 3) return false;

        double first = prices.get(0);
        double second = prices.get(1);
        double third = prices.get(2);
        return first >= second && second >= third && third <= (seller.getPrice() * luxuryThreshold);
    }

    public void handlePurchase(int quantity, double currentPrice, String type) {
        double totalCost = currentPrice * quantity;
        if ("essential".equals(type)) {
            purchasedEssentials += quantity;
        } else if ("luxury".equals(type)) {
            purchasedLuxuries += quantity;
        }

        budget -= totalCost;
    }

    public boolean isBudgetAdjustedThisTurn() {
        return budgetAdjustedThisTurn;
    }


    public void startNewTurn() {
        setLuxuryThreshold(1.2);
        setBudgetAdjustedThisTurn(false);

    }

    // Gettery
    public double getBudget() { return budget; }
    public int getRequiredEssentials() { return requiredEssentials; }
    public int getDesiredLuxuries() { return desiredLuxuries; }
    public double getLuxuryThreshold() { return luxuryThreshold; }
    public int getPurchasedEssentials() { return purchasedEssentials; }
    public int getPurchasedLuxuries() { return purchasedLuxuries; }


    // Settery
    public void setBudget(double budget) {
        if (budget < 0) throw new IllegalArgumentException("Budżet nie może być ujemny");
        this.budget = budget;
    }

    public void setRequiredEssentials(int requiredEssentials) {
        if (requiredEssentials < 0) throw new IllegalArgumentException("Wartość nie może być ujemna");
        this.requiredEssentials = requiredEssentials;
    }

    public void setDesiredLuxuries(int desiredLuxuries) {
        if (desiredLuxuries < 0) throw new IllegalArgumentException("Wartość nie może być ujemna");
        this.desiredLuxuries = desiredLuxuries;
    }

    public void setLuxuryThreshold(double luxuryThreshold) {
        this.luxuryThreshold = luxuryThreshold;
    }

    public void setBudgetAdjustedThisTurn(boolean adjusted) {
        this.budgetAdjustedThisTurn = adjusted;
    }
}