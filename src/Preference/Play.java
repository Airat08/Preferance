package Preference;

import java.io.*;
import java.util.*;

public class Play {
    private List<Distribution> distributions;
    private int countDistributions;
    private List<Spectator> spectators;
    private GroupPlayers groupPlayers ;
    private Rule rule;
    private static final String NAME_DIRECTION_REQUEST = "Запросы";
    private static final String NAME_DIRECTION_API = "API";
    private static final String LOG_PLAY = "Лог игры";
    private static final String DEFAULT_NAME_DIRECTION = "БезНазвания";

    public Play(int countDistributions, List<Spectator> spectators, Rule rule) {
        distributions = new ArrayList<>();
        this.countDistributions = countDistributions;
        this.spectators = spectators;
        this.rule = rule;
    }

    public List<Distribution> getDistributions() {
        return distributions;
    }

    public void start() throws IOException {
        for (int i = 0; i < countDistributions; i++) {
            groupPlayers = new GroupPlayers(spectators);
            distributions.add(new Distribution(groupPlayers,rule));
        }
        System.out.println(this.toString());//логирование в консоль
        write(this.toString(),-1,LOG_PLAY);//дополнительное логирование в текстовый файл
    }

    public void outRequestes() {
        try {
            Scanner in = new Scanner(System.in);
            System.out.println("Выберите запрос:");
            System.out.println("0: Не хочу запрос");
            System.out.println("1: Сохранить в текстовый файл результаты раздачи карт (Выбор раздачи будет дальше...)");
            System.out.println("2: Сохранить в текстовый файл процесс трговли (Выбор раздачи будет дальше...)");
            System.out.println("3: Сохранить в текстовый файл полный процесс розыгрыша (Выбор раздачи будет дальше...)");
            System.out.println("4: Сохранить в тектовый файл результаты всех розыгрышей");
            System.out.println("5: Сохранить в текстовый файл финальные результаты игры");
            System.out.println("6: Логирование всех действий в текстовый файл (Логирование всех действий в консоле уже есть)");
            request(in.nextInt());
        }
        catch (Exception ex)
        {
            System.out.println("Неправильно указали команду запроса, введите (0, 1, 2, ...,) в соответсвии с форматом");
            outRequestes();
        }
    }

    private void request(int numberRequest) {
        Scanner in = new Scanner(System.in);
        StringBuilder stringBuilder;
        int numberDistribution;
        try {

            switch (numberRequest) {
                case 0:
                    break;
                case 1: {
                    System.out.println("Выберите раздачу:");
                    numberDistribution = in.nextInt();
                    stringBuilder = new StringBuilder("\nРаздача №").append(numberDistribution).append("\n")
                            .append(distributions.get(numberDistribution).getCourses().get(0).toString());
                    write(stringBuilder.toString(), 1,NAME_DIRECTION_REQUEST);
                    System.out.println("Сохранено в самом проекте (*\\Preferance)");
                    outRequestes();
                    break;

                }
                case 2:
                {
                    System.out.println("Выберите раздачу:");
                    numberDistribution = in.nextInt();
                    stringBuilder = new StringBuilder("\nРаздача №").append(numberDistribution).append("\n")
                            .append(distributions.get(numberDistribution).processTrade());
                    write(stringBuilder.toString(),2,NAME_DIRECTION_REQUEST);
                    System.out.println("Сохранено в самом проекте (*\\Preferance)");
                    outRequestes();
                    break;
                }
                case 3:
                {
                    System.out.println("Выберите раздачу:");
                    numberDistribution = in.nextInt();
                    stringBuilder = new StringBuilder("\nРаздача №").append(numberDistribution).append("\n")
                            .append(distributions.get(numberDistribution).toString());
                    write(stringBuilder.toString(),3,NAME_DIRECTION_REQUEST);
                    System.out.println("Сохранено в самом проекте (*\\Preferance)");
                    outRequestes();
                    break;
                }
                case 4:
                {
                    write(result(countDistributions),4,NAME_DIRECTION_REQUEST);
                    System.out.println("Сохранено в самом проекте (*\\Preferance)");
                    outRequestes();
                    break;
                }
                case 5:
                {
                    write(finalResult(),5,NAME_DIRECTION_REQUEST);
                    System.out.println("Сохранено в самом проекте (*\\Preferance)");
                    outRequestes();
                    break;
                }
                case 6:
                {
                    write(toString(),6,NAME_DIRECTION_REQUEST);
                    System.out.println("Сохранено в самом проекте (*\\Preferance)");
                    outRequestes();
                    break;
                }
            }
        }
        catch (Exception ex)
        {
            System.out.println("Попробуйте заново!" +
                    " Возможно вы ввели не правильно команду или номер раздачи превышает число раздач.");
            request(numberRequest);
        }
    }

