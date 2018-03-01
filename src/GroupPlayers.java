import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GroupPlayers {
    private List<Spectator> spectators;
    private List<Player> groupPlayers;
    private List<Card> cardPacks;
    private final static int COUNT_CARD_FOR_PLAYER = 10;
    private final static int COUNT_PLAYER_IN_GROUP = 3;

    public GroupPlayers(List<Spectator> spectators, List<Card> cardPacks) {
        this.spectators = spectators;
        this.cardPacks = cardPacks;
        groupPlayers = new ArrayList<>();
        createGroup();
    }

    private void createGroup()
    {
        int tempNumber;
        Random random = new Random();
        for (int i = 0; i < COUNT_PLAYER_IN_GROUP; i++) {
            tempNumber = random.nextInt(spectators.size());
            groupPlayers.add(new Player(spectators.get(tempNumber),cardsForPlayer()));
            spectators.remove(tempNumber);
        }
    }

    private CardsPack cardsForPlayer()
    {
        int tempNumber;
        CardsPack cardPacks = new CardsPack(COUNT_CARD_FOR_PLAYER);
        Random random = new Random();
        for (int i = 0; i < COUNT_CARD_FOR_PLAYER; i++) {
            tempNumber = random.nextInt(this.cardPacks.size());
            cardPacks.add(this.cardPacks.get(tempNumber));
            this.cardPacks.remove(tempNumber);
        }

        return cardPacks;
    }

    public List<Player> getGroupPlayers() {
        return groupPlayers;
    }

    public List<Card> getCardPacks() {//Для двух последних карт
        return cardPacks;
    }
}
