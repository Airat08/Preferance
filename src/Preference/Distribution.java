package Preference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Distribution {
    private GroupPlayers groupPlayers;//сами игроки
    private HashMap<Player, Card> winnerTrade;//победитель торговли
    private Table table;//в столе определяется кто выигрыл раунд, тоесть идет сравнение карт
    private Rule rule;//конвенция, нужна для начислении очков в гору в висты и в пулю
    private List<Card> dumping;//сброшенные карты от игрока выигрывшего торговлю
    private List<Course> courses;//сюда записываем каждый раунд в раздаче
    private SuitCard trumpSuit;//устанавливается козырная после торговли
    private int indexFirstPlayerGoing;
    private int indexCurrentPlayerGoing;
    private HashMap<Player, Card> cardForSession;//карты с которыми ходят игроки (Хождение игроков определяется в метода, кто за кем)
    private static final int MAX_RANK_CARD = 100; //главное чтоб было большее число
    private static final int DEFAULT_COUNT_MIZER = 5;//Если уменьшить то мизер будет частый
    private static final int MAX_RANK_FOR_BK = 12;//Если у игрока будут большие карты, начиная с дамы,
    // то он может взять контракт с БК(без козырей)

    public Distribution(GroupPlayers groupPlayers, Rule rule) {
        courses = new ArrayList<>();
        this.groupPlayers = groupPlayers;
        cardForSession = new HashMap<>();
        dumping = new ArrayList<>();
        this.rule = rule;
        start();
        // TODO: 10.03.2018  //http://review-pref.ru/spravochnik/rus_pref/ Отсюда брал информацию по конвенциям
    }

    private void start() {
        try {
            indexFirstPlayerGoing = groupPlayers.indexOfTheFirstPlayerGoing();
            indexCurrentPlayerGoing = indexFirstPlayerGoing;
            connectBetweenPlayers();//устанавливаем для каждого игрока, кто на кого будет кидать висты (Слева и справо)
            HashMap<Player, Card> winnerCurrentRound = new HashMap<>();
            courses.add(new Course(0, groupPlayers.getPlayers(), winnerCurrentRound, null,cardForSession));
            Player winnerTrade = trade(groupPlayers.getPlayers().get(indexFirstPlayerGoing));//происходит торговля игроков
            Card minCardWinner = minCard(winnerTrade);//определяется карта с который будет ходить выигравший торговлю игрок


            if (!getGameMod().equals(GameMode.СВОЙ_ТОРГ) && !getGameMod().equals(GameMode.ПЕРЕРАЗДАЧА)) {
                addCardForSession(winnerTrade, minCardWinner);//добавляем карту для хода
                for (Player player : groupPlayers.getPlayers()) {
                    if (!this.winnerTrade.entrySet().iterator().next().getKey().equals(player)) {
                        addCardForSession(player, minCard(player, minCardWinner.getSuit()));
                    }
                }
                table = new Table(getGameMod(), trumpSuit);//инициализация стола

                winnerCurrentRound = table.solveWinnerSession(cardForSession);//решает кто победител в этом раунде
                Player playerPrevWinner = winnerTrade;
                courses.add(new Course(courses.size(), groupPlayers.getPlayers(), winnerCurrentRound, playerPrevWinner,
                        cardForSession));

                for (int i = 1; i < groupPlayers.getCountCardForPlayer(); i++) {
                    minCardWinner = minCard(winnerCurrentRound.entrySet().iterator().next().getKey());//победитель раунда выбирает карту для хода
                    addCardForSession(winnerCurrentRound.entrySet().iterator().next().getKey(), minCardWinner);//фиксируется эта карта
                    for (Player player : groupPlayers.getPlayers()) {//для остальных игроков
                        if (!winnerCurrentRound.entrySet().iterator().next().getKey().equals(player))
                            addCardForSession(player, minCard(player, minCardWinner.getSuit()));//выбирается карта с указанной мастью от победителя
                        //предыдущего раунда
                    }

                    playerPrevWinner = winnerCurrentRound.entrySet().iterator().next().getKey();
                    winnerCurrentRound.clear();
                    winnerCurrentRound = table.solveWinnerSession(cardForSession);
                    //записываем все в отдельном объекте, и храним текущий раунд
                    courses.add(new Course(i + 1, groupPlayers.getPlayers(), winnerCurrentRound, playerPrevWinner,
                            cardForSession));
                }
            } else if (GameMode.СВОЙ_ТОРГ.equals(getGameMod())) {//Происходит из за таких РЕАКЦИЯ игроков, как (ЗДЕСЬ, ПАСС, ПАСС)
                Table table = new Table(GameMode.СВОЙ_ТОРГ, this.winnerTrade.entrySet().iterator().next().getValue().getSuit());
                table.addPointPlayer(this.winnerTrade.entrySet().iterator().next().getKey(), getNumberContractForWinner());
            }
            for (Player player : groupPlayers.getPlayers()) {
                calculation(player, rule, getGameMod());//подсчитываем пульку
            }
        }
        catch (Exception ex)
        {
            start();
        }
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
                    player.addBullet(countBullet(rule, getNumberContractForWinner()));
                }
            }
        }
        if (gameMode.equals(GameMode.МИЗЕР))
        {
            switch (player.getStatus())
            {
                case МИЗЕР:
                {//я не заморачивался насчет 10, так как во всех конвенциях по 10
                    //очков в пулю начисляется, не стал делать отдельный метод для этого
                    if (player.getPoint()>= 10) player.addPoint(10);
                    //11 контракт это мизер
                    else player.addMount(countMount(rule,11,10-player.getPoint()));
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
                    case 11: return 10;//для мизера
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
                    case 11: return 10;//для мизера
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
                    case 11: return 10;//для мизера
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
                    case 11://для мизера
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
                    case 11://Для мизера
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
                    case 11://Для мизера
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

        winnerTrade.put(firstGoingPlayer, null);
        if (checkedOnMizer()) winnerTrade.entrySet().iterator().next().getKey().setStatus(Status.МИЗЕР);
        else winnerTrade.entrySet().iterator().next().setValue(getCountMaxCard(firstGoingPlayer));


        for (int i = 0; i < groupPlayers.getPlayers().size() - 1; i++) {//Идем последовательно (ПО ПРАВИЛАМ)
            compare(groupPlayers.nextGoingPlayer(groupPlayers.getPlayers().get(indexCurrentPlayerGoing)));
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
    {
        //если первый игрок не объявил мизер
        if (!winnerTrade.entrySet().iterator().next().getKey().getStatus().equals(Status.МИЗЕР)) {
            return solvePlayersStatusNOMizer(player);
        }

        else // и тут если объявил
        {
            return solvePlayersStatusMizer(player);
        }
    }

    //алгоритм таков, если у игрока количество карт достигает указанного т.е. у него должны быть такие карты как 7, 8, 9
    //если кол-во этих карт вместе взятых достигает числа 5, то игрок заявляет МИЗЕР
    private boolean checkedOnMizer()//проверка первого игрока, не хочет ли он мизер(ПО ПРАВИЛАМ)
    {
        int count = 0;
        List<Card> cardsPlayer = winnerTrade.entrySet().iterator().next().getKey().getCards().getCardsPack();
        for (Card card: cardsPlayer) {
            if (card.getName().equals(NameCard.семь) ||
                    card.getName().equals(NameCard.восемь) ||
                    card.getName().equals(NameCard.девять))
                count++;
        }
        return (count>=DEFAULT_COUNT_MIZER);
    }

    private boolean solvePlayersStatusMizer(Player player)//Логика ботов при МИЗЕРЕ
    {//Исход такого события очень редок
        try {
            if (getCountMaxCard(player).getRank() >= Trade.nineSpades.getRank())//Сбить мизер можно лишь со ставкой 9 или 10 (ПО ПРАВИЛАМ)
            {
                if (getCountMaxCard(winnerTrade.entrySet().iterator().next().getKey()).getRank() <
                        getCountMaxCard((player)).getRank()) {
                    winnerTrade.entrySet().iterator().next().getKey().setStatus(Status.ПАСС);
                    winnerTrade.clear();
                    player.setStatus(Status.ЗДЕСЬ);
                    winnerTrade.put(player, getCountMaxCard(player));
                    return true;
                } else if (getCountMaxCard(winnerTrade.entrySet().iterator().next().getKey()).getRank() ==
                        getCountMaxCard((player)).getRank()) {
                    winnerTrade.entrySet().iterator().next().getKey().setStatus(Status.ВИСТ);
                    winnerTrade.clear();
                    player.setStatus(Status.ЗДЕСЬ);
                    winnerTrade.put(player, getCountMaxCard(player));
                    return true;
                } else {
                    winnerTrade.entrySet().iterator().next().getKey().setStatus(Status.ЗДЕСЬ);
                    player.setStatus(Status.ПАСС);
                    return false;
                }
            } else {
                player.setStatus(Status.ПАСС);
            }
        }
        catch (Exception ex)//Возникает, если у игрока нет даже 3х одинаковых мастей (МОЯ ЛОГИКА ДЛЯ БОТОВ)
        {
            player.setStatus(Status.ПАСС);
        }
        return false;
    }

    private boolean solvePlayersStatusNOMizer(Player player)
    {
        Card cardPlayer1 = winnerTrade.entrySet().iterator().next().getValue(),
                cardPlayer2 = null;
        try {
            if (cardPlayer1 == null) {
                winnerTrade.entrySet().iterator().next().getKey().setStatus(Status.ПАСС);
            } else {
                cardPlayer1 = winnerTrade.entrySet().iterator().next().getValue();
                winnerTrade.entrySet().iterator().next().getKey().setStatus(Status.ЗДЕСЬ);
            }
            if (getCountMaxCard(player) == null) {
                player.setStatus(Status.ПАСС);
                return false;
            } else {
                cardPlayer2 = getCountMaxCard(player);
                player.setStatus(Status.ЗДЕСЬ);
            }

            if (cardPlayer1.getRank() < cardPlayer2.getRank() - 3)//почему минус 3, чтобы по реже были пасы (для логики)
            {
                winnerTrade.entrySet().iterator().next().getKey().setStatus(Status.ПАСС);
                winnerTrade.clear();
                player.setStatus(Status.ЗДЕСЬ);
                winnerTrade.put(player, cardPlayer2);
                return true;
            } else if (cardPlayer1.getRank() <= cardPlayer2.getRank() + 3)//Чтоб висты по чаще были
            {
                winnerTrade.entrySet().iterator().next().getKey().setStatus(Status.ЗДЕСЬ);
                player.setStatus(Status.ВИСТ);
                return false;
            } else {
                winnerTrade.entrySet().iterator().next().getKey().setStatus(Status.ЗДЕСЬ);
                player.setStatus(Status.ПАСС);
                return false;
            }
        }
        catch (Exception ex)
        {
           if (cardPlayer2!=null)
           {
               winnerTrade.clear();
               player.setStatus(Status.ЗДЕСЬ);
               winnerTrade.put(player,cardPlayer2);
               return true;
           }
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
        if (statuses.contains(Status.МИЗЕР)) return GameMode.МИЗЕР;
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
    {
        HashMap<Integer, SuitCard> hashMap = new HashMap<>();
        for (SuitCard suit: SuitCard.values()) {
            hashMap.put(countSuit(player,suit),suit);
        }
        return hashMap;
    }

    private int countSuit(Player player, SuitCard suit)//Количество одинаковых мастей
    {
        int count = 0;
        if (suit.equals(SuitCard.БК))
        {
            for (Card card:player.getCards().getCardsPack()) {
                if (card.getRank()>MAX_RANK_FOR_BK) count++;
            }
            return count;
        }
        else
        for (Card card:player.getCards().getCardsPack()) {
            if (card.getSuit().equals(suit)) count++;
        }
        return count;
    }

    public void setWinnerTrade(HashMap<Player, Card> winnerTrade) {
        this.winnerTrade = winnerTrade;
    }

    public GroupPlayers getGroupPlayers() {
        return groupPlayers;
    }

    public HashMap<Player, Card> getWinnerTrade() {
        return winnerTrade;
    }

    public String processTrade()
    {
        StringBuilder stringBuilder = new StringBuilder("Торговлю начал - ").
                append(groupPlayers.getPlayers().get(getIndexFirstPlayerGoing())).append("\n")
                .append("Торговлю выиграл - ").append(winnerTrade.entrySet().iterator().next().getKey());
        if (winnerTrade.entrySet().iterator().next().getValue()==null) stringBuilder
                .append("Контракт - ").append(getGameMod()).append("\n");
        else stringBuilder.append("С контрактом - ")
                .append(winnerTrade.entrySet().iterator().next().getValue()).append("\n")
                .append("В прикупе лежит - ").append(groupPlayers.getPrikup()).append("\n")
                .append("Выигрывший торговлю сделал сброс - ").append(dumping).append("\n");
        stringBuilder.append("Реакция остальных:").append("\n");
        for (Player player : groupPlayers.getPlayers()) {
            if (player!=winnerTrade.entrySet().iterator().next().getKey())
                stringBuilder.append(player).append("\n");
        }
        return stringBuilder.toString();
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Торговлю начал - ").
                append(groupPlayers.getPlayers().get(getIndexFirstPlayerGoing())).append("\n")
                .append("Торговлю выиграл - ").append(winnerTrade.entrySet().iterator().next().getKey());
                if (winnerTrade.entrySet().iterator().next().getValue()==null) stringBuilder
                .append("Контракт - ").append(getGameMod()).append("\n");
                else stringBuilder.append("С контрактом - ")
                        .append(winnerTrade.entrySet().iterator().next().getValue()).append("\n")
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
