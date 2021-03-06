package dtu.AI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import ai_actions.Action;
import ai_actions.ActionDiscard;
import ai_actions.ActionHint;
import ai_actions.ActionPlay;
import ai_actions.MoveWrapper;
import dtu.hanabi_ai_game.Board;
import dtu.hanabi_ai_game.Card;
import dtu.hanabi_ai_game.SuitEnum;
import log.Log;

/**
 * A likely improvement based on MoveGenerator
 * @author s164166
 *
 */
public class MoveGeneratorAdvanced implements MoveGenerator {

	private SuitEnum[] suits = {SuitEnum.WHITE, SuitEnum.RED, SuitEnum.BLUE, SuitEnum.YELLOW, SuitEnum.GREEN};
			
	@Override
	/**
	 * Generates a series of possible moves given the information that is available.
	 * @author s164166
	 * @param wrapper
	 * @param maxDepth
	 * @param currPlayer
	 * @param origPlayer
	 */
	public void generateMoves(MoveWrapper wrapper, int maxDepth, int currPlayer, int origPlayer, Predictor predictor)
	{	
		

		Board board = wrapper.getBoard();
		int playerCount = board.getPlayerHands().size();

		ArrayList<ArrayList<Card>> playerHands = board.getPlayerHands();
		int scorePool[] = board.getFireworkStacks();
		//A set allows for unique actions, we don't wanna accidentally look at what happens when 2 green cards are played.
		ArrayList<Action> possibleActions = new ArrayList<Action>();
		//Replace the players predicted hand with X
		
		ArrayList<Card> predictedHand = predictor.predict(playerHands, currPlayer, origPlayer, board);
		playerHands.set(currPlayer, predictedHand);
		for(int i = 0; i < playerCount; i++)
		{
			if (i != currPlayer && i != origPlayer)
			{
				//Hint if possible.
				if (board.getClueTokens() > 0)
				{
					//Log.log("Generating Hints about player: " + (i+1) + " for player: " + (currPlayer+1));
					possibleActions.addAll(generateHints(playerHands.get(i), i));
				}
			}
			else
			{
				if ( i == currPlayer)
				{
					//Log.log("Generating Possible Plays for player: " + (i+1));
					//Detect all possiblePlay.
					possibleActions.addAll(generatePossiblePlays(playerHands.get(i), scorePool));
					//Best discard if possible.
					if (board.getClueTokens() < 8)
					{
						//Log.log("Generating Discard for player: " + (i+1));

						possibleActions.add(bestDiscard(playerHands.get(i), scorePool, board));
						//Log.log("Discard option found was: " + possibleActions.get(possibleActions.size() - 1).play());

					}
				}
			}
		}
		//Create a wrapper for each possible move.		
		for (Action action : possibleActions)
		{
			Board newBoardState = board.copyState();
			applyAction(action, newBoardState, currPlayer, origPlayer);
			MoveWrapper newWrapper = new MoveWrapper(action, newBoardState);
			wrapper.addMove(newWrapper);
		}
	}
	
