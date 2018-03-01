import java.io.Console;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        CardsPack cardsPack = new CardsPack();
      /*  for (int i = 0; i < cardsPack.getCardsPack().size(); i++) {
            System.out.println(cardsPack.getCardsPack().get(i).getName() + " " + cardsPack.getCardsPack().get(i).getSuit());
        }*/
        List<Spectator> spectators = new ArrayList<>();
        spectators.add(new Spectator("Айрат","Мухутдинов"));
        spectators.add(new Spectator("2","2"));
        spectators.add(new Spectator("3","3"));

        GroupPlayers groupPlayers = new GroupPlayers(spectators,cardsPack.getCardsPack());
        for (int j = 0; j < 3; j++) {
            System.out.println("Для игрока " + groupPlayers.getGroupPlayers().get(j).getPlayer().getFirstName());
            for (int i = 0; i < 10; i++) {
                System.out.println(groupPlayers.getGroupPlayers().get(j).getCards().getCardsPack().get(i).getName() + " " +
                        groupPlayers.getGroupPlayers().get(j).getCards().getCardsPack().get(i).getSuit());
            }
            System.out.println("---------------------------------------------");
        }

    }
}
