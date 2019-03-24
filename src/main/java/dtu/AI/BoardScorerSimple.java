package dtu.AI;

import java.util.ArrayList;

import dtu.hanabi_ai_game.Board;
import dtu.hanabi_ai_game.Card;
import dtu.hanabi_ai_game.SuitEnum;
import log.Log;

public class BoardScorerSimple implements BoardScorer
{
	private int pointsForInformation = 1;
	private int pointsForPossibleFuture = 2;
	private int pointsForNextPlay = 100;
	private int pointsForLast1Numeric = 50;
	private int pointsForSecondLast1Numeric = 10;
	private int pointsForLastCopy = 5;
	private int pointsPerHintToken = -1;
	private int pointsPerScore = 200;

	private int calculateLastCopyScore(Card card, Board board)
	{
		return pointsForLastCopy*(6-card.getCardValue());
	}
	private int calculateHintTokenValue(int tokenCount)
	{
		return tokenCount*pointsPerHintToken;
	}
	
	@Override
	public int getBoardScore(Board board, int origTurn)
	{
		//Look at points, points*100
		//Look at hint tokens, fewer tokens = increasing value
		//Look at information, how much information is available
		int score = 0;
		ArrayList<ArrayList<Card>> hands = board.getPlayerHands();
		hands.remove(origTurn);
		for (ArrayList<Card> hand : board.getPlayerHands())
		{
			score += countPointsForSuitAndValue(hand, board.getFireworkStacks(), origTurn, board);
		}
		score += calculateHintTokenValue(board.getClueTokens());
		score += pointsPerScore*board.getScore();
		return score;
	}
	
	/**
	 * Take a hand and count the points for it.
	 * @author s164166
	 * @param hand
	 * @param scorePool
	 * @return
	 */
	public int countPointsForSuitAndValue(ArrayList<Card> hand, int[] scorePool, int origTurn, Board board)
	{
		int sum = 0;
		for (Card card : hand)
		{
			if (card.isSuitRevealed())
			{
				sum += pointsForInformation;
			}
			if (card.isValueRevealed())
			{
				sum += pointsForInformation;
				if (checkPossiblePlay(scorePool, card))
				{
					Log.important("FOUND POSSIBLE NEXT PLAY");
					sum += pointsForNextPlay;
				}
			}
			
			if (checkPossibleFuturePlay(scorePool, card))
			{
				sum += pointsForPossibleFuture;
				sum += addPointsForImportance(card, board);
				
			}
			
		}
		return sum;
	}


	
	private int addPointsForImportance(Card card, Board board) {
		int count = 0;
		for (Card checkingCard : board.getPlayedCards())
		{
			if (checkingCard.getCardSuit().equals(card.getCardSuit()) && card.getCardValue() == checkingCard.getCardValue())
			{
				count++;
			}
		}
		if (count == 2) //its the last card with a numeric value of 1 in this suit.
		{
			return pointsForLast1Numeric;
		}
		if (count == 1 && card.getCardValue() == 1) //Its a 1 but not the last
		{
			return pointsForSecondLast1Numeric;
		}
		else if (card.getCardValue() == 5 || count == 1) //Its the last copy of this card.
		{
			return calculateLastCopyScore(card, board);
		}
		return 0;
	}

	/**
	 * Check if its possible to play a card.
	 * @author s164166
	 * @param scorePool
	 * @param card
	 * @return
	 */
	private boolean checkPossiblePlay(int[] scorePool, Card card) {
		if (!card.isSuitRevealed() && scorePool[5] == 0)
		{
			for (int i = 0; i < 5; i++)
			{
				if (scorePool[i]+1 != card.getCardValue())
				{
					return false;
				}
			}
			return true;
		}
		
		if (card.isSuitRevealed() && scorePool[card.getCardSuit().getID()] + 1 == card.getCardValue())
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Check if its possible to play in the future card.
	 * @author s164166
	 * @param scorePool
	 * @param card
	 * @return
	 */
	private boolean checkPossibleFuturePlay(int[] scorePool, Card card) 
	{	
		return scorePool[card.getCardSuit().getID()] < card.getCardValue();
	}
	
	/**
	 * Identify if a specific suit is playable.
	 * @param suit
	 * @param score
	 * @param board
	 * @return
	 */
	public boolean checkIfSuitIsPlayable(SuitEnum suit, Board board)
	{
		int suitIndex = suit.getID();
		int currScore = board.getFireworkStacks()[suitIndex];
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
		return false;
	}



}