    private void write(String text, int numberRequest, String nameDirection) throws IOException {

        PrintWriter out;
        File file = new File(nameDirection);
        if (!file.exists()) file.mkdir();
        switch (nameDirection)
        {
            case NAME_DIRECTION_REQUEST:
            {
                out = new PrintWriter(new FileWriter(nameDirection+"\\Запрос №"+numberRequest+".txt",true));
                writeText(text,out);
                break;
            }

            case NAME_DIRECTION_API:
            {
                out = new PrintWriter(new FileWriter(nameDirection+"\\API №"+numberRequest+".txt",true));
                writeText(text,out);
                break;
            }

            case LOG_PLAY:
            {
                out = new PrintWriter(new FileOutputStream(nameDirection+"\\log.txt"));
                writeText(text,out);
                break;
            }
            default:
            out = new PrintWriter(new FileOutputStream(DEFAULT_NAME_DIRECTION+"\\БезИмени"+numberRequest+".txt"));
            writeText(text,out);
            break;
        }
    }

    private void writeText(String text,PrintWriter out)
    {
        String[] temp = text.split("\n");
        for (String s: temp) {
            out.println(s);
        }
        out.close();
    }

    private GroupPlayers result(GroupPlayers groupPlayers)
    {
// TODO Инструкцию(правила) по подсчету брал из https://minigames.mail.ru/info/article/raschet_puli_v_preferanse
        clearPoint(groupPlayers);
        calculationMount(groupPlayers);
        calculationWhistPlayers(groupPlayers,getSmallMount(groupPlayers));
        calculationPoint(groupPlayers);
        return groupPlayers;
    }

    private void clearPoint(GroupPlayers groupPlayers)
    {
        for (Player player: groupPlayers.getPlayers()) {
            player.clearPoint();
        }
    }

    private void calculationPoint(GroupPlayers groupPlayers)
    {
        for (Player player: groupPlayers.getPlayers()) {
            for (Map.Entry entry: player.getWhist().entrySet()) {
                player.addPoint((int)entry.getValue());
                player.subtractionWhist((Player) entry.getKey(),(int)entry.getValue());
            }
        }
    }

    private void calculationMount(GroupPlayers groupPlayers)
    {
        for (Player player: groupPlayers.getPlayers()) {//Вычитаем из горы его пули которые он набрал
            player.subtractionMount(player.getBullet());
            player.subtractionBullet(player.getBullet());
        }
    }

    private void calculationWhistPlayers(GroupPlayers groupPlayers, int small)
    {
        for (Player player: groupPlayers.getPlayers()) {
            player.subtractionMount(small);
        }
        for (Player player: groupPlayers.getPlayers()) {
            for (Player playerr: groupPlayers.getPlayers()) {
                player.addWhist(playerr,(playerr.getMount()/ GroupPlayers.getCountPlayerInGroup()) *10);
            }
        }
        for (Player player : groupPlayers.getPlayers()) {
            player.subtractionMount(player.getMount());
        }

        int number = 0;
        for (Player player: groupPlayers.getPlayers()) {
            for (Player playerr: groupPlayers.getPlayers()) {
                number = player.getWhist(playerr);
                player.subtractionWhist(playerr,playerr.getWhist(player));
                playerr.subtractionWhist(player,number);
            }
        }
    }


    private int getSmallMount(GroupPlayers groupPlayers)
    {
        int number = 10000;
        for (Player player: groupPlayers.getPlayers()) {
            if (player.getMount()<number) number = player.getMount();
        }
        return number;
    }

