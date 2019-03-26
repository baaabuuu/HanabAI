package dtu.AI;

import java.util.ArrayList;
import java.util.Arrays;

import ai_actions.Action;
import dtu.hanabi_ai_game.Board;
import dtu.hanabi_ai_game.Card;
import dtu.hanabi_ai_game.SuitEnum;

public class BoardScorerSimple implements BoardScorer
{
	private SuitEnum[] suits = {SuitEnum.WHITE, SuitEnum.RED, SuitEnum.BLUE, SuitEnum.YELLOW, SuitEnum.GREEN};

	private int pointsForInformation = 7;
	private int pointsForLowValue = 20;
	private int pointsForLeftMostPlayable = 3;
	private int pointsForPossibleFuture = 5;
	private int pointsForFullInformation = 14;
	private int pointsForLast1Numeric = 0;
	private int pointsForSecondLast1Numeric = 0;
	private int pointsForLastCopy = 0;
	
	private int pointsForDiscardFullInfo = 60;
	private int pointsForDiscardPartialInfo = 0;
	private int pointsForDiscardNoInfo = 0;
	
	private int pointsForFewToken = -50;
	
	private int pointsPerScore = 200;
	private int pointForCliff = 200;

	
	private	Double depthModifier = -0.0;
	
	private	int actionPointsDiscard = 0;
	private	int actionPointsHint = 0;
	private	int actionPointsPlay = 300;
	
	
	
	private Double depthModifier(int currDepth, int maxDepth)
	{
		return 1-(depthModifier*currDepth);
	}

	private int calculateLastCopyScore(Card card, Board board)
	{
		return pointsForLastCopy*(6-card.getCardValue());
	}
	
	private int calculateHintTokenValue(int tokenCount)
	{
		if (tokenCount < 2)
		{

			return pointsForFewToken;
		}
		return 0;
	}
	
	@Override
	public int getBoardScore(Board board, int origTurn, Action action, int currDepth, int maxDepth)
	{
		double score = 0;
		if (board.getScore() == 25)
		{
			return Integer.MAX_VALUE-1000000;
		}
		ArrayList<ArrayList<Card>> hands = new ArrayList<ArrayList<Card>>();
		
		for (ArrayList<Card> playerHand : board.getPlayerHands())
		{
			ArrayList<Card> newHand = new ArrayList<Card>();
			hands.add(newHand);
			for (Card card : playerHand)
			{
				newHand.add(card.copyCard());
			}
		}
		
		
		score += pointsForDiscard(hands.get(origTurn).get(action.getTarget()), action);
		hands.remove(origTurn);
		for (ArrayList<Card> hand : board.getPlayerHands())
		{
			score += countPointsForSuitAndValue(hand, board.getFireworkStacks(), origTurn, board);
		}
		score += calculateHintTokenValue(board.getClueTokens());
		score += pointsPerScore*board.getScore();
		score += scoreBoardRows(board);
		score += pointsForAction(action);
		score *= depthModifier(currDepth, maxDepth);
		return (int) score;
	}
	
	private double pointsForDiscard(Card card, Action action) 
	{
		if (action.getActionType().equals("D"))
		{
			if (card.isSuitRevealed() && card.isValueRevealed())
			{
				return pointsForDiscardFullInfo;
			}
			else if (card.isSuitRevealed() || card.isValueRevealed())
			{
				return pointsForDiscardPartialInfo;
			}
			else
			{
				return pointsForDiscardNoInfo;
			}
		}
		return 0;
	}

	private int pointsForAction(Action action) {
		if (action.getActionType().equals("D"))
		{
			return actionPointsDiscard;
		}
		else if (action.getActionType().equals("H"))
		{
			return actionPointsHint;
		}
		else if (action.getActionType().equals("P"))
		{
			return actionPointsPlay;
		}
		return 0;
	}
	
	private int scoreBoardRows(Board board)
	{
		int[] scorePool = board.getFireworkStacks();
		int sum = 0;
		int minValue = Arrays.stream(scorePool).min().getAsInt();
		int maxValue = Arrays.stream(scorePool).max().getAsInt();
		int cliff = maxValue-minValue;
		if (cliff > 0)
		{
			sum += pointForCliff/cliff;

		}
		return sum;
	}
	/**
	 * Take a hand and count the points for it.
	 * @author s164166
	 * @param hand
	 * @param scorePool
	 * @return
	 */
	private int countPointsForSuitAndValue(ArrayList<Card> hand, int[] scorePool, int origTurn, Board board)
	{
		int sum = 0;
		for (int i = 0; i < hand.size(); i++)
		{
			Card card = hand.get(i);
			if (card.isSuitRevealed() && card.isValueRevealed())
			{
				sum += pointsForFullInformation;
			}
			if (card.isSuitRevealed())
			{
				sum += pointsForInformation;
			}
			if (card.isValueRevealed())
			{
				if (checkPossiblePlay(scorePool, card))
				{
					sum += pointsForLeftMostPlayable * (hand.size() - i);
					sum += (6-card.getCardValue())*pointsForLowValue;
				}
				else
				{
					sum += pointsForInformation;

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
