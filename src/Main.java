import java.util.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;

public class Main {
    public static void main(String[] args) {
        List<TurnData> allTurns = new ArrayList<>();
        Editor editor = new Editor();
        CentralBank centralBank = new CentralBank(1000.0, editor);

        // Tworzenie sprzedawców
        Seller essentialSeller1 = new Seller("essential", 10.0, 2.0, editor, centralBank);
        Seller essentialSeller2 = new Seller("essential", 12.0, 1.5, editor, centralBank);
        Seller luxurySeller1 = new Seller("luxury", 50.0, 10.0, editor, centralBank);
        Seller luxurySeller2 = new Seller("luxury", 60.0, 15.0, editor, centralBank);
        List<Seller> sellers = new ArrayList<>(List.of(essentialSeller1, essentialSeller2, luxurySeller1, luxurySeller2));

        // Tworzenie kupujących
        Buyer buyer1 = new Buyer(500.0, 5, 3, editor, centralBank);
        Buyer buyer2 = new Buyer(300.0, 3, 1, editor, centralBank);
        List<Buyer> buyers = new ArrayList<>(List.of(buyer1, buyer2));

        // Symulacja 300 tur
        for (int turn = 0; turn <= 300; turn++) {
            System.out.println("\n===== Tura " + turn + " =====");

            // TEST 1: Wysoka marża od początku i brak elastyczności cenowej
            if (turn == 0) {
                sellers.forEach(s -> s.setMargin(s.getMargin() * 3));
                System.out.println("\n>>> TEST 3: Wysoka marża od początku (potrojona)");
            }

            // TEST 2: Obserwacja stabilizacji w pierwszych 50 turach
            if (turn == 0) {
                System.out.println(">>> TEST 1: Stabilizacja systemu w standardowych warunkach (obserwacja)");
            }

            // TEST 3: Szok pieniężny – podwojenie budżetów
            if (turn == 100) {
                buyers.forEach(b -> b.setBudget(b.getBudget() * 2));
                System.out.println("\n>>> SZOK PIENIĘŻNY: Budżet kupujących podwojony");
            }

            // TEST 4: Zakłócenie – usunięcie części graczy z rynku
            if (turn == 120) {
                sellers.removeIf(s -> s.getProductType().equals("luxury")); // usuwamy luksusowych sprzedawców
                if (!buyers.isEmpty()) {
                    buyers.remove(0); // usuwamy jednego kupującego
                }
                System.out.println("\n>>> TEST 2: Zakłócenie — usunięcie sprzedawców luksusowych i kupującego w turze 120");
            }

            // TEST 5: Wejście nowego gracza po kryzysie
            if (turn == 150) {
                Seller newSeller = new Seller("luxury", 50.0, 10.0, editor, centralBank);
                sellers.add(newSeller);

                Buyer newBuyer = new Buyer(400.0, 4, 2, editor, centralBank);
                buyers.add(newBuyer);

                System.out.println("\n>>> TEST 4: Wejście nowego sprzedawcy i kupującego po turze 150");
            }


            // Pierwsza tura – tylko stan początkowy
            if (turn == 0) {
                printStatus(centralBank, sellers, buyers);
                continue;
            }

            // Nowa tura – przygotowanie uczestników (jeśli więcej niż tura 1)
            if (turn > 1) {
                sellers.forEach(Seller::startNewTurn);
                buyers.forEach(Buyer::startNewTurn);
            }

            // Zakupy
            buyers.forEach(Buyer::makePurchases);

            // Wydruk stanu
            printStatus(centralBank, sellers, buyers);

            TurnData turnData = new TurnData();
            turnData.setTurn(turn);
            turnData.setInflation(centralBank.getCurrentInflation());
            turnData.setBankTurnover(centralBank.previousTurnover);

            // Zbierz dane sprzedawców
            for (Seller seller : sellers) {
                TurnData.SellerInfo sellerInfo = new TurnData.SellerInfo();
                sellerInfo.setType(seller.getProductType());
                sellerInfo.setPrice(seller.getPrice());
                sellerInfo.setMargin(seller.getMargin());
                sellerInfo.setStock(seller.getStock());
                turnData.getSellers().add(sellerInfo);
            }

            // Zbierz dane kupujących
            for (Buyer buyer : buyers) {
                TurnData.BuyerInfo buyerInfo = new TurnData.BuyerInfo();
                buyerInfo.setBudget(buyer.getBudget());
                buyerInfo.setEssentials(buyer.getPurchasedEssentials());
                buyerInfo.setLuxuries(buyer.getPurchasedLuxuries());
                turnData.getBuyers().add(buyerInfo);
            }

            allTurns.add(turnData);

            // Aktualizacja stanu banku (obliczenie inflacji itd.)
            centralBank.finishTurn();
        }

        try (FileWriter writer = new FileWriter("simulation.json")) {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();
            gson.toJson(allTurns, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printStatus(CentralBank bank, List<Seller> sellers, List<Buyer> buyers) {
        System.out.println("\n--- Stan systemu ---");
        System.out.printf("Inflacja: %.2f | Obrót banku: %.2f\n", bank.getCurrentInflation(), bank.previousTurnover);

        System.out.println("\nSprzedawcy:");
        for (Seller s : sellers) {
            System.out.printf(
                    "Typ: %-10s | Cena: %6.2f | Marża: %5.2f | Zapas: %3d\n",
                    s.getProductType(), s.getPrice(), s.getMargin(), s.getStock()
            );
        }

        System.out.println("\nKupujący:");
        for (Buyer b : buyers) {
            System.out.printf(
                    "Budżet: %6.2f | Podst: %2d/%2d | Luks: %2d/%2d\n",
                    b.getBudget(), b.getPurchasedEssentials(), b.getRequiredEssentials(),
                    b.getPurchasedLuxuries(), b.getDesiredLuxuries()
            );
        }
    }
}