    private GroupPlayers calculation(int endDistribution)
    {
        int count = 0 ;
        GroupPlayers groupPlayers = new GroupPlayers(spectators, GroupPlayers.getCountPlayerInGroup());
        connectBetweenPlayers(groupPlayers);
        for (Player player: groupPlayers.getPlayers()) {
            for (Distribution distribution : distributions) {
                if (count<endDistribution) {
                    for (Player playerr : distribution.getGroupPlayers().getPlayers()) {
                        if (player.equals(playerr)) {
                            player.addBullet(playerr.getBullet());
                            player.addMount(playerr.getMount());
                            for (Map.Entry entry : playerr.getWhist().entrySet()) {
                                player.addWhist((Player) entry.getKey(), (int) entry.getValue());
                            }
                        }
                    }
                }
                count++;
            }
            count = 0;
        }
        return groupPlayers;
    }

    private void connectBetweenPlayers(GroupPlayers groupPlayers)
    {
        HashMap<Player,Integer> hashMap = new HashMap<>();
        for (Player player: groupPlayers.getPlayers()) {
            hashMap.put(groupPlayers.prevGoingPlayer(player),0);
            hashMap.put(groupPlayers.nextGoingPlayer(player),0);
            player.setWhist(hashMap);
            hashMap = new HashMap<>();
        }
    }

    //Метод получения данных определенной раздачи (кому какие карты были
    //розданы, что в прикупе, кто начинает торговлю/чей ход). Номер раздачи
    //должен быть вынесен в параметры метода. Результаты работы метода
    public String API1(int number) throws IOException {
        StringBuilder stringBuilder = new StringBuilder("----------API1 ДЛЯ ")
                .append(number).append(" раздачи--------------");
        for (Map.Entry entry: distributions.get(number).getCourses().get(0).getPlayers().entrySet()) {
            stringBuilder.append("\nКарты для игрока - ")
                    .append(((Player)entry.getKey()).getPlayer().getFirstName()).append(" ")
                    .append(((Player)entry.getKey()).getPlayer().getSecondName()).append("\n");
            for (Card card: (List<Card>)entry.getValue()) {
                stringBuilder.append(card).append("\n");
            }
        }
        stringBuilder.append("\nВ прикупе лежит:\n");
        for (Card card: distributions.get(number).getGroupPlayers().getPrikup()) {
            stringBuilder.append(card).append("\n");
        }
        for (Map.Entry entry: distributions.get(number).getWinnerTrade().entrySet()) {
            stringBuilder.append("\nНачинает торговлю - ")
                    .append(((Player)entry.getKey()).getPlayer().getFirstName()).append(" ")
                    .append(((Player)entry.getKey()).getPlayer().getSecondName()).append("\n");
        }
        write(stringBuilder.toString(),1,NAME_DIRECTION_API);//Запись в файл
        return stringBuilder.toString();
    }

    //Метод получения данных о процессе торговли для определенной раздачи
    //(включая данные о полученном прикупе). Номер раздачи должен быть
    //вынесен в параметры метода. Результаты работы метода должны быть
    //выданы на экран/записаны в файл
    public String API2(int number) throws IOException {
        StringBuilder stringBuilder = new StringBuilder("----------API2 ДЛЯ ").append(number)
                .append(" раздачи-----------------").append("\n")
                .append("\nВ прикупе лежит:").append("\n");
        for (Card card: distributions.get(number).getGroupPlayers().getPrikup()) {
            stringBuilder.append(card).append("\n");
        }
        stringBuilder.append("\nИгрок - ")
                .append(distributions.get(number).getWinnerTrade().entrySet().iterator().next().getKey())
                .append("Cделал СБРОС:").append("\n");
        for (Card card: distributions.get(number).getDumping()) {
            stringBuilder.append(card).append("\n");
        }
        stringBuilder.append("\n");
        for (Player player: distributions.get(number).getGroupPlayers().getPlayers()) {
            stringBuilder.append(player).append("\n");
        }
        write(stringBuilder.toString(),2,NAME_DIRECTION_API);
        return stringBuilder.toString();
    }

