package Preference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Distribution {
    private GroupPlayers groupPlayers;
    private HashMap<Player, Card> winnerTrade;
    private Table table;
    private Rule rule;
    private HashMap<Player, Card> winnerCurrentRound;
    private HashMap<Player, Card> winnerPrevRound;
    private List<Card> dumping;
    private List<Course> courses;
    private SuitCard trumpSuit;
    private int indexFirstPlayerGoing;
    private int indexCurrentPlayerGoing;
    private HashMap<Player, Card> cardForSession;
    private static final Card DEFAULT_CARD = new Card(null,null, -1);
    private static final int MAX_RANK_CARD = 100; //главное чтоб было большее число

    public Distribution(GroupPlayers groupPlayers, Rule rule) {
        courses = new ArrayList<>();
        this.groupPlayers = groupPlayers;
        cardForSession = new HashMap<>();
        dumping = new ArrayList<>();
        this.rule = rule;
        start();
        // TODO: 10.03.2018  //http://review-pref.ru/spravochnik/rus_pref/ Отсюда брал информацию по конвенциям
    }

    private void start()
    {
        indexFirstPlayerGoing = groupPlayers.indexOfTheFirstPlayerGoing();
        indexCurrentPlayerGoing = indexFirstPlayerGoing;
        connectBetweenPlayers();

        courses.add(new Course(0,groupPlayers.getPlayers(),winnerCurrentRound,winnerPrevRound));
        Player winnerTrade = trade(groupPlayers.getPlayers().get(indexFirstPlayerGoing));
        Card minCardWinner = minCard(winnerTrade);


        if (!getGameMod().equals(GameMode.СВОЙ_ТОРГ) && !getGameMod().equals(GameMode.ПЕРЕРАЗДАЧА)) {
            addCardForSession(winnerTrade, minCardWinner);
            for (Player player : groupPlayers.getPlayers()) {
                if (!this.winnerTrade.entrySet().iterator().next().getKey().equals(player)) {
                    addCardForSession(player, minCard(player, minCardWinner.getSuit()));
                }
            }
            table = new Table(getGameMod(), trumpSuit);
            winnerPrevRound = table.solveWinnerSession(cardForSession);
            winnerCurrentRound = winnerPrevRound;
            courses.add(new Course(courses.size(), groupPlayers.getPlayers(), winnerCurrentRound, winnerPrevRound));

            for (int i = 1; i < groupPlayers.getCountCardForPlayer(); i++) {
                minCardWinner = minCard(winnerCurrentRound.entrySet().iterator().next().getKey());
                addCardForSession(winnerCurrentRound.entrySet().iterator().next().getKey(), minCardWinner);
                for (Player player : groupPlayers.getPlayers()) {
                    if (!winnerCurrentRound.entrySet().iterator().next().getKey().equals(player))
                        addCardForSession(player, minCard(player, minCardWinner.getSuit()));
                }
                winnerPrevRound.clear();
                winnerPrevRound = winnerCurrentRound;
                winnerCurrentRound.clear();
                winnerCurrentRound = table.solveWinnerSession(cardForSession);
                courses.add(new Course(i+1, groupPlayers.getPlayers(), winnerCurrentRound, winnerPrevRound));
            }
        }
        else if (GameMode.СВОЙ_ТОРГ.equals(getGameMod()))
        //todo я не успел сделать про такой сход событий
        {//при таком раскладе я не понял, что кому начисляют, если (Пасс, Здесь, Пасс)
            //что делать с игроком который с торговал и взял прикуп? а остальные пасс
            //Table table = new Table(GameMode.СВОЙ_ТОРГ,this.winnerTrade.entrySet().iterator().next().getValue().getSuit());
            //table.addPointPlayer(this.winnerTrade.entrySet().iterator().next().getKey(),getNumberContractForWinner());
            //calculation(this.winnerTrade.entrySet().iterator().next().getKey(),rule,getGameMod());
        }
        for (Player player: groupPlayers.getPlayers()) {
            calculation(player,rule,getGameMod());//подсчитываем пульку
        }

        /*else
        {
            start();
        }*/
    }

    private void connectBetweenPlayers()
    {
        HashMap<Player,Integer> hashMap = new HashMap<>();
        for (Player player: groupPlayers.getPlayers()) {
            hashMap.put(groupPlayers.prevGoingPlayer(player),0);
            hashMap.put(groupPlayers.nextGoingPlayer(player),0);;
            player.setWhist(hashMap);
            hashMap = new HashMap<>();
        }
    }

    private void calculation(Player player, Rule rule, GameMode gameMode)
    {
        if (gameMode.equals(GameMode.ВИСТ)) {
            switch (player.getStatus()) {
                case ЗДЕСЬ: {
                    if (player.getPoint() >= getNumberContractForWinner())
                        player.addBullet(countBullet(rule, getNumberContractForWinner()));
                    else
                        player.addMount(countMount(rule, getNumberContractForWinner(),
                                getNumberContractForWinner() - player.getPoint()));
                }
                case ВИСТ: {
                    if (player.getPoint() < getNumberContractForWhist(rule, getNumberContractForWinner()))
                        player.addMount(countWhistInMount(rule, getNumberContractForWinner(),
                                getNumberContractForWhist(rule, getNumberContractForWinner()) - player.getPoint()));
                    player.addWhist(winnerTrade.entrySet().iterator().next().getKey(), player.getPoint());
                }
            }
        }

        if (gameMode.equals(GameMode.РАСПАСОВКА)) {
            player.addMount(player.getPoint() * 2);//Я беру первый распас только, для примера
        }

        if (gameMode.equals(GameMode.СВОЙ_ТОРГ)) {
            switch (player.getStatus()) {
                case ЗДЕСЬ: {//при таком раскладе я не понял, что кому начисляют, если (Пасс, Здесь, Пасс)
                    //что делать с игроком который с торговал и взял прикуп? а остальные пасс
                    //player.addBullet(countBullet(rule, getNumberContractForWinner()));
                }
            }
        }
    }

    private int countBullet(Rule rule, int contract)
    {
        switch (rule)
        {
            case Ленинрад:
            {
                switch (contract)
                {
                    case 6: return 2;
                    case 7: return 4;
                    case 8: return 6;
                    case 9: return 8;
                    case 10: return 10;
                }
            }
            case Сочи:
            {
                switch (contract)
                {
                    case 6: return 2;
                    case 7: return 4;
                    case 8: return 6;
                    case 9: return 8;
                    case 10: return 10;
                }
            }
            case Ростов:
            {
                switch (contract)
                {
                    case 6: return 2;
                    case 7: return 4;
                    case 8: return 6;
                    case 9: return 8;
                    case 10: return 10;
                }
            }
        }
        return 0;
    }

    private int countMount(Rule rule, int contract, int NOT_Enough)
    {
        switch (rule)
        {
            case Ленинрад: {
                switch (contract) {
                    case 6:
                        return 4 * NOT_Enough;
                    case 7:
                        return 8 * NOT_Enough;
                    case 8:
                        return 12 * NOT_Enough;
                    case 9:
                        return 16 * NOT_Enough;
                    case 10:
                        return 20 * NOT_Enough;
                }
            }
            case Сочи:
            {
                switch (contract)
                {
                    case 6:
                        return 2 * NOT_Enough;
                    case 7:
                        return 4 * NOT_Enough;
                    case 8:
                        return 6 * NOT_Enough;
                    case 9:
                        return 8 * NOT_Enough;
                    case 10:
                        return 10 * NOT_Enough;
                }
            }
            case Ростов:
            {
                switch (contract)
                {
                    case 6:
                        return 2 * NOT_Enough;
                    case 7:
                        return 4 * NOT_Enough;
                    case 8:
                        return 6 * NOT_Enough;
                    case 9:
                        return 8 * NOT_Enough;
                    case 10:
                        return 10 * NOT_Enough;
                }
            }
        }
        return 0;
    }

    private int countWhistInMount(Rule rule, int contract, int NOT_Enough)
    {
        switch (rule)
        {
            case Ленинрад:
            {
                switch (contract)
                {
                    case 6: return 2*NOT_Enough;
                    case 7: return 4*NOT_Enough;
                    case 8: return 6*NOT_Enough;
                    case 9: return 8*NOT_Enough;
                    case 10: return 10*NOT_Enough;
                }
            }
            case Сочи:
            {
                switch (contract)
                {
                    case 6: return 2*NOT_Enough;
                    case 7: return 4*NOT_Enough;
                    case 8: return 6*NOT_Enough;
                    case 9: return 8*NOT_Enough;
                    case 10: return 10*NOT_Enough;
                }
            }
            case Ростов:
            {
                switch (contract)
                {
                    case 6: return 1*NOT_Enough;
                    case 7: return 2*NOT_Enough;
                    case 8: return 3*NOT_Enough;
                    case 9: return 4*NOT_Enough;
                    case 10: return 5*NOT_Enough;
                }
            }
        }
        return 0;
    }

    private int getNumberContractForWinner()
    {
        try {
            switch (winnerTrade.entrySet().iterator().next().getValue().getName()) {
                case шесть:
                    return 6;
                case семь:
                    return 7;
                case восемь:
                    return 8;
                case девять:
                    return 9;
                case десять:
                    return 10;
            }
            return 0;
            }
            catch(Exception ex)
            {
                return 0;
            }
    }

    private int getNumberContractForWhist(Rule rule, int contract)
    {
        switch (rule)
        {
            case Ленинрад:
            {
                switch (contract)
                {
                    case 6 : return 4;
                    case 7 : return 8;
                    case 8 : return 12;
                    case 9 : return 16;
                    case 10 : return 20;
                }
            }
            case Сочи:
            {
                switch (contract)
                {
                    case 6 : return 2;
                    case 7 : return 4;
                    case 8 : return 6;
                    case 9 : return 8;
                    case 10 : return 10;
                }
            }
            case Ростов:
            {
                switch (contract)
                {
                    case 6 : return 2;
                    case 7 : return 4;
                    case 8 : return 6;
                    case 9 : return 8;
                    case 10 : return 10;
                }
            }
                break;
        }
        return 0;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public Table getTable() {
        return table;
    }

    public int getIndexFirstPlayerGoing() {
        return indexFirstPlayerGoing;
    }

    private Player trade(Player firstGoingPlayer)
    {
        for (Player player:groupPlayers.getPlayers()) {//Отсортируем каждому игроку его карты по старшинству мастей
            player.setHashMap(getStatisticCards(player));
        }
        winnerTrade = new HashMap<>();
        winnerTrade.put(firstGoingPlayer, getCountMaxCard(firstGoingPlayer));
        for (int i = 0; i < groupPlayers.getPlayers().size() - 1; i++) {
            compare(groupPlayers.getPlayers().get(groupPlayers.indexNextGoingPlayer(indexCurrentPlayerGoing)));
            indexCurrentPlayerGoing = groupPlayers.indexNextGoingPlayer(indexCurrentPlayerGoing);
        }
        for (Card card:groupPlayers.getPrikup()) { //Выигравшему торг, отдаем карты с прикупа (по правилам)
            winnerTrade.entrySet().iterator().next().getKey().getCards().add(card);//Добавляем с прикупа
            dumping.add(minCard(winnerTrade.entrySet().iterator().next().getKey()));
            winnerTrade.entrySet().iterator().next().getKey().getCards().
                    remove(dumping.get(dumping.size()-1));//И забираем у него маленькую карту);
        }
        try {
           trumpSuit = winnerTrade.entrySet().iterator().next().getValue().getSuit();
        }
        catch (NullPointerException ex)
        {
            trumpSuit = SuitCard.БК;
        }
        return winnerTrade.entrySet().iterator().next().getKey();
    }

    private boolean compare(Player player)//Спрашивает бота, что ты будешь делать :)
            //todo переделать этот метод по другому
    {//todo мизер не успел продумать
        Card cardPlayer1, cardPlayer2;

        if (winnerTrade.entrySet().iterator().next().getValue() == null)
        {
            cardPlayer1 = DEFAULT_CARD;//чтобы не поймали nullPointer
            winnerTrade.entrySet().iterator().next().getKey().setStatus(Status.ПАСС);
        }
        else
        {
            cardPlayer1 = winnerTrade.entrySet().iterator().next().getValue();
            winnerTrade.entrySet().iterator().next().getKey().setStatus(Status.ЗДЕСЬ);
        }
        if (getCountMaxCard(player) == null)
        {
            //cardPlayer2 = DEFAULT_CARD;
            player.setStatus(Status.ПАСС);
            return false;
        }
        else
        {
            cardPlayer2 = getCountMaxCard(player);
            player.setStatus(Status.ЗДЕСЬ);
        }

        if (cardPlayer1.getRank()<cardPlayer2.getRank()-3)//почему минус 3, чтобы по реже были пасы (для логики)
        {
            winnerTrade.entrySet().iterator().next().getKey().setStatus(Status.ПАСС);
            winnerTrade.clear();
            winnerTrade.put(player,cardPlayer2);
            player.setStatus(Status.ЗДЕСЬ);
            return true;
        }
        else if (cardPlayer1.getRank()<=cardPlayer2.getRank()+3)//Чтоб висты по чаще были
        {
            winnerTrade.entrySet().iterator().next().getKey().setStatus(Status.ЗДЕСЬ);
            player.setStatus(Status.ВИСТ);
            return false;
        }
        else
        {
            winnerTrade.entrySet().iterator().next().getKey().setStatus(Status.ЗДЕСЬ);
            player.setStatus(Status.ПАСС);
            return false;
        }
    }

    public GameMode getGameMod()
    {
        List<Status> statuses = new ArrayList<>();
        for (Player player:groupPlayers.getPlayers()) {
            statuses.add(player.getStatus());
        }
        if (statuses.contains(Status.НЕ_ЗАДЕЙСТВОВАН)) return GameMode.ПЕРЕРАЗДАЧА;
        if (statuses.contains(Status.МИЗЕР)) return GameMode.МИЗЕР;//todo про мизер не успел продумать логику боту
        if (statuses.contains(Status.ЗДЕСЬ) && statuses.contains(Status.ВИСТ)) return GameMode.ВИСТ;
        if (statuses.contains(Status.ЗДЕСЬ) && !statuses.contains(Status.ВИСТ)) return GameMode.СВОЙ_ТОРГ;
        return GameMode.РАСПАСОВКА;
    }

    private void addCardForSession(Player player, Card card)
    {
        if (cardForSession.size()>=3) cardForSession = new HashMap<>();
        cardForSession.put(player,card);
        player.getCards().getCardsPack().remove(card);
    }

    public HashMap<Player, Card> getCardForSession() {
        return cardForSession;
    }

    private Card minCard(Player player, SuitCard suit)//Минимальная карта с текущей мастью, если нет то минимальная козырная
    {//Все по правилам. Ищется карта с заданной масте, если нет то минимальную козырную, если нет то любую другую
        //Для логики ботов берем минимальную
        int temp = MAX_RANK_CARD;
        Card minCard = null;
        for (Card card:player.getCards().getCardsPack()) {
            if (card.getRank()<temp && card.getSuit().equals(suit))
            {
                temp = card.getRank();
                minCard = card;
            }
        }
        if (minCard==null && trumpSuit!=null)
        {
            for (Card card:player.getCards().getCardsPack()) { //С минимальной козырной
                if (card.getRank()<temp && card.getSuit().equals(trumpSuit))
                {
                    temp = card.getRank();
                    minCard = card;
                }
            }
        }
        if (minCard==null)//Любая другая карта
        {
            for (Card card:player.getCards().getCardsPack()) {
                if (card.getRank()<temp)
                {
                    temp = card.getRank();
                    minCard = card;
                }
            }
        }
        return minCard;
    }

    private Card minCard(Player player)//Для первого игрока, который ходит
    {
        int temp = MAX_RANK_CARD;
        Card minCard = null;
        for (Card card:player.getCards().getCardsPack()) {
            if (card.getRank()<temp && !card.getSuit().equals(trumpSuit))
            {
                temp = card.getRank();
                minCard = card;
            }
        }

        if (minCard == null)//Если остались все козырные
        {
            for (Card card:player.getCards().getCardsPack()) {
                if (card.getRank()<temp)
                {
                    temp = card.getRank();
                    minCard = card;
                }
            }
        }
        return minCard;
    }

    private Card getCountMaxCard(Player player)
    {
        int temp = -1;
        SuitCard suit = null;
        for (Map.Entry entry: player.getHashMap().entrySet()) {
            if (temp < (int)entry.getKey())
            {
                temp = (int)entry.getKey();
                suit = (SuitCard) entry.getValue();
            }
        }
        return getCardTrade(temp,suit);
    }

    private Card getCardTrade(int maxEqualSuit, SuitCard suit) //критерии для отбора карты в торговле (логика ботов!!)
    {
        switch (suit)
        {
            case пики:
            {
                switch (maxEqualSuit)
                {
                    case 4 : return Trade.sixSpades;
                    case 5 : return Trade.sevenSpades;
                    case 6 : return Trade.sevenSpades;
                    case 7 : return Trade.eightSpades;
                    case 8 : return Trade.eightSpades;
                    case 9 : return Trade.nineSpades;
                    case 10 : return Trade.tenSpades;
                }
            }
            case треф:
            {
                switch (maxEqualSuit)
                {
                    case 4 : return Trade.sixClubs;
                    case 5 : return Trade.sevenClubs;
                    case 6 : return Trade.sevenClubs;
                    case 7 : return Trade.eightClubs;
                    case 8 : return Trade.eightClubs;
                    case 9 : return Trade.nineClubs;
                    case 10 : return Trade.tenClubs;
                }
            }
            case бубн:
            {
                switch (maxEqualSuit)
                {
                    case 4 : return Trade.sixDiamonds;
                    case 5 : return Trade.sevenDiamonds;
                    case 6 : return Trade.sevenDiamonds;
                    case 7 : return Trade.eightDiamonds;
                    case 8 : return Trade.eightDiamonds;
                    case 9 : return Trade.nineDiamonds;
                    case 10 : return Trade.tenDiamonds;
                }
            }
            case черви:
            {
                switch (maxEqualSuit)
                {
                    case 4 : return Trade.sixHeart;
                    case 5 : return Trade.sevenHeart;
                    case 6 : return Trade.sevenHeart;
                    case 7 : return Trade.eightHeart;
                    case 8 : return Trade.eightHeart;
                    case 9 : return Trade.nineHeart;
                    case 10 : return Trade.tenHeart;
                }
            }
            case БК:
            {
                switch (maxEqualSuit)
                {
                    case 4 : return Trade.sixBC;
                    case 5 : return Trade.sevenBC;
                    case 6 : return Trade.sevenBC;
                    case 7 : return Trade.eightBC;
                    case 8 : return Trade.eightBC;
                    case 9 : return Trade.nineBC;
                    case 10 : return Trade.tenBC;
                }
            }
        }
        return null;
    }

    public List<Card> getDumping() {
        return dumping;
    }

    private HashMap<Integer, SuitCard> getStatisticCards(Player player)
    {//Без козырей надо продумать логику бота
        HashMap<Integer, SuitCard> hashMap = new HashMap<>();
        for (SuitCard suit: SuitCard.values()) {
            hashMap.put(countSuit(player,suit),suit);//Без козыря надо отдельно считать
        }
        return hashMap;
    }

    private int countSuit(Player player, SuitCard suit)//Количество одинаковых мастей
    {
        int count = 0;
        for (Card card:player.getCards().getCardsPack()) {
            if (card.getSuit().equals(suit)) count++;
        }
        return count;
    }

    public GroupPlayers getGroupPlayers() {
        return groupPlayers;
    }

    public HashMap<Player, Card> getWinnerTrade() {
        return winnerTrade;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Торговлю начал - ").
                append(groupPlayers.getPlayers().get(groupPlayers.indexOfTheFirstPlayerGoing())).append("\n")
                .append("Торговлю выиграл - ").append(winnerTrade.entrySet().iterator().next().getKey())
                .append("С контрактом - ").append(winnerTrade.entrySet().iterator().next().getValue()).append("\n")
                .append("В прикупе лежит - ").append(groupPlayers.getPrikup()).append("\n")
                .append("Выигрывший торговлю сделал сброс - ").append(dumping).append("\n")
                .append("\n")
                .append("\n");
        for (Course course: courses) {
            stringBuilder.append(course).append("\n");
        }
        stringBuilder.append("Итого игроки получили:").append("\n");
        for (Player player: groupPlayers.getPlayers()) {
            stringBuilder.append("Игрок - ").append(player.getPlayer().getFirstName()).append(" ")
            .append(player.getPlayer().getSecondName()).append("\n")
                    .append("ВЗЯЛ ВЗЯТОК - ").append(player.getPoint()).append("\n")
            .append("В ПУЛЮ НАЧИСЛЕНО - ").append(player.getBullet()).append("\n")
            .append("В ГОРУ НАЧИСЛЕНО - ").append(player.getMount()).append("\n");
            for (Map.Entry entry: player.getWhist().entrySet()) {
                stringBuilder.append("ВИСТЫ НА ИГРОКА - ").append(((Player)entry.getKey()).getPlayer().getFirstName())
                        .append(" ")
                        .append(((Player)entry.getKey()).getPlayer().getSecondName())
                        .append(" ")
                        .append("В РАЗМЕРЕ = ").append(entry.getValue()).append("\n");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
