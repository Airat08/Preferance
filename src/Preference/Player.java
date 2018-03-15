package Preference;

import java.util.HashMap;
import java.util.Map;

public class Player {
    private Spectator player;
    private CardsPack cards;
    private Status status;
    private int point;
    private int mount;//гора
    private int bullet;//пуля
    private HashMap<Player,Integer> whist;//висты на игроков (слева и справо)(связи устанавливаются в Distribution)
    private HashMap<Integer, SuitCard> hashMap;
    private static final int DEFAULT_POINT = 0;

    public Player(Spectator spectator, CardsPack cards) {
        this.player = spectator;
        this.cards = cards;
        hashMap = new HashMap<>();
        status = Status.НЕ_ЗАДЕЙСТВОВАН;
        whist = new HashMap<>();
        point = DEFAULT_POINT;
        mount = DEFAULT_POINT;
        bullet = DEFAULT_POINT;
    }

    public int getPoint() {
        return point;
    }

    public void addPoint(int number) {
        this.point+=number;
    }
    public void addMount(int number) {this.mount+=number;}
    public void addBullet(int number){this.bullet+=number; }
    public void addWhist(Player player, int number) {
        for (Map.Entry entry:whist.entrySet()) {
            if (entry.getKey().equals(player)) entry.setValue(((Integer)entry.getValue())+number);
        }
    }

    public void subtractionMount(int number)
    {
        this.mount-=number;
    }

    public void subtractionBullet(int number)
    {
        this.bullet-=number;
    }

    public void subtractionWhist(Player player, int number)
    {
        for (Map.Entry entry: whist.entrySet()) {
            if (entry.getKey().equals(player)) entry.setValue((Integer) entry.getValue()-number);
        }
    }

    public int getWhist(Player player)
    {
        for (Map.Entry entry: whist.entrySet()) {
            if (player.equals(entry.getKey())) return (int)entry.getValue();
        }
        return 0;
    }

    public HashMap<Player, Integer> getWhist() {
        return whist;
    }

    public void setWhist(HashMap<Player, Integer> whist) {
        this.whist = whist;
    }

    public void clearPoint()
    {
        this.point = DEFAULT_POINT;
    }

    public Spectator getPlayer() {
        return player;
    }

    public CardsPack getCards() {
        return cards;
    }

    public int getBullet() {
        return bullet;
    }

    public int getMount() {
        return mount;
    }


    public void setHashMap(HashMap<Integer, SuitCard> hashMap) {
        this.hashMap = hashMap;
    }

    public HashMap<Integer, SuitCard> getHashMap() {
        return hashMap;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(player.getFirstName())
                .append(" ").append(player.getSecondName())
                .append(" РЕАКЦИЯ - ").append(status).append("\n");
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object obj) {//Переопределил пока на время, но это не правильно
        //правильнее было бы сделать с помощью уникального идентификатора (Применить сессии)
        if (this == obj) return true;
        if (this.getClass()!=obj.getClass()) return false;
        if (obj==null) return false;

        Player object = (Player)obj;
        return (this.getPlayer().getFirstName().equals(object.getPlayer().getFirstName()) &&
                this.getPlayer().getSecondName().equals(object.getPlayer().getSecondName()));
    }


}
