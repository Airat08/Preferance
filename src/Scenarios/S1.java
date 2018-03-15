package Scenarios;

import Preference.Rule;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class S1 {
    public static void main(String[] args) throws IOException {
        List<Preference.Spectator> spectators = new ArrayList<>();

        //Добавляем игроков в спектра
        spectators.add(new Preference.Spectator("Айрат", "Мухутдинов"));
        spectators.add(new Preference.Spectator("Вася", "Пупкин"));
        spectators.add(new Preference.Spectator("Андрей", "Кержаков"));

        //добавляем игру с кол-вой раздачи 12, кидаем спектров, и выбираем конвенцию, (В ПАРАМЕТРИЗАЦИИ))
        Preference.Play play = new Preference.Play(12, spectators, Rule.Ростов);
        play.start();//там же и логирование


        System.out.println("\n\n\nВыполняю заданный сценарий по заданию");


        System.out.println(play.API1(2));
        System.out.println(play.API1(5));
        System.out.println(play.API2(4));
        System.out.println(play.API3(3));
        System.out.println(play.API4(7));
        System.out.println(play.API5(5));
        System.out.println(play.API7(8, spectators.get(0)));//Я не стал делать личный идентификатор, а так если бы реализовывал,
        //делал бы через сессии для уникальности идентификатора (в общем, если бы была база данных к примеру)
        System.out.println(play.API8(10, spectators.get(1)));
        System.out.println(play.API9(10,spectators.get(2)));
        System.out.println(play.API10(12));//В заданиях не указано конкректно для какой раздачи

        play.outRequestes();//дополнительное логирование в текстовый файл
    }

}
class S2{//будет различаться со сценарием s1 так как другой main
    public static void main(String[] args) throws IOException {
        List<Preference.Spectator> spectators = new ArrayList<>();
        spectators.add(new Preference.Spectator("Айрат", "Мухутдинов"));
        spectators.add(new Preference.Spectator("Вася", "Пупкин"));
        spectators.add(new Preference.Spectator("Андрей", "Кержаков"));
        Preference.Play play = new Preference.Play(12,spectators, Preference.Rule.Сочи);
        play.start();
        System.out.println("\n\n\nВыполняю заданный сценарий по заданию");

        for (int i = 0; i < play.getDistributions().size(); i++) {
            System.out.println(play.API6(i));
        }

        play.outRequestes();
    }
}