    //Метод получения данных о процессе заявки игрока (какую игру заказал для
    //определенной раздачи) и реакции других игроков (вист/паст, игра в
    //открытую/закрытую). Номер раздачи должен быть вынесен в параметры
    //метода. Результаты работы метода должны быть выданы на экран/записаны
    //в файл
    public String API3(int number) throws IOException {
        Player winPlayer = null;
        StringBuilder stringBuilder = new StringBuilder("-----------API3 ДЛЯ ")
                .append(number).append(" раздачи---------------").append("\n");
        for (Map.Entry entry:distributions.get(number).getWinnerTrade().entrySet()) {
            winPlayer = (Player) entry.getKey();
            if (entry.getValue()!=null)
            stringBuilder.append("Игрок: ").append(entry.getKey()).append("\n")
                    .append("ЗАКАЗАЛ ").append(entry.getValue()).append("\n");
            else stringBuilder.append("Игрок: ").append(entry.getKey()).append("\n")
                    .append("ЗАКАЗАЛ(так как остальные пасс) ").append(distributions.get(number).getGameMod()).append("\n");
        }
        stringBuilder.append("\nРеакция других:");
        for (Player player: distributions.get(number).getGroupPlayers().getPlayers()) {
            if (!player.equals(winPlayer)) stringBuilder.append(player).append("\n");
        }
        write(stringBuilder.toString(),3,NAME_DIRECTION_API);
        return stringBuilder.toString();
    }


    //Метод получения данных о процессе розыгрыша (последовательность ходов
    //и принадлежность взяток) определенной раздачи. Номер раздачи должен
    //быть вынесен в параметры метода. Результаты работы метода должны быть
    //выданы на экран/записаны в файл
    public String API4(int number) throws IOException {
        StringBuilder stringBuilder = new StringBuilder("-----------API4 ДЛЯ ")
                .append(number).append(" раздачи--------------").append("\n");
        for (Course course: distributions.get(number).getCourses()) {
            stringBuilder.append(course).append("\n");
        }
        write(stringBuilder.toString(),4,NAME_DIRECTION_API);
        return stringBuilder.toString();
    }

