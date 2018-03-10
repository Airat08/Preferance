package Preference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class GroupPlayers {
    private List<Spectator> spectators;
    private List<Player> groupPlayers;//псоледний будет прикуп
    private List<Card> cardPacks;
    private CardsPack pack;
    private List<Card> prikup;
    private int[] indexPositionPlayers;
    private final static int COUNT_CARD_FOR_PLAYER = 10;
    private final static int COUNT_DISTRIBUTION_CARD = 2;
    private final static int COUNT_PLAYER_IN_GROUP = 3;
    private final static int CONT_CARD_PRIKUP = 2;

    public GroupPlayers(List<Spectator> spectators) {
        pack = new CardsPack();
        this.spectators = spectators;
        this.cardPacks = pack.getCardsPack();
        groupPlayers = new ArrayList<>();
        prikup = new ArrayList<>();
        createGroup();//Создаем группу игроков
        createPrikup();//И создаем прикупа
    }

    public GroupPlayers(List<Spectator> spectators, int count) {

        createGroup(spectators, count);
    }

    private void createGroup()
    {
        groupPlayers = new ArrayList<>();
        mixCardsPack(); //тасуем карты
        List<CardsPack> cardsForPlayers = cardsForPlayer();
        for (int i = 0; i < COUNT_PLAYER_IN_GROUP; i++) {
            groupPlayers.add(new Player(spectators.get(i),cardsForPlayers.get(i)));
        }
        for (Player player : groupPlayers) {
            if (player.getCards().getCardsPack().size()<9)
            {
                createGroup();
                createPrikup();
            }
        }
    }

    private void createGroup(List<Spectator> spectators, int count)//Нужно для расчета пула, в дальнейшем
    {
        groupPlayers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            groupPlayers.add(new Player(spectators.get(i),null));
        }
    }

    private List<CardsPack> cardsForPlayer() //раздача карт всем игрокам по правилам игры
    {
        List<Card> cardsPackPlayers = getCardsPackForPlayers();
        List<CardsPack> cardsPacksAllPlayers = new ArrayList<>();
        CardsPack cardPacks = new CardsPack(COUNT_CARD_FOR_PLAYER);
        for (int i = 0; i < (COUNT_DISTRIBUTION_CARD * COUNT_PLAYER_IN_GROUP) -1; i+=COUNT_DISTRIBUTION_CARD ) {//определяет какая первая карта для раздачи
            for (int j = i; j < cardsPackPlayers.size(); j+=COUNT_PLAYER_IN_GROUP * COUNT_DISTRIBUTION_CARD) {//перебираем все карты
                for (int k = 0; k < COUNT_DISTRIBUTION_CARD; k++) {//раздаем последовательно по 2 карты
                    cardPacks.add(cardsPackPlayers.get(j+k));
                }
            }
            cardsPacksAllPlayers.add(cardPacks);
            cardPacks = new CardsPack(COUNT_CARD_FOR_PLAYER);
        }
        return cardsPacksAllPlayers;
    }

    private List<Card> getCardsPackForPlayers()//карты для игроков, т.к 2 последние должны отдаваться прикупу
    {
        List<Card> list = new ArrayList<>();
        for (int i = 0; i < CardsPack.getCountPackCards() - CONT_CARD_PRIKUP; i++) {
            list.add(this.cardPacks.get(i));
        }
        return list;
    }

    private void mixCardsPack()//мешаем колоду
    {
        int index, number;
        Card temp;
        Random random = new Random();
        List<Integer> tempCardsPack = new ArrayList<>();
        for (int i = 0; i < cardPacks.size(); i++) {
            tempCardsPack.add(i);
        }
        for (int i = 0; i < tempCardsPack.size(); i++) {
            number = random.nextInt(tempCardsPack.size());
            index = tempCardsPack.get(number);
            if (index==0) index++;
            temp = cardPacks.get(i);
            cardPacks.set(i,cardPacks.get(index));
            cardPacks.set(index,temp);
            tempCardsPack.remove(number);
        }
    }

    //Я сделал вывод, при прочтении правил, что первый игрок который начинает торговлю находится слева от прикупа
    //И поэтому я решил, что прикупу надо дать рандомное место, а потом определять кто первый ходит
    public int indexOfTheFirstPlayerGoing()//Вызывать его лишь в классе Distribution
    {
        int[] temp = new int[COUNT_PLAYER_IN_GROUP + 1];
        for (int i = 0; i < COUNT_PLAYER_IN_GROUP + 1; i++) {
            temp[i] = i;
        }
        temp = mixedPlayerAndPrikup(temp);
        indexPositionPlayers = temp;
        for (int i = 0; i < temp.length; i++) {
            if ((temp[i] == COUNT_PLAYER_IN_GROUP) && i!=0) return (i-1);
            if (temp[i] == COUNT_PLAYER_IN_GROUP && i==0) return (temp[temp.length-1]);
        }
        return 0;
    }

    public int indexNextGoingPlayer(int indexPrevPlayer) //Возвращает индекс следующего игрока, который будет ходить
    {
        if (indexPrevPlayer==(indexPositionPlayers.length-2)) return 0;
           else return indexPrevPlayer+1;
    }

    public Player nextGoingPlayer(Player player)
    {
        for (int i = 0; i < groupPlayers.size(); i++) {
            if (player.equals(groupPlayers.get(i)))
            {
                if (i==groupPlayers.size()-1) return groupPlayers.get(0);
                else return groupPlayers.get(i+1);
            }
        }
        return null;
    }

    public Player prevGoingPlayer(Player player)
    {
        for (int i = 0; i < groupPlayers.size(); i++) {
            if (player.equals(groupPlayers.get(i)))
            {
                if (i==0) return groupPlayers.get(groupPlayers.size()-1);
                else return groupPlayers.get(i-1);
            }
        }
        return null;
    }

    public int[] mixedPlayerAndPrikup(int[] indexesPlayerAndPrikup)
    {
        Random random = new Random();
        int index = random.nextInt(indexesPlayerAndPrikup.length);
        int temp = indexesPlayerAndPrikup[index];
        indexesPlayerAndPrikup[indexesPlayerAndPrikup.length - 1] = temp;
        indexesPlayerAndPrikup[temp] = indexesPlayerAndPrikup.length - 1;
        return indexesPlayerAndPrikup;
    }

    public void setHashMapPlayer(HashMap<Integer, SuitCard> hashMap, Player player)
    {
        player.setHashMap(hashMap);
    }

    public List<Player> getPlayers() {
        return groupPlayers;
    }

    public void createPrikup() {//Для двух последних карт, прикупу
        prikup.add(cardPacks.get(cardPacks.size()- 2));
        prikup.add(cardPacks.get(cardPacks.size() -1));
    }

    public List<Card> getPrikup() {
        return prikup;
    }

    public List<Card> getCardPacks() {
        return cardPacks;
    }

    public static int getCountCardForPlayer() {
        return COUNT_CARD_FOR_PLAYER;
    }

    public static int getCountDistributionCard() {
        return COUNT_DISTRIBUTION_CARD;
    }

    public static int getCountPlayerInGroup() {
        return COUNT_PLAYER_IN_GROUP;
    }

    public static int getContCardPrikup() {
        return CONT_CARD_PRIKUP;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("").append("\n");
        for (Player player: groupPlayers) {
            stringBuilder.append(player).append("\n");
        }
        return stringBuilder.toString();
    }
}
