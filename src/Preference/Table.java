package Preference;

import java.util.HashMap;
import java.util.Map;

public class Table {
    private GameMode mode;
    private HashMap<Player, Card> cardsPack;
    private HashMap<Player, Card> winnerRound;//победитель этого раунда
    private SuitCard suit;
    private static final int INCREMENT_POINT_PLAYER = 1;

    public Table(GameMode mode, SuitCard suit) {
        this.mode = mode;
        this.suit = suit;
        winnerRound = new HashMap<>();
    }

    public HashMap<Player, Card> solveWinnerSession(HashMap<Player, Card> cardsPack)
    {
        winnerRound.clear();
        this.cardsPack = cardsPack;
        HashMap<Player, Card> tempSortCardsPack = sort();
        if (!tempSortCardsPack.isEmpty())
        {
            Player player = tempSortCardsPack.entrySet().iterator().next().getKey();
            Card card = tempSortCardsPack.entrySet().iterator().next().getValue();
            for (Map.Entry entry:tempSortCardsPack.entrySet()) {
                if (!((Card)entry.getValue()).getSuit().equals(suit))
                {
                    if (((Card)entry.getValue()).getRank() > card.getRank())
                    {
                        player = (Player) entry.getKey();
                        card = (Card) entry.getValue();
                    }
                }
                else if (!card.getSuit().equals(suit))
                {
                    player = (Player) entry.getKey();
                    card = (Card) entry.getValue();
                }
                else
                {
                    if (((Card)entry.getValue()).getRank() > card.getRank())
                    {
                        player = (Player) entry.getKey();
                        card = (Card) entry.getValue();
                    }
                }
            }
            player.addPoint(INCREMENT_POINT_PLAYER);
            winnerRound.put(player,card);
            return winnerRound;
        }
        return null;
    }

    //Метод для того чтобы вернуть остортированные полученные карты по порядку их значимости
    private HashMap<Player, Card> sort()//т.е. чтоб было (не козырная, не козырная, козырная), а не так (не козырная, козырная, не козырная)
    {
        if (!cardsPack.isEmpty()) {
            HashMap<Player, Card> tempHashMap = new HashMap<>();
            for (Map.Entry entry : cardsPack.entrySet()) {//Сперва добавляем без козырей
                if (!((Card) entry.getValue()).getSuit().equals(suit)) {
                    tempHashMap.put((Player) entry.getKey(), (Card) entry.getValue());
                }
            }

            for (Map.Entry entry : cardsPack.entrySet()) {//Потом добавляем с козырями
                if (((Card) entry.getValue()).getSuit().equals(suit)) {
                    tempHashMap.put((Player) entry.getKey(), (Card) entry.getValue());
                }
            }
            return tempHashMap;
        }
        return null;
    }

    public void addPointPlayer(Player player, int numberPointContract)
    {
        player.addPoint(numberPointContract);
    }

    public HashMap<Player, Card> getWinnerRound() {
        return winnerRound;
    }

    public GameMode getMode() {
        return mode;
    }

    public void setMode(GameMode mode) {
        this.mode = mode;
    }

    public HashMap<Player, Card> getCardsPack() {
        return cardsPack;
    }

    public void setCardsPack(HashMap<Player, Card> cardsPack) {
        this.cardsPack = cardsPack;
    }

    public SuitCard getSuit() {
        return suit;
    }
}
