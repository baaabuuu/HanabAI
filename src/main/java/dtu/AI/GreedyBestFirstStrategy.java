package dtu.AI;
import java.util.ArrayList;

import ai_actions.Action;
import dtu.hanabi_ai_game.Board;
import dtu.hanabi_ai_game.Card;
public class GreedyBestFirstStrategy implements Strategy {
	private Board gameState;
	private int id;
	private int playerCount;
	
	public GreedyBestFirstStrategy(Board gameState, int id, int playerCount)
	{
		this.gameState = gameState;
		this.id = id;
		this.playerCount = playerCount;
	}
	
	public String search(int depth)
	{
		MoveWrapper wrapper = new MoveWrapper(null, gameState.copyState());
		generateMoves(wrapper, 0, depth);
		return "IMPLEMENT ME";
	}
	
	
	public void generateMoves(MoveWrapper wrapper, int currDepth, int maxDepth)
	{
		Board board = wrapper.getBoard();
		ArrayList<ArrayList<Card>> playerHands = new ArrayList<ArrayList<Card>>();
		for (int i = 0; i < playerCount; i++)
		{
			if (i != playerCount)
			{
				playerHands.add(board.getPlayerHand(i));
			}
			else
			{
				playerHands.add(new ArrayList<Card>());
			}
				
		}
		board.getPlayerHand(playerNumber)
	}
	
	
	
	private void andSearch(){};
	private void orSearch(){}


}
