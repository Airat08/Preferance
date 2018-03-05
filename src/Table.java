import java.util.HashMap;
import java.util.Map;

public class Table {
    private GameMode mode;
    private HashMap<Player,Card> cardsPack;
    private SuitCard suit;

    public Table(GameMode mode, HashMap<Player,Card> cardsPack, SuitCard suit) {
        this.mode = mode;
        this.cardsPack = cardsPack;
        this.suit = suit;
    }

    public HashMap<Player,Card> getWinnerSession()
    {
        HashMap<Player,Card> tempSortCardsPack = sort();
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
            HashMap<Player,Card> winner = new HashMap<>();
            winner.put(player,card);
            return winner;
        }
        return tempSortCardsPack;
    }

    //Метод для того чтобы вернуть остортированные полученные карты по порядку их значимости
    private HashMap<Player,Card> sort()//т.е. чтоб было (не козырная, не козырная, козырная), а не так (не козырная, козырная, не козырная)
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