	/**
	 * Applies an action to the fake board and updates it for scoring it.
	 * @author s164166
	 * @param action
	 * @param board
	 * @param turn
	 */
	private void applyAction(Action action, Board board, int turn, int origTurn) 
	{
		String output = action.play();
		if (output.charAt(0) == 'P')
		{
			int cardIndex = Character.getNumericValue(output.charAt(1));
			Card card = board.getPlayerHand(turn).get(cardIndex);
			if (card.isSuitRevealed() != true || card.isValueRevealed() != true)
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
			if (turn != origTurn)
			{
				board.discardCard(card);
			}
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
	
	/**
	 * Check if its possible to play a card.
	 * Value has to be revealed before.
	 * @author s164166
	 * @param scorePool
	 * @param card
	 * @return
	 */
	private boolean checkPossiblePlay(int[] scorePool, Card card) {
		if (card.isValueRevealed())
		{
			if (!card.isSuitRevealed())
			{
				for (int i = 0; i < 5; i++)
				{
					if (card.isCard(suits[i]) && scorePool[i] + 1 != card.getCardValue())
					{
						return false;
					}
				}
				return true;
			}
			
			if ( card.isSuitRevealed() && scorePool[card.getCardSuit().getID()] + 1 == card.getCardValue())
			{
				return true;
			}

		}
		if (card.isPlayable())
		{
			return true;
		}
		return false;
	}


	/**
	 * Generate teh possible plays and add them to the hand.
	 * @author s164166
	 * @param hand
	 * @param scorePool
	 * @return
	 */
	private Collection<? extends Action> generatePossiblePlays(ArrayList<Card> hand, int[] scorePool) 
	{
		HashSet<Action> playset = new HashSet<Action>();
		for (int i = 0; i < hand.size(); i++)
		{
			Card card = hand.get(i);
			if (checkPossiblePlay(scorePool, card))
			{
				playset.add(new ActionPlay(i));
			}
		}
		return playset;
	}

	/**
	 * Same as in Movegenerator - but with bugfixes
	 * @author s160902
	 * @param hand
	 * @param scorePool
	 * @param board
	 * @return
	 */
	private Action bestDiscard(ArrayList<Card> hand, int[] scorePool, Board board)
	{

		//Identify possible discard targets, instead of considering all - states we want only the best possible discard thing.
		int discardTarget = checkIfHandContainsUnplayableCard(hand, scorePool, board);
		if (discardTarget == -1)
		{
			discardTarget = removeDuplicate(hand);
			if (discardTarget == -1)
			{
				discardTarget = noInformation(hand);
				if (discardTarget == -1)
				{
					discardTarget = noValue(hand);
					if (discardTarget == -1)
					{
						discardTarget = noSuit(hand);
						if (discardTarget == -1)
						{
							discardTarget = 0;
						}
					}
				}
			}
		}
		return new ActionDiscard(discardTarget);
	}

	/**
	 * Generate the hints given
	 * @author s164166
	 * @param hand
	 * @param playerID
	 * @return
	 */
	private Collection<? extends Action> generateHints(ArrayList<Card> hand, int playerID)
	{
		ArrayList<Action> collection = new ArrayList<Action>();
		for (Card card : hand)
		{
			if (!card.isSuitRevealed())
			{
				Action action = new ActionHint(playerID, card.getCardSuit().getSuitChar());
				if (!collection.stream().anyMatch(checkAction -> checkAction.play().equals(action.play())))
				{
					collection.add(action);
				}

			}
			if (!card.isValueRevealed())
			{
				Action action = new ActionHint(playerID, String.valueOf(card.getCardValue()));
				if (!collection.stream().anyMatch(checkAction -> checkAction.play().equals(action.play())))
				{
					collection.add(action);
				}
			}
		}
		return collection;
	}
	
	/**
	 * Check if a card in hand hasnt had its suit revealed, if so its a discard target
	 * @author s164166
	 * @param hand
	 * @return
	 */
	private int noSuit(ArrayList<Card> hand)
	{
		for (int j = 0; j < hand.size(); j++)
		{
			Card cardConsider = hand.get(j);
			if (!cardConsider.isSuitRevealed())
			{
				return j;
			}
		}
		return -1;
	}
	
	/**
	 * Check if a cards value is revealed, if not its the discard target.
	 * @author s164166
	 * @param hand
	 * @return
	 */
	private int noValue(ArrayList<Card> hand)
	{
		for (int j = 0; j < hand.size(); j++)
		{
			Card cardConsider = hand.get(j);
			if (!cardConsider.isValueRevealed())
			{
				return j;
			}
		}
		return -1;
	}
	
	/**
	 * If no information exists about a card, discard it.
	 * @author s164166
	 * @param hand
	 * @return
	 */
	private int noInformation(ArrayList<Card> hand) {
		for (int j = 0; j < hand.size(); j++) {
			Card cardConsider = hand.get(j);
			if (!cardConsider.isValueRevealed() && !cardConsider.isSuitRevealed()) {
				return j;
			}
		}
		return -1;
	}

	/**
	 * If a card is a duplicate, remove it.
	 * @author s164166
	 * @param hand
	 * @return
	 */
	private int removeDuplicate(ArrayList<Card> hand) {
		for (int j = 0; j < hand.size(); j++)
		{
			Card cardConsider = hand.get(j);
			for (int k = j+1; k < hand.size() - 1; k++)
			{
				Card otherCard = hand.get(k);
				if (cardConsider.isSuitRevealed() && cardConsider.isValueRevealed() &&
						otherCard.isSuitRevealed() && otherCard.isValueRevealed() &&
						cardConsider.getCardValue() == otherCard.getCardValue() &&
						cardConsider.getCardSuit().equals(otherCard.getCardSuit()))
				{
					return j;
				}		
			}
		}
		return -1;
	}

	/**
	 * If a card is unable to played - discard it.
	 * @author s164166
	 * @param hand
	 * @param scorePool
	 * @param board
	 * @return
	 */
	public int checkIfHandContainsUnplayableCard(ArrayList<Card> hand, int[] scorePool, Board board)
	{
		for (int i = 0; i < hand.size(); i++)
		{
			Card card = hand.get(i);
			//Check if value is possible to play
			if (!card.isSuitRevealed() && card.isValueRevealed())
			{
				boolean check = true;
				for (int k = 0; k < 5; k++)
				{					
					if (card.getCardValue() > scorePool[k])
					{
						check = false;
					}
				}
				if (check)
				{
					return i;
				}
			}
			
			if (card.isSuitRevealed() && !checkIfSuitIsPlayable(card.getCardSuit(), scorePool, board))
			{
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Identify if a specific suit is playable.
	 * @param suit
	 * @param score
	 * @param board
	 * @return
	 */
	public boolean checkIfSuitIsPlayable(SuitEnum suit, int[] score, Board board)
	{
		int suitIndex = suit.getID();
		int currScore = score[suitIndex];
		int[][] discardPile = board.getDiscardMatrix();
		if (currScore == 0)
		{
			if (discardPile[suitIndex][currScore] != 3)
			{
				return true;
			}
		}
		else if (currScore == 1 || currScore == 2 || currScore == 3)
		{
			if(discardPile[suitIndex][currScore] != 2)
			{
				return true;
			}
		}
		else if (currScore == 4)
		{
			if (discardPile[suitIndex][currScore] != 1)
			{
				return true;
			}
		}	
		Log.important("AI is considering the suit " + suit.getSuitChar() + " to be useless, due to no cards being playable");
		return false;
	}

}
