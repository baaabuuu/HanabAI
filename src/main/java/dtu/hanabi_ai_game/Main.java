package dtu.hanabi_ai_board;

public class Main {

	public static void main(String[] args)
	{
        System.out.println("Hello \u001b[1;31mred\u001b[0m world!");

		Board game = new Board();
		game.createNewGame(1);
		Board copy = game.copyState();
		System.out.println(game.getPlayerHand(0).get(0).getStringRepresentation());
		System.out.println(copy.getPlayerHand(0).get(0).getStringRepresentation());
		System.out.println(game.getPlayerHand(0).get(0).toString());
		System.out.println(copy.getPlayerHand(0).get(0).toString());
		System.out.println("done");
		
	}
}
