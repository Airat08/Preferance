import java.util.HashMap;

public class Player {
    private Spectator player;
    private CardsPack cards;
    private Status status;
    private HashMap<Integer,SuitCard> hashMap;

    public Player(Spectator spectator, CardsPack cards) {
        this.player = spectator;
        this.cards = cards;
        hashMap = new HashMap<>();
        status = Status.НЕ_ЗАДЕЙСТВОВАН;
    }

    public Spectator getPlayer() {
        return player;
    }

    public CardsPack getCards() {
        return cards;
    }

    public void setHashMap(HashMap<Integer, SuitCard> hashMap) {
        this.hashMap = hashMap;
    }

    public HashMap<Integer, SuitCard> getHashMap() {
        return hashMap;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(player.getFirstName())
                .append(" ").append(player.getSecondName());
        return stringBuilder.toString();
    }
}
