import java.util.ArrayList;
import java.util.List;

public class TurnData {
    private int turn;
    private double inflation;
    private double bankTurnover;
    private List<SellerInfo> sellers = new ArrayList<>();
    private List<BuyerInfo> buyers = new ArrayList<>();

    // Gettery i settery dla pól głównych
    public int getTurn() { return turn; }
    public void setTurn(int turn) { this.turn = turn; }

    public double getInflation() { return inflation; }
    public void setInflation(double inflation) { this.inflation = inflation; }

    public double getBankTurnover() { return bankTurnover; }
    public void setBankTurnover(double bankTurnover) { this.bankTurnover = bankTurnover; }

    public List<SellerInfo> getSellers() { return sellers; }
    public List<BuyerInfo> getBuyers() { return buyers; }

    public static class SellerInfo {
        private String type;
        private double price;
        private double margin;
        private int stock;

        // Gettery i settery
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }

        public double getMargin() { return margin; }
        public void setMargin(double margin) { this.margin = margin; }

        public int getStock() { return stock; }
        public void setStock(int stock) { this.stock = stock; }
    }

    public static class BuyerInfo {
        private double budget;
        private int essentials;
        private int luxuries;

        // Gettery i settery
        public double getBudget() { return budget; }
        public void setBudget(double budget) { this.budget = budget; }

        public int getEssentials() { return essentials; }
        public void setEssentials(int essentials) { this.essentials = essentials; }

        public int getLuxuries() { return luxuries; }
        public void setLuxuries(int luxuries) { this.luxuries = luxuries; }
    }
}