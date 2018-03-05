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

        GroupPlayers groupPlayers = new GroupPlayers(spectators);
        System.out.println("====КОЛОДА==========");
        for (Card card: groupPlayers.getCardPacks()) {
            System.out.println(card.getName() + " " + card.getSuit() + " " + card.getRank());
        }



        System.out.println("Для прикупа " + groupPlayers.getPrikup().getCardsPack().get(0).getName());
        System.out.println("Для прикупа " + groupPlayers.getPrikup().getCardsPack().get(1).getName());

        System.out.println("====================================\n");
        Play play = new Play(groupPlayers);
        play.start();
        System.out.println("====================================\n");
        for (int j = 0; j < 3; j++) {
            System.out.println("Для игрока " + play.getGroupPlayers().getPlayers().get(j).getPlayer().getFirstName());
            for (int i = 0; i < play.getGroupPlayers().getPlayers().get(j).getCards().size(); i++) {
                System.out.println(play.getGroupPlayers().getPlayers().get(j).getCards().getCardsPack().get(i).getName() + " " +
                        play.getGroupPlayers().getPlayers().get(j).getCards().getCardsPack().get(i).getSuit() + " " +
                        play.getGroupPlayers().getPlayers().get(j).getCards().getCardsPack().get(i).getRank());
            }
            System.out.println("---------------------------------------------\n");
        }
        System.out.println("Первый ходит = " + play.getGroupPlayers().getPlayers().get(play.getIndexFirstPlayerGoing()));

        System.out.println("Выиграл торговлю = " + play.getWinner());
        System.out.println("MAX Card = " + (play.getGroupPlayers().getPlayers().get(
                play.getIndexFirstPlayerGoing()).getHashMap().keySet()));

        System.out.println("Победитель в торговле:");
        System.out.println(play.getWinner().entrySet().iterator().next().getKey());
        System.out.println("С картой = " + play.getWinner().entrySet().iterator().next().getValue());
        System.out.println("Играем в - "+play.getGameMod());
        /*for (int i = 0; i < 2; i++) {
            System.out.println(groupPlayers.getPrikup().getCardsPack().get(i).getName() + " " +
                    groupPlayers.getPrikup().getCardsPack().get(i).getSuit() + " " +
                    groupPlayers.getPrikup().getCardsPack().get(i).getRank() + " ");
        }*/


        //groupPlayers.mixCardsPack();
        /*for (int i = 0; i < groupPlayers.getCardPacks().size(); i++) {
            System.out.println(groupPlayers.getCardPacks().get(i).getName() + " " +
                    groupPlayers.getCardPacks().get(i).getSuit());
        }*/

    }
}
