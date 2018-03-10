package Preference;

public class Card {
    private NameCard name;
    private SuitCard suit;
    private int rank;

    public Card(NameCard name, SuitCard suit, int rank) {
        this.name = name;
        this.suit = suit;
        this.rank = rank;
    }

    public NameCard getName() {
        return name;
    }

    public SuitCard getSuit() {
        return suit;
    }

    public int getRank() {
        return rank;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (name!=null)
            stringBuilder.append(name.name())
                .append(" ").append(suit.name())
                .append(" ранк (").append(rank).append(")");
        return stringBuilder.toString();
    }
}
