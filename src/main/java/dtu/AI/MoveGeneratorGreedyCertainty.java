package dtu.AI;

import java.util.ArrayList;

import ai_actions.Action;
import ai_actions.ActionDiscard;
import ai_actions.ActionHint;
import ai_actions.ActionPlay;
import ai_actions.MoveWrapper;
import dtu.hanabi_ai_game.Board;
import dtu.hanabi_ai_game.Card;
import dtu.hanabi_ai_game.SuitEnum;

public class MoveGeneratorGreedyCertainty implements MoveGenerator {
	private Board b;
	private ArrayList<Card> hand;
	private ArrayList<ArrayList<Card>> possibleHands = new ArrayList<ArrayList<Card>>();
	@Override
	public void generateMoves(MoveWrapper wrapper, int maxDepth, int currPlayer, int origPlayer, Predictor predictor) {
		
	
		
		
		
		
		
		expandWrapperNode(wrapper,origPlayer);
		// TODO Auto-generated method stub

	}
	public MoveGeneratorGreedyCertainty(MoveWrapper w, int aiplayer) {
		this.b = w.getBoard();
		this.hand = w.getBoard().getPlayerHand(aiplayer);
	}
/**
 * Make the board that would result from the action and add it to the wrapper
 * 
 * */
private MoveWrapper buildWrapper(Action a, Board b, int turn) {
	

	Board board = b;
	applyAction(a,board,turn);
	
	return new MoveWrapper(a,board);
	
}
private boolean matchHandConditional(Card c, int position) {
	for(int i= 0;i<4;i++) {
		//Check if card at position matches known information, if not we can discard combination
	}
	
	return true;
}
private  void combinationUtil(Card arr[], Card data[], int start, int end, int index, int r) 
{ 
// Current combination is ready to be printed, print it 

	if (index == r) 
	{ 
	boolean valid = false;
	ArrayList<Card> tmp = new ArrayList<Card>();
	for (int j=0; j<r; j++) {
	if( matchHandConditional(data[j],j)) {
			valid = true;
	}
		tmp.add(data[j]);
	
	if(valid) possibleHands.add(tmp);
	} 
	
	return;

	
	}

// replace index with all possible elements. The condition 
// "end-i+1 >= r-index" makes sure that including one element 
// at index will make a combination with remaining elements 
// at remaining positions 
for (int i=start; i<=end && end-i+1 >= r-index; i++) 
{ 
data[index] = arr[i]; 
combinationUtil(arr, data, i+1, end, index+1, r); 
} 
} 

// The main function that prints all combinations of size r 
// in arr[] of size n. This function mainly uses combinationUtil() 
private  void printCombination(Card arr[], int n, int r) 
{ 
// A temporary array to store all combination one by one 
Card data[]=new Card[r]; 

// Print all combination using temprary array 'data[]' 
combinationUtil(arr, data, 0, n-1, 0, r); 

}

private void addStocasticStates(Action a,Board b) {
	
	
	
	
}

private ArrayList<ArrayList<Card>> generatePossibleHands(int handsize, int aiplayer, MoveWrapper w){
	
	Board b = w.getBoard();
	ArrayList<Card> 
	
	
	
}
	
public void expandWrapperNode(MoveWrapper w, int aiplayer) {
		
		//Add all valid actions
		//Note: Unsafe reference to player
		
		Board board = w.getBoard();
		//We can always play or discard
		for(int j = 0;j<4;j++) {
			w.addMove(buildWrapper(new ActionPlay(j),board, aiplayer));
			w.addMove(buildWrapper(new ActionDiscard(j),board, aiplayer));
		}
		//If it is possible to hint
		if(board.getClueTokens() != 0) {
			
		for(int i=0;i<board.getPlayerHands().size();i++) {
				//We can only hint players that are not ourselves
				
				
				
					//For a given player we only consider valid hints i.e. don't consider hinting about green if a player has no green	
				Boolean[] suits = {false,false,false,false,false};
				Boolean[] values = {false,false,false,false,false};
				//Handle offset
				if(i != aiplayer) {
					board.getPlayerHands().get(i).forEach((x)-> {
						suits[x.getCardSuit().getID()] = true;
						values[x.getCardValue()-1] = true;
					});
				
				
				
				//Itterate the possiblites
				for(int k = 0;k<4;k++) {
					if(suits[k]) {
						w.addMove(buildWrapper(new ActionHint(i,getEnumFromId(k).getSuitChar()),board, aiplayer));
					}
					if(values[k]) {
						w.addMove(buildWrapper(new ActionHint(i,String.valueOf(k+1)),board, aiplayer));
						}
					}
					
				
				
				}
		}
			}

}


private SuitEnum getEnumFromId(int id) {
	
	switch(id) {
	case 0:
		return SuitEnum.WHITE;
	case 1:
		return SuitEnum.RED;
	case 2:
		return SuitEnum.BLUE;
	case 3:
		return SuitEnum.YELLOW;
	case 4:
		return SuitEnum.GREEN;
	default:
		return null;
	}
	

} 

private void applyAction(Action action, Board board, int turn) 
{
	String output = action.play();
	if (output.charAt(0) == 'P')
	{
		int cardIndex = Character.getNumericValue(output.charAt(1));
		Card card = board.getPlayerHand(turn).get(cardIndex);
		if (card.isSuitRevealed() != true)
		{
			board.playCard(card, 5);
		}
		else
		{
			board.playCard(card, card.getCardSuit().getID());
		}
		board.addPoint();
	}
	else if (output.charAt(0) == 'D')
	{
		int cardIndex = Character.getNumericValue(output.charAt(1));
		Card card = board.getPlayerHand(turn).get(cardIndex);
		board.discardCard(card);
		board.addClueToken();
	}
	else
	{
		int playerIndex = Character.getNumericValue(output.charAt(1));
		char identified = output.charAt(2);
		board.removeClueToken();
		if (identified == '1' || identified == '2' || identified == '3' || identified == '4' || identified == '5')
		{
			int cardValue = Character.getNumericValue(output.charAt(2));
			ArrayList<Card> hand = board.getPlayerHand(playerIndex);
			for (Card card : hand)
			{
				if (card.getCardValue() == cardValue)
				{
					card.revealValue();
				}
			}

		}
		else
		{
			ArrayList<Card> hand = board.getPlayerHand(playerIndex);
			for (Card card : hand)
			{
				if (card.getCardSuit().getSuitChar().charAt(0) == identified)
				{
					card.revealSuit();
				}
			}
		}
	}		
}

}
