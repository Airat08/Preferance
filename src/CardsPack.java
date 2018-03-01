import java.util.ArrayList;
import java.util.List;

public class CardsPack {
    private List<Card> cardsPack;

    public CardsPack() {
        cardsPack = new ArrayList<>();
        createCardsPack();
    }

    public CardsPack(int size) {
        cardsPack = new ArrayList<>(size);
    }

    private void createCardsPack()
    {
        for (NameCard nameCard: NameCard.values()) {
            for (SuitCard suitCard: SuitCard.values()) {
                cardsPack.add(new Card(nameCard,suitCard));
            }
        }
    }

    public void add(Card card)
    {
        cardsPack.add(card);
    }

    public List<Card> getCardsPack() {
        return cardsPack;
    }
}
