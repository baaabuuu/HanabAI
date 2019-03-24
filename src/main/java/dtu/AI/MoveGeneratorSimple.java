package dtu.AI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import ai_actions.Action;
import ai_actions.ActionDiscard;
import ai_actions.ActionHint;
import ai_actions.ActionPlay;
import dtu.hanabi_ai_game.Board;
import dtu.hanabi_ai_game.Card;
import dtu.hanabi_ai_game.SuitEnum;
import log.Log;

public class MoveGeneratorSimple implements MoveGenerator {

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
		HashSet<Action> possibleActions = new HashSet<Action>();
		//Replace the players predicted hand with X
		
		ArrayList<Card> predictedHand = predictor.predict(playerHands, currPlayer, origPlayer, board);
		playerHands.set(currPlayer, predictedHand);
		for(int i = 0; i < playerCount; i++)
		{
			if (i != currPlayer && origPlayer != i)
			{
				//Hint if possible.
				if (board.getClueTokens() > 0)
				{
					possibleActions.addAll(generateHints(playerHands.get(i), i));
				}
			}
			else
			{
				//Detect all possiblePlay.
				possibleActions.addAll(generatePossiblePlays(playerHands.get(i), scorePool));
				
				//Best discard if possible.
				if (board.getClueTokens() < 8)
				{
					possibleActions.add(bestDiscard(playerHands.get(i), scorePool, board));
				}
			}
		}
		//Create a wrapper for each possible move.		
		for (Action action : possibleActions)
		{
			Board newBoardState = board.copyState();
			applyAction(action, newBoardState, currPlayer);
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
				int cardValue = Character.getNumericValue(output.charAt(1));

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
	 * @author s164166
	 * @param scorePool
	 * @param card
	 * @return
	 */
	private boolean checkPossiblePlay(int[] scorePool, Card card) {
		if (card.isValueRevealed() && !card.isSuitRevealed() && scorePool[5] == 0)
		{
			for (int i = 0; i < 5; i++)
			{
				if (scorePool[i]+1 != card.getCardValue())
				{
					return false;
				}
			}
			Log.important("AI is trying to play a " + card.getCardSuit().getSuitChar() + card.getCardValue() + " pool consisting of " + scorePool[0]);
			return true;
		}
		
		if (card.isValueRevealed() && card.isSuitRevealed() && scorePool[card.getCardSuit().getID()] + 1 == card.getCardValue())
		{
			Log.important("AI is considering a play of " + card.getCardSuit().getSuitChar() + card.getCardValue() + " to be a good move");

			return true;
		}
		return false;
	}


	private Collection<? extends Action> generatePossiblePlays(ArrayList<Card> hand, int[] scorePool) {
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

	private Action bestDiscard(ArrayList<Card> hand, int[] scorePool, Board board)
	{

		//Identify possible discard targets, instead of considering all - states we want only the best possible discard thing.
		int discardTarget = checkIfHandContainsUnplayableCard(hand, scorePool, board);
		if (discardTarget == -1)
		{
			Log.log("Card was not considered unplayable");
			discardTarget = removeDuplicate(hand);
			if (discardTarget == -1)
			{
				Log.log("Card was not considered a duplicate");
				discardTarget = noInformation(hand);
				if (discardTarget == -1)
				{
					Log.log("No card information");
					discardTarget = noValue(hand);
					if (discardTarget == -1)
					{
						Log.log("No info about value");
						discardTarget = noSuit(hand);
						if (discardTarget == -1)
						{
							Log.log("No info about suit");

							Log.important("Oh lord, this is an extreme edge case");
							discardTarget = 0;
						}
					}
				}
			}
		}
		return new ActionDiscard(discardTarget);
	}

	private Collection<? extends Action> generateHints(ArrayList<Card> hand, int playerID)
	{
		HashSet<Action> collection = new HashSet<Action>();
		for (Card card : hand)
		{
			if (!card.isSuitRevealed())
			{
				Action action = new ActionHint(playerID, card.getCardSuit().getSuitChar());
				
				collection.add(action);

			}
			if (!card.isValueRevealed())
			{
				Action action = new ActionHint(playerID, String.valueOf(card.getCardValue()));
				collection.add(action);	
			}
		}
		return collection;
	}

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
	
	private int noInformation(ArrayList<Card> hand) {
		for (int j = 0; j < hand.size(); j++) {
			Card cardConsider = hand.get(j);
			if (!cardConsider.isValueRevealed() && !cardConsider.isSuitRevealed()) {
				return j;
			}
		}
		return -1;
	}

	private int removeDuplicate(ArrayList<Card> hand) {
		for (int j = 0; j < hand.size(); j++)
		{
			Card cardConsider = hand.get(j);
			for (int k = j; k < hand.size() - 1; k++)
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

	public int checkIfHandContainsUnplayableCard(ArrayList<Card> hand, int[] scorePool, Board board)
	{
		for (int i = 0; i < hand.size(); i++)
		{
			Card cardConsider = hand.get(i);
			//Check if value is possible to play
			for (int k = 0; k < 5; k++)
			{
				if (!cardConsider.isSuitRevealed() && cardConsider.isValueRevealed() && cardConsider.getCardValue() < scorePool[k])
				{
					return i;
				}
			}
			if (cardConsider.isSuitRevealed() && !checkIfSuitIsPlayable(cardConsider.getCardSuit(), scorePool, board))
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
			if (discardPile[suitIndex][0] < 3)
			{
				return true;
			}
		}
		else if (currScore > 0 && currScore < 3)
		{
			if( discardPile[suitIndex][currScore] < 2)
			{
				return true;
			}
		}
		else if (currScore == 4)
		{
			if (discardPile[suitIndex][4] != 1)
			{
				return true;
			}
		}	
		Log.important("AI is considering the suit " + suit.getSuitChar() + " to be useless, due to no cards being playable");
		return false;
	}

}