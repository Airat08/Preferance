public class Player {
    private Spectator player;
    private CardsPack cards;

    public Player(Spectator spectator, CardsPack cards) {
        this.player = spectator;
        this.cards = cards;
    }

    public Spectator getPlayer() {
        return player;
    }

    public CardsPack getCards() {
        return cards;
    }
}
