package Scenarios;

import java.util.ArrayList;
import java.util.List;


public class S1 {
    public static void main(String[] args) {
        List<Preference.Spectator> spectators = new ArrayList<>();
        spectators.add(new Preference.Spectator("Айрат", "Мухутдинов"));
        spectators.add(new Preference.Spectator("Вася", "Пупкин"));
        spectators.add(new Preference.Spectator("Андрей", "Кержаков"));
        Preference.Play play = new Preference.Play(12, spectators, Preference.Rule.Сочи);
        play.start();
        System.out.println(play);//Логирую все действия в консоль
        System.out.println("\n\n\nВыполняю заданный сценарий по заданию");

        play.API1(2);
        play.API1(5);

        play.API2(4);

        play.API3(3);

        play.API4(7);

        play.API5(6);

        play.API7(8, spectators.get(0));

        play.API8(10, spectators.get(1));

        play.API10(12);//В заданиях не указано конкректно для какой раздачи
    }
}
class S2{
    public static void main(String[] args) {
        List<Preference.Spectator> spectators = new ArrayList<>();
        spectators.add(new Preference.Spectator("Айрат", "Мухутдинов"));
        spectators.add(new Preference.Spectator("Вася", "Пупкин"));
        spectators.add(new Preference.Spectator("Андрей", "Кержаков"));
        Preference.Play play = new Preference.Play(12,spectators, Preference.Rule.Сочи);
        play.start();
        System.out.println(play);//Логирую все действия в консоль
        System.out.println("\n\n\nВыполняю заданный сценарий по заданию");

        for (int i = 0; i < play.getDistributions().size(); i++) {
            play.API6(i);
        }
    }
}