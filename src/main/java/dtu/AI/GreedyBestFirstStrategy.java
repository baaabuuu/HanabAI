package dtu.AI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

import ai_actions.Action;
import ai_actions.ActionDiscard;
import ai_actions.ActionHint;
import ai_actions.ActionPlay;
import dtu.hanabi_ai_game.Board;
import dtu.hanabi_ai_game.Card;
import dtu.hanabi_ai_game.SuitEnum;
import log.Log;
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
		Log.important("Starting a searchin for Patrick&Christian AI");
		MoveWrapper wrapper = new MoveWrapper(null, gameState.copyState());
		generateMoves(wrapper, 0, depth, id);
		return "IMPLEMENT ME";
	}
	
	public ArrayList<Card> generatePotentialCards(ArrayList<ArrayList<Card>> playerhands, int playerTurn)
	{
		ArrayList<Card> originalDeck = gameState.generateDeck();
		for (Card card : gameState.getPlayedCards())
		{
			originalDeck.remove(card);
		}
		for (int i = 0; i < playerCount; i++)
		{
			if (i != id && i != playerTurn)
			{
				originalDeck.removeAll(playerhands.get(i));
			}
			else
			{					
				for (Card card : playerhands.get(i))
				{
					if (card.isSuitRevealed() && card.isValueRevealed())
					{
						originalDeck.remove(card);
					}
				}
			}
		}
		return originalDeck;
	}
	
	public ArrayList<Card> predictCards(ArrayList<Card> playerHand, ArrayList<ArrayList<Card>> hands, int turn)
	{ 
		//Oh lord
		ArrayList<Card> attempt = new ArrayList<Card>();
		for (Card card : playerHand)
		{
			attempt.add(card.copyCard());
		}
		ArrayList<Card> potentialCards = generatePotentialCards(hands, turn);

		boolean guessedSuite = false;
		boolean guessedValues = false;
		while(true)
		{
			boolean guess = false;
			for (int i = 0; i < attempt.size(); i++)
			{
				Card card = attempt.get(i);
				if (card.isSuitRevealed() && !card.isValueRevealed())
				{
					int guessNumber = 0;
					for (Card cardGuess : potentialCards)
					{
						if (cardGuess.getCardSuit().equals(card.getCardSuit()) && guessNumber == 0)
						{
							guessNumber = cardGuess.getCardValue();

						}
						else if (cardGuess.getCardSuit().equals(card.getCardSuit()) && guessNumber != cardGuess.getCardValue())
						{
							guessNumber = 0;
							break;
						}
					}
					if (guessNumber != 0)
					{
						potentialCards.remove(card);
						card.revealValue();
						Log.important("Predicted the card " + card.getStringRepresentation());
						guess = true;
						break;
					}
					
				}
				else
				if (card.isValueRevealed() && !card.isSuitRevealed())
				{
					int guessSuit = -1;
					for (Card cardGuess : potentialCards)
					{
						if (cardGuess.getCardValue() == card.getCardValue() && guessSuit == -1)
						{
							guessSuit = cardGuess.getCardSuit().getID();

						}
						else if (cardGuess.getCardValue() == card.getCardValue() && guessSuit != cardGuess.getCardSuit().getID())
						{
							guessSuit = -1;
							break;
						}
					}
					if (guessSuit != -1)
					{
						potentialCards.remove(card);
						card.revealValue();
						Log.important("Predicted the card " + card.getStringRepresentation());
						guess = true;
						break;
					}
				}

			}
			
			if (guessedSuite == false && potentialCards.size() <= 10)
			{
				int guessSuit = -1;
				for (Card cardGuess : potentialCards)
				{
					if (guessSuit == -1)
					{
						guessSuit = cardGuess.getCardSuit().getID();
					}
					else if (guessSuit != cardGuess.getCardSuit().getID())
					{
						guessSuit = -1;
						break;
					}
				}
				if (guessSuit != -1)
				{
					for (Card iteratingCard : attempt)
					{
						iteratingCard.revealSuit();
					}
					guessedSuite = true;
					guess = true;
				}
			}
			
			if (guessedValues == false && potentialCards.size() <= 3)
			{
				int guessNumber = 0;
				for (Card cardGuess : potentialCards)
				{
					if (guessNumber == 0)
					{
						guessNumber = cardGuess.getCardValue();
					}
					else if (guessNumber != cardGuess.getCardValue())
					{
						guessNumber = 0;
						break;
					}
				}
				if (guessNumber != 0)
				{
					guessedValues = true;
					for (Card iteratingCard : attempt)
					{
						iteratingCard.revealValue();
					}
					guess = true;
				}
			}
			
			if (!guess)
			{
				break;
			}
		}
		
		
		
		return attempt;
	}
	
	
	
	public void generateMoves(MoveWrapper wrapper, int currDepth, int maxDepth, int currPlayer)
	{
		Board board = wrapper.getBoard();
		ArrayList<ArrayList<Card>> playerHands = new ArrayList<ArrayList<Card>>();
		addPlayerHands(playerHands, board);
		int scorePool[] = board.getFireworkStacks();
		HashSet<Action> possibleActions = new HashSet<Action>();

		for(int i = 0; i < playerCount; i++)
		{
			if (i != currPlayer )
			{
				//Hint
				if (board.getClueTokens() > 0)
				{
					possibleActions.addAll(generateHints(playerHands.get(i), i));
				}
			}
			else
			{
				//Detect all possiblePlay
				possibleActions.addAll(generatePossiblePlays(playerHands.get(i), scorePool));
				
				//Detect best discard option
				Action discardAction = bestDiscard(playerHands.get(i), scorePool, board);
				if (discardAction != null)
				{
					possibleActions.add(discardAction);
				}				
			}
		}
		
	}
	
	private Collection<? extends Action> generatePossiblePlays(ArrayList<Card> hand, int[] scorePool) {
		HashSet<Action> playset = new HashSet<Action>();
		for (int i = 0; i < hand.size(); i++)
		{
			Card card = hand.get(0);
			if (card.isSuitRevealed() && card.isValueRevealed() && scorePool[card.getCardSuit().getID()]+1 == card.getCardValue())
			{
				playset.add(new ActionPlay(i));
			}
			else if (card.isValueRevealed())
			{
				boolean possibleMove = true;
				for (int j = 0; j < 5; j++)
				{
					if (scorePool[j] >= card.getCardValue())
					{
						possibleMove = false;
					}
				}
				if (possibleMove)
				{
					playset.add(new ActionPlay(i));
				}
			}
		}
		return playset;
	}

	private Action bestDiscard(ArrayList<Card> hand, int[] scorePool, Board board) {
		
		if (board.getClueTokens() < 8)
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
								Log.important("Oh lord, this is an extreme edge case");
								discardTarget = 0;
							}
						}
					}
				}
			}
			return new ActionDiscard(discardTarget);
		}
		return null;
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
	
	
	
	
	
	
	
	
	public void addPlayerHands(ArrayList<ArrayList<Card>> hands, Board board)
	{
		for (int i = 0; i < playerCount; i++)
		{
				hands.add(board.getPlayerHand(i));		
		}
	}
	
	
	private void andSearch(){};
	private void orSearch(){}


}
