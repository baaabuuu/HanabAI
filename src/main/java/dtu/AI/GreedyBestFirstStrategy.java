package dtu.AI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import ai_actions.Action;
import ai_actions.ActionHint;
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
	
	
	public void generateMoves(MoveWrapper wrapper, int currDepth, int maxDepth, int currPlayer)
	{
		Board board = wrapper.getBoard();
		ArrayList<ArrayList<Card>> playerHands = new ArrayList<ArrayList<Card>>();
		addPlayerHands(playerHands, board);
		int scorePool[] = board.getFireworkStacks();
		HashMap<String, Integer> freqMap = new HashMap<String, Integer>();
		for(int i = 0; i < playerCount; i++)
		{
			if (i != currPlayer)
			{
				for (Card card : playerHands.get(i))
				{
					if (!card.isSuitRevealed())
					{
						String possibleMove = new ActionHint(i, card.getCardSuit().getSuitChar()).play();
						if (freqMap.containsKey(possibleMove))
						{
							freqMap.put(possibleMove, freqMap.get(possibleMove)+1);
						}
						else
						{
							freqMap.put(possibleMove, 1);
						}
					}
					if (!card.isValueRevealed())
					{
						String possibleMove = new ActionHint(i, String.valueOf(card.getCardValue())).play();
						if (freqMap.containsKey(possibleMove))
						{
							freqMap.put(possibleMove, freqMap.get(possibleMove)+1);
						}
						else
						{
							freqMap.put(possibleMove, 1);
						}
					}
				}
			}
			else
			{
				if (board.getClueTokens() < 8)
				{
					//Identify possible discard targets
					//Oldest Card
					int discardTarget = -1;
					//Card with value less
					Card cardConsider;
					Card otherCard;
	
					for (int j = 0; j < playerHands.get(i).size(); j++)
					{
						cardConsider = playerHands.get(i).get(j);
						//Check if value is possible to play
						for (int k = 0; k < 5; k++)
						{
							if (!cardConsider.isSuitRevealed() && cardConsider.isValueRevealed() && cardConsider.getCardValue() < scorePool[k])
							{
								discardTarget = j;
								break;
							}
						}
						//Check if suit is playable
						if (discardTarget == -1)
						{
							if (cardConsider.isSuitRevealed() && !checkIfSuitIsPlayable(cardConsider.getCardSuit(), scorePool, board))
							{
								discardTarget = j;
								break;
							}
						}
					}
					
					
					if (discardTarget == -1)
					{
						//check duplicate
						for (int j = 0; j < playerHands.get(i).size(); j++)
						{
							cardConsider = playerHands.get(i).get(j);
							for (int k = j; k < playerHands.get(i).size() - 1; k++)
							{
								otherCard = playerHands.get(i).get(k);
								if (cardConsider.isSuitRevealed() && cardConsider.isValueRevealed() &&
										otherCard.isSuitRevealed() && otherCard.isValueRevealed() &&
										cardConsider.getCardValue() == otherCard.getCardValue() &&
										cardConsider.getCardSuit().equals(otherCard.getCardSuit()))
								{
									discardTarget = j;
									break;
								}		
							}
						}
					}
					
					if (discardTarget == -1)
					{
						for (int j = 0; j < playerHands.get(i).size(); j++)
						{
							cardConsider = playerHands.get(i).get(j);
							if (!cardConsider.isValueRevealed() && !cardConsider.isSuitRevealed())
							{
								discardTarget = j;
								break;
							}
						}
					}
					if (discardTarget == -1)
					{
						for (int j = 0; j < playerHands.get(i).size(); j++)
						{
							cardConsider = playerHands.get(i).get(j);
							if (!cardConsider.isValueRevealed())
							{
								discardTarget = j;
								break;
							}
						}
					}
					if (discardTarget == -1)
					{
						for (int j = 0; j < playerHands.get(i).size(); j++)
						{
							cardConsider = playerHands.get(i).get(j);
							if (!cardConsider.isSuitRevealed())
							{
								discardTarget = j;
								break;
							}
						}
					}
					//Consider the card that is hardest to play
					if (discardTarget == -1)
					{
						Log.important("Oh lord, this is an extreme edge case");
						discardTarget = 0;
					}
					
				}

			}
		}
		
		
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
