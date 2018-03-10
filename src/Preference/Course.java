package Preference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Course {
    private int ID;//номер раунда
    private HashMap<Player,List<Card>> players;//информация по игрокам за текущий раунд
    private HashMap<Player, Card> winnerCurrentRound;//победитель в этом раунде и следующий раздающий
    private HashMap<Player, Card> winnerPrevRound;//раздающий

    public Course(int ID, List<Player> players, HashMap<Player, Card> winnerCurrentRound, HashMap<Player, Card> winnerPrevRound) {
        this.ID = ID;
        this.players = new HashMap<>();
        addCardsPlayer(players);
        if (winnerCurrentRound!=null)
        addWinnerCurrentRound(winnerCurrentRound);
        if (winnerPrevRound!=null)
        addWinnerPrevRound(winnerPrevRound);
    }

    private void addCardsPlayer(List<Player> players)
    {
        List<Card> list = new ArrayList<>();
        for (Player player: players) {
            for (Card card: player.getCards().getCardsPack()) {
                list.add(card);
            }
            this.players.put(player,list);
            list = new ArrayList<>();
        }
    }

    private void addWinnerCurrentRound(HashMap<Player, Card> winnerCurrentRound)
    {
        this.winnerCurrentRound = new HashMap<>();
        Player player = null;
        Card card = null;
        int temp = winnerCurrentRound.size()-1;
        int count = 0;
        for (Map.Entry entry: winnerCurrentRound.entrySet()) {
            if (count==temp) {
                card = new Card(((Card) entry.getValue()).getName(),
                        ((Card) entry.getValue()).getSuit(),
                        ((Card) entry.getValue()).getRank());
                player = (Player) entry.getKey();
            }
            count++;
        }

        this.winnerCurrentRound.put(player,card);
    }

    private void addWinnerPrevRound(HashMap<Player, Card> winnerPrevRound)
    {
        this.winnerPrevRound = new HashMap<>();
        Player player = null;
        Card card = null;
        int temp = winnerPrevRound.size()-1;
        int count = 0;
        for (Map.Entry entry: winnerPrevRound.entrySet()) {
            if (count==temp) {
                card = new Card(((Card) entry.getValue()).getName(),
                        ((Card) entry.getValue()).getSuit(),
                        ((Card) entry.getValue()).getRank());
                player = (Player) entry.getKey();
            }
            count++;
        }
        this.winnerPrevRound.put(player,card);
    }

    public int getID() {
        return ID;
    }

    public HashMap<Player, List<Card>> getPlayers() {
        return players;
    }

    public HashMap<Player, Card> getWinnerCurrentRound() {
        return winnerCurrentRound;
    }

    public HashMap<Player, Card> getWinnerPrevRound() {
        return winnerPrevRound;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Номер раунда в раздаче = ").append(ID).append("\n");
        if (winnerPrevRound!= null && winnerCurrentRound!=null) {
            stringBuilder.append("Раздающий - ").append(winnerPrevRound.entrySet().iterator().next().getKey()).append("\n");
            stringBuilder.append("Победитель в раунде (получивший взятку) - ").append(winnerCurrentRound.entrySet().iterator().next().getKey())
                    .append("С картой ").append(winnerCurrentRound.entrySet().iterator().next().getValue())
                    .append("\n")
            .append("\n");
        }
        stringBuilder.append("Остались такие карты:\n");
        for (Map.Entry entry: players.entrySet()) {
            stringBuilder.append(entry.getKey());
            for (Card card: (List<Card>)entry.getValue()) {
                stringBuilder.append(card).append("\n");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
