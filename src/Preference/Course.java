package Preference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Course {
    private int ID;//номер раунда
    private HashMap<Player,List<Card>> players;//информация по игрокам за текущий раунд
    private HashMap<Player, Card> winnerCurrentRound;//победитель в этом раунде и следующий раздающий
    private Player winnerPrevRound;//раздающий
    private HashMap<Player, Card> cardsForSession;//карты с которыми игроки сходили

    public Course(int ID, List<Player> players, HashMap<Player, Card> winnerCurrentRound, Player winnerPrevRound,
                  HashMap<Player, Card> cardsForSession) {
        this.ID = ID;
        this.players = new HashMap<>();
        addCardsPlayer(players);
        this.cardsForSession = cardsForSession;
        if (winnerCurrentRound!=null)
        addWinnerCurrentRound(winnerCurrentRound);
        this.winnerPrevRound = winnerPrevRound;
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
        this.winnerCurrentRound.clear();
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

    public int getID() {
        return ID;
    }

    public HashMap<Player, List<Card>> getPlayers() {
        return players;
    }

    public HashMap<Player, Card> getWinnerCurrentRound() {
        return winnerCurrentRound;
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (ID == 0) stringBuilder = new StringBuilder("Номер раунда в раздаче = ").append(ID)
                .append(" Изначальный вариант карт (БЕЗ СБРОСА)").append("\n");
        else stringBuilder.append("Номер раунда в раздаче = ").append(ID).append("\n");
        if (winnerPrevRound!= null && winnerCurrentRound!=null) {
            stringBuilder.append("Ходит игрок - ").append(winnerPrevRound).append("\n");
            stringBuilder.append("Победитель в раунде (получивший взятку) - ").append(winnerCurrentRound.entrySet().iterator().next().getKey())
                    .append("С картой ").append(winnerCurrentRound.entrySet().iterator().next().getValue())
                    .append("\n")
            .append("\n")
            .append("Игроки сходили с такими картами: (Расположены не в порядке хода)").append("\n");
            for (Map.Entry entry: cardsForSession.entrySet()) {
                stringBuilder.append(((Player)entry.getKey()).getPlayer().getFirstName()).append(" ")
                        .append(((Player)entry.getKey()).getPlayer().getSecondName()).append(" сходил с ")
                        .append(entry.getValue()).append("\n");
            }
        }
        stringBuilder.append("\nОстались такие карты:\n");
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
