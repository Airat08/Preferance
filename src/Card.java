public class Card {
    private NameCard name;
    private SuitCard suit;

    public Card(NameCard name, SuitCard suit) {
        this.name = name;
        this.suit = suit;
    }

    public NameCard getName() {
        return name;
    }

    public SuitCard getSuit() {
        return suit;
    }
}
