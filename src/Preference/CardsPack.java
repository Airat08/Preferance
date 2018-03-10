package Preference;

import java.util.ArrayList;
import java.util.List;

public class CardsPack {
    private List<Card> cardsPack;
    private static final int COUNT_PACK_CARDS = 32;

    public CardsPack() {
        cardsPack = new ArrayList<>();
        createCardsPack();
    }

    public CardsPack(int size) {
        cardsPack = new ArrayList<>(size);
    }

    private void createCardsPack()
    {
        int count = 7;
        for (NameCard nameCard: NameCard.values()) {
            if (!nameCard.name().equals(NameCard.шесть.name())) {
                for (SuitCard suitCard : SuitCard.values()) {
                    if (!suitCard.name().equals(SuitCard.БК.name())) {
                        cardsPack.add(new Card(nameCard, suitCard, count));
                        count++;
                    }
                }
            }
        }
    }

    public void remove(Card card)
    {
        cardsPack.remove(card);
    }

    public void remove(Card card, Player player)
    {
        cardsPack.remove(card);
    }

    public void add(Card card)
    {
        cardsPack.add(card);
    }

    public List<Card> getCardsPack() {
        return cardsPack;
    }

    public static int getCountPackCards() {
        return COUNT_PACK_CARDS;
    }

}
