package Preference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Play {
    private List<Distribution> distributions;
    private int countDistributions;
    private List<Spectator> spectators;
    private GroupPlayers groupPlayers ;
    private Rule rule;

    public Play(int countDistributions, List<Spectator> spectators, Rule rule) {
        distributions = new ArrayList<>();
        this.countDistributions = countDistributions;
        this.spectators = spectators;
        this.rule = rule;
    }

    public List<Distribution> getDistributions() {
        return distributions;
    }

    public void start()
    {

        for (int i = 0; i < countDistributions; i++) {
            groupPlayers = new GroupPlayers(spectators);
            distributions.add(new Distribution(groupPlayers,rule));
        }
        //wad();
    }

    public GroupPlayers result(GroupPlayers groupPlayers)
    {
//Инструкцию(правила) по подсчету брал из https://minigames.mail.ru/info/article/raschet_puli_v_preferanse

        calculationWhistPlayers(groupPlayers,getSmallMount(groupPlayers));
        clearPoint(groupPlayers);
        calculationMount(groupPlayers);
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
            player.addPoint(player.getBullet());
            player.subtractionBullet(player.getBullet());
            //player.subtractionMount(player.getBullet());
            //player.subtractionBullet(player.getBullet());
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
                if (count<=endDistribution) {
                    for (Player playerr : distribution.getGroupPlayers().getPlayers()) {
                        if (player.equals(playerr)) {
                            //player.addPoint(playerr.getPoint());
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
    public void API1(int number)
    {
        System.out.println("----------API1-------------");
        for (Map.Entry entry: distributions.get(number).getCourses().get(0).getPlayers().entrySet()) {
            System.out.println("\nКарты для игрока - " + ((Player)entry.getKey()).getPlayer().getFirstName()+ " " +
            ((Player)entry.getKey()).getPlayer().getSecondName());
            for (Card card: (List<Card>)entry.getValue()) {
                System.out.println(card);
            }
        }
        System.out.println("\nВ прикупе лежит:");
        for (Card card: distributions.get(number).getGroupPlayers().getPrikup()) {
            System.out.println(card);
        }
        for (Map.Entry entry: distributions.get(number).getWinnerTrade().entrySet()) {
            System.out.println("\nНачинает торговлю - "+ ((Player)entry.getKey()).getPlayer().getFirstName()+ " " +
                    ((Player)entry.getKey()).getPlayer().getSecondName());
        }
    }

    public void API2(int number)
    {
        System.out.println("----------API2------------");
        System.out.println("\nВ прикупе лежит:");
        for (Card card: distributions.get(number).getGroupPlayers().getPrikup()) {
            System.out.println(card);
        }
        System.out.println("\nИгрок - "+ distributions.get(number).getWinnerTrade().entrySet().iterator().next().getKey()+
        "Cделал СБРОС:");
        for (Card card: distributions.get(number).getDumping()) {
            System.out.println(card);
        }
        System.out.println();
        for (Player player: distributions.get(number).getGroupPlayers().getPlayers()) {
            System.out.println(player);
        }
    }

    public void API3(int number)
    {
        Player winPlayer = null;
        System.out.println("-----------API3------------");
        for (Map.Entry entry:distributions.get(number).getWinnerTrade().entrySet()) {
            winPlayer = (Player) entry.getKey();
            System.out.println("Игрок: "+entry.getKey());
            System.out.println("ЗАКАЗАЛ " + entry.getValue());
        }
        System.out.println("\nРеакция других:");
        for (Player player: distributions.get(number).getGroupPlayers().getPlayers()) {
            if (!player.equals(winPlayer)) System.out.println(player);
        }
    }

    public void API4(int number)
    {
        System.out.println("-----------API4----------------");
        for (Course course: distributions.get(number).getCourses()) {
            System.out.println(course);
        }
    }

    public void API5(int number)
    {
        System.out.println("------------API5----------------");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Итого игроки получили:").append("\n");
        System.out.println(distributions.get(number).getGroupPlayers().getPlayers().size());
        for (Player player: distributions.get(number).getGroupPlayers().getPlayers()) {
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
        System.out.println(stringBuilder.toString());
    }

    public void API6(int number)
    {
        System.out.println("-------------API6----------------");
        System.out.println(distributions.get(number));
    }

    public void API7(int number, Spectator spectator)
    {
        Player playerr = new Player(spectator,null);
        System.out.println("-------------API7------------------");
        GroupPlayers groupPlayers = calculation(number);
        for (Player player: groupPlayers.getPlayers()) {
            if (player.equals(playerr)) {
                System.out.println(player.getPlayer().getFirstName() + " " + player.getPlayer().getSecondName());
                System.out.println("У него в пуле = " + player.getBullet());
                System.out.println("У него в горе = " + player.getMount());
                for (Map.Entry entry : player.getWhist().entrySet()) {
                    System.out.println("Висты на ирока " + ((Player) entry.getKey()).getPlayer().getFirstName() + " " +
                            ((Player) entry.getKey()).getPlayer().getSecondName()
                            + " Очков = " + entry.getValue());
                }
                System.out.println("\n");
            }
        }

    }

    public void API8(int number, Spectator spectator)
    {
        Player playerr = new Player(spectator,null);
        System.out.println("-------------API8------------------");
        GroupPlayers groupPlayers = result(calculation(number));
        for (Player player: groupPlayers.getPlayers()) {
            if (player.equals(playerr)) {
                System.out.println(player.getPlayer().getFirstName() + " " + player.getPlayer().getSecondName());
                System.out.println("Промежуточный результат = " + player.getPoint());
                System.out.println("\n");
            }
        }
    }

    public void API10(int number)
    {
        System.out.println("-------------API10------------------");
        GroupPlayers groupPlayers = result(calculation(number));
        for (Player player: groupPlayers.getPlayers()) {
            System.out.println(player.getPlayer().getFirstName() + " " + player.getPlayer().getSecondName());
            System.out.println("Промежуточный результат = " + player.getPoint());
            System.out.println("\n");
        }
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Игра Preference: (игра ботов)\n");
        stringBuilder.append("Выбрана конвенция - "+rule).append("\n")
                .append("Количество раздач - "+countDistributions).append("\n").append("\n")
        .append("Имена игроков-ботов:").append("\n");
        for (Player player: groupPlayers.getPlayers()) {
            stringBuilder.append(player.getPlayer().getFirstName()).append(" ")
                    .append(player.getPlayer().getSecondName()).append("\n");
        }
        stringBuilder.append("\n");
        for (int i = 0; i < countDistributions; i++) {
            stringBuilder.append("Раздача №").append(" ").append(i).append("\n")
                    .append(distributions.get(i)).append("\n");
        }
        return stringBuilder.toString();
    }
}