    private String result(int number)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Итого игроки получили:").append("\n");
        for (int i = 0; i < number; i++) {
            stringBuilder.append("Раздача №").append(i).append("\n");
            for (Player player : distributions.get(i).getGroupPlayers().getPlayers()) {
                stringBuilder.append("Игрок - ").append(player.getPlayer().getFirstName()).append(" ")
                        .append(player.getPlayer().getSecondName()).append("\n")
                        .append("ВЗЯЛ ВЗЯТОК - ").append(player.getPoint()).append("\n")
                        .append("В ПУЛЮ НАЧИСЛЕНО - ").append(player.getBullet()).append("\n")
                        .append("В ГОРУ НАЧИСЛЕНО - ").append(player.getMount()).append("\n");
                for (Map.Entry entry : player.getWhist().entrySet()) {
                    stringBuilder.append("ВИСТЫ НА ИГРОКА - ").append(((Player) entry.getKey()).getPlayer().getFirstName())
                            .append(" ")
                            .append(((Player) entry.getKey()).getPlayer().getSecondName())
                            .append(" ")
                            .append("В РАЗМЕРЕ = ").append(entry.getValue()).append("\n");
                }
                stringBuilder.append("\n").append("\n");
            }
        }
        return stringBuilder.toString();
    }

    //Метод получения данных о результатах розыгрыша определенной раздачи
    //(кто сколько взяток взял, какие цифры записаны в пулю, гору и висты).
    //Номер раздачи должен быть вынесен в параметры метода. Результаты
    //работы метода должны быть выданы на экран/записаны в файл
    public String API5(int number) throws IOException {
        StringBuilder stringBuilder = new StringBuilder("------------API5 ДЛЯ ")
                .append(number).append(" раздачи---------------").append("\n");
        stringBuilder.append(result(number));
        write(stringBuilder.toString(),5,NAME_DIRECTION_API);
        return stringBuilder.toString();
    }

    //Метод получения данных о полном процессе розыгрыша определенной
    //раздачи (раздача, торговля, заявка, игра, результаты). Номер раздачи
    //должен быть вынесен в параметры метода. Результаты работы метода
    //должны быть выданы на экран/записаны в файл
    public String API6(int number) throws IOException {
        StringBuilder stringBuilder = new StringBuilder("-------------API6 ДЛЯ ")
                .append(number).append(" раздачи-----------------").append("\n");
        stringBuilder.append(distributions.get(number));
        write(stringBuilder.toString(),6,NAME_DIRECTION_REQUEST);
        return stringBuilder.toString();
    }

    //Метод получения данных о текущем состоянии пули, горы и вистах игрока
    //после определенной раздачи. Номер раздачи и идентификатор игрока
    //должны быть вынесены в параметры метода. Результаты работы метода
    //должны быть выданы на экран/записаны в файл
    public String API7(int number, Spectator spectator) throws IOException {
        Player playerr = new Player(spectator,null);
        StringBuilder stringBuilder = new StringBuilder("-------------API7 ДЛЯ ")
                .append(number).append(" раздачи----------------").append("\n");
        GroupPlayers groupPlayers = calculation(number);
        for (Player player: groupPlayers.getPlayers()) {
            if (player.equals(playerr)) {
                stringBuilder.append(player.getPlayer().getFirstName()).append(" ")
                        .append(player.getPlayer().getSecondName()).append("\n")
                        .append("У него в пуле = ").append(player.getBullet()).append("\n")
                        .append("У него в горе = ").append(player.getMount()).append("\n");
                for (Map.Entry entry : player.getWhist().entrySet()) {
                    stringBuilder.append("Висты на ирока ")
                            .append(((Player) entry.getKey()).getPlayer().getFirstName()).append(" ")
                            .append(((Player) entry.getKey()).getPlayer().getSecondName())
                            .append(" Очков = ").append(entry.getValue()).append("\n");
                }
                stringBuilder.append("\n");
            }
        }
        write(stringBuilder.toString(),7,NAME_DIRECTION_API);
        return stringBuilder.toString();
    }

    //Метод получения промежуточного результата игрока после определенной
    //раздачи. Результат работы метода – определенное число, показывающее
    //как успешно играет игрок (определяется исходя из состояния пули, горы и
    //вистов всех игроков, как если бы игра была прекращена после указанной
    //раздачи и надо было бы подсчитывать результаты). Номер раздачи и
    //идентификатор игрока должны быть вынесены в параметры метода.
    //Результаты работы метода должны быть выданы на экран/записаны в файл
    public String API8(int number, Spectator spectator) throws IOException {
        Player playerr = new Player(spectator,null);
        StringBuilder stringBuilder = new StringBuilder("-------------API8 ДЛЯ ")
                .append(number).append(" раздачи-----------------").append("\n");
        GroupPlayers groupPlayers = result(calculation(number));
        for (Player player: groupPlayers.getPlayers()) {
            if (player.equals(playerr)) {
                stringBuilder.append(player.getPlayer().getFirstName()).append(" ")
                        .append(player.getPlayer().getSecondName()).append("\n")
                        .append("Промежуточный результат = ").append(player.getPoint()).append("\n")
                        .append("\n");
            }
        }
        write(stringBuilder.toString(),8,NAME_DIRECTION_API);
        return stringBuilder.toString();
    }

    private int successfulPlayForPlayer(Spectator spectator, int numberDistribution, GameMode gameMode)
    {
        int countSuccessful = 0;
        for (int i = 0; i < numberDistribution; i++) {
            for (Player player: distributions.get(i).getGroupPlayers().getPlayers()) {
                if (player.equals(new Player(spectator,null)))
                {
                    switch (distributions.get(i).getGameMod())
                    {
                        case ВИСТ:
                        {
                            if (gameMode.equals(GameMode.ВИСТ) && player.getStatus().equals(Status.ЗДЕСЬ)
                                    && player.getBullet()>0)
                                countSuccessful++;
                            break;
                        }
                        case СВОЙ_ТОРГ:
                        {
                            if (gameMode.equals(GameMode.СВОЙ_ТОРГ) && player.getStatus().equals(Status.ЗДЕСЬ)
                                    && player.getBullet()>0)
                                countSuccessful++;
                            break;
                        }
                        case РАСПАСОВКА:
                        {
                            if (gameMode.equals(GameMode.РАСПАСОВКА) && player.getPoint()==0) countSuccessful++;
                            break;
                        }
                        case МИЗЕР:
                        {
                            if (gameMode.equals(GameMode.МИЗЕР) && player.getStatus().equals(Status.МИЗЕР)
                                    && player.getPoint()==0)
                                countSuccessful++;
                            break;
                        }
                    }
                }
            }
        }
        return countSuccessful;
    }

    //Метод получения статистики по игроку после розыгрыша определенной
    //раздачи – сколько и каких игр было сыграно, сколько успешных распасов,
    //мизеров. Номер раздачи и идентификатор игрока должны быть вынесены в
    //параметры метода. Результаты работы метода должны быть выданы на
    //экран/записаны в файл
    public String API9(int number, Spectator spectator) throws IOException {
        int successful = 0;
        for (GameMode gameMode: GameMode.values()) {
            successful += successfulPlayForPlayer(spectator,number,gameMode);
        }
        StringBuilder stringBuilder = new StringBuilder("-------------API9 ДЛЯ ")
                .append(number).append(" раздач----------------\n");
        stringBuilder.append("Я считаю, что сыгранная игра это игра в котором ты получил выгоду\n")
        .append("В итоге для: ").append(spectator).append("\n")
        .append("Cыгранных(успешных) игр было - ").append(successful).append("\n")
        .append("Сыгранных в режиме ВИСТ (Это когда у кого то из игроков реакция ВИСТ) = ")
                .append(successfulPlayForPlayer(spectator,number,GameMode.ВИСТ)).append("\n")
        .append("Сыгранных в режиме СВОЙ_ТОРГ (Это когда другие игроки спасовали, а один не спасовал) = ")
                .append(successfulPlayForPlayer(spectator,number,GameMode.СВОЙ_ТОРГ)).append("\n")
        .append("Сыгранных в режиме РАССПАСОВКА (Это когда все спасовали) = ")
                .append(successfulPlayForPlayer(spectator,number,GameMode.РАСПАСОВКА)).append("\n")
        .append("Сыгранных в режиме МИЗЕР (Это когда игрок предложил мизер в начале торговли) = ")
                .append(successfulPlayForPlayer(spectator,number,GameMode.МИЗЕР)).append("\n");
        write(stringBuilder.toString(),9,NAME_DIRECTION_API);
        return stringBuilder.toString();
    }

    private String finalResult()
    {
        StringBuilder stringBuilder = new StringBuilder("Финальный результат игроков:").append("\n");
        GroupPlayers groupPlayers = result(calculation(countDistributions));
        for (Player player: groupPlayers.getPlayers()) {
            stringBuilder.append(player.getPlayer().getFirstName()).append(" ")
                    .append(player.getPlayer().getSecondName()).append("\n")
            .append("Промежуточный результат = ").append(player.getPoint())
                    .append("\n").append("\n");
        }
        return stringBuilder.toString();
    }

    //Метод получения промежуточного результата всех игроков после
    //определенной раздачи. Результат работы метода – определенное число,
    //показывающее, как успешно играют игроки (определяется исходя из
    //состояния пули, горы и вистов всех игроков, как если бы игра была
    //прекращена после указанной раздачи и надо было бы подсчитывать
    public String API10(int number) throws IOException {
        StringBuilder stringBuilder = new StringBuilder("-------------API10 ДЛЯ ")
                .append(number).append(" раздачи---------------").append("\n");
        GroupPlayers groupPlayers = result(calculation(number));
        for (Player player: groupPlayers.getPlayers()) {
            stringBuilder.append(player.getPlayer().getFirstName()).append(" ")
                    .append(player.getPlayer().getSecondName()).append("\n")
                    .append("Промежуточный результат = ").append(player.getPoint()).append("\n")
                    .append("\n");
        }
        write(stringBuilder.toString(),10,NAME_DIRECTION_API);
        return stringBuilder.toString();
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Игра Preference: (автоматическая игра ботов)\n");
        stringBuilder.append("Выбрана конвенция - "+rule).append("\n")
                .append("Количество раздач - "+countDistributions).append("\n")
                .append("Последовательность хода игроков прописаны в программе")
                .append("\n").append("\n")
        .append("Имена игроков-ботов (Это не последовательность ход игроков):").append("\n");
        for (Player player: groupPlayers.getPlayers()) {
            stringBuilder.append(player.getPlayer().getFirstName()).append(" ")
                    .append(player.getPlayer().getSecondName()).append("\n");
        }
        stringBuilder.append("\n");
        for (int i = 0; i < countDistributions; i++) {
            stringBuilder.append("Раздача №").append(" ").append(i).append("\n")
                    .append(distributions.get(i)).append("\n");
        }
        stringBuilder.append("\n").append(finalResult());
        return stringBuilder.toString();
    }
}
