package dtu.AI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
public class DFSTakeNBestNodes implements Strategy
{
	private Board gameState;
	private int id;
	private int playerCount;
	private Predictor predictor = new PredictorSimple();
	private MoveGenerator generator = new MoveGeneratorSimple();
	private BoardScorer scorer = new BoardScorerSimple();

	public DFSTakeNBestNodes(Board gameState, int id, int playerCount)
	{
		this.gameState = gameState;
		this.id = id;
		this.playerCount = playerCount;
	}
	
	public String search(int depth)
	{
		Log.important("Starting a search for DFS AI " + id);
		MoveWrapper wrapper = new MoveWrapper(null, gameState.copyState());
		return findBestPossiblePlay(wrapper, 0, depth, id).play();
	}
	
	
	private Action findBestPossiblePlay(MoveWrapper wrapper, int currDepth, int maxDepth, int currPlayer)
	{
		generator.generateMoves(wrapper, maxDepth, currPlayer, id, predictor);
		Log.important("Generated the following moves: ");
		Log.log(moveWrapperPossibleMovesToString(wrapper));
		
		
		
		ArrayList<MoveWrapper> possibleMoves = wrapper.getPossibleMoves();
		scoreEachWrapper(possibleMoves, currDepth, maxDepth);
		
		int futureNodes = 5;		
		int maxPossibleMoves = (possibleMoves.size() > futureNodes) ? futureNodes : possibleMoves.size() - 1;
		
		ArrayList<MoveWrapper> bestMoves = getNbestBaseMoves(possibleMoves, maxPossibleMoves);
		int nextPlayer = (currPlayer + 1 == playerCount) ? 0 : currPlayer+1;

		for (MoveWrapper move : bestMoves)
		{
			move.AddToFullScore(scorePossibleFutureMoves(move, currDepth + 1, maxDepth, nextPlayer));;
		}
		
		if (Log.debug)
		{
			for (int i = 0; i < bestMoves.size(); i++)
			{
				Log.important("Move " + i  );
				Log.log("Action:  " + bestMoves.get(i).getAction().play());
				Log.log("Base Score:  " + bestMoves.get(i).getBaseScore());
				Log.log("Full Score:  " + bestMoves.get(i).getFullScore());
			}
		}
		return getNbestFullMoves(bestMoves, 1).get(0).getAction();
	}
		
	private int scorePossibleFutureMoves(MoveWrapper wrapper, int currDepth, int maxDepth, int currPlayer)
	{
		generator.generateMoves(wrapper, maxDepth, currPlayer, id, predictor);
		ArrayList<MoveWrapper> possibleMoves = wrapper.getPossibleMoves();
		scoreEachWrapper(possibleMoves, currDepth, maxDepth);
		
		int futureNodes = 5;		
		int maxPossibleMoves = (possibleMoves.size() > futureNodes) ? futureNodes : possibleMoves.size() - 1;
		
		ArrayList<MoveWrapper> bestMoves = getNbestBaseMoves(possibleMoves, maxPossibleMoves);

		
		
		
		if (currDepth == maxDepth)
		{
			if (bestMoves.size() == 0)
				return 0;
			return bestMoves.get(0).getBaseScore();
		}
		else
		{
			int nextPlayer = (currPlayer + 1 == playerCount) ? 0 : currPlayer+1;
			for (MoveWrapper move : bestMoves)
			{
				move.AddToFullScore(scorePossibleFutureMoves(move, currDepth + 1, maxDepth, nextPlayer));;
			}
			if (bestMoves.size() > 0)
				return getNbestFullMoves(bestMoves, 1).get(0).getFullScore();
			return 0;
		}		
	}
	
	
	
	
	private ArrayList<MoveWrapper> getNbestFullMoves(ArrayList<MoveWrapper> moves, int max)
	{		
		ArrayList<MoveWrapper> bestMoves = new ArrayList<MoveWrapper>();
		for(int i = 0; i < max; i++)
		{
			bestMoves.add(null);
		}
		
		for (int i = 0; i < moves.size(); i++)
		{
			MoveWrapper wrapperIterator = moves.get(i);
			for (int j = 0; j < max; j++)
			{
				if (bestMoves.get(j) == null)
				{
					bestMoves.add(j, wrapperIterator);
					bestMoves.remove(bestMoves.size() - 1);

					break;
				}
				else if (wrapperIterator.getFullScore() > bestMoves.get(j).getFullScore())
				{
					bestMoves.add(j, wrapperIterator);
					break;
				}
			}
		}
		return bestMoves;
	}
	
	private ArrayList<MoveWrapper> getNbestBaseMoves(ArrayList<MoveWrapper> moves, int max)
	{		
		ArrayList<MoveWrapper> bestMoves = new ArrayList<MoveWrapper>();
		for(int i = 0; i < max; i++)
		{
			bestMoves.add(null);
		}
		
		for (int i = 0; i < moves.size(); i++)
		{
			MoveWrapper wrapperIterator = moves.get(i);
			for (int j = 0; j < max; j++)
			{
				if (bestMoves.get(j) == null)
				{
					bestMoves.add(j, wrapperIterator);
					bestMoves.remove(bestMoves.size() - 1);
					break;
				}
				else if (wrapperIterator.getBaseScore() > bestMoves.get(j).getBaseScore())
				{
					bestMoves.add(j, wrapperIterator);
					bestMoves.remove(bestMoves.size() - 1);
					break;
				}
			}
		}
		return bestMoves;
	}
	
	void scoreEachWrapper(ArrayList<MoveWrapper> moves, int currDepth, int maxDepth)
	{		
		for (int i = 0; i < moves.size(); i++)
		{
			MoveWrapper wrapper = moves.get(i);
			wrapper.setBaseScore(scorer.getBoardScore(wrapper.getBoard(), id, wrapper.getAction(), currDepth, maxDepth));
		}
	}
	
	
	private String moveWrapperPossibleMovesToString(MoveWrapper wrapper)
	{
		ArrayList<MoveWrapper> wraps = wrapper.getPossibleMoves();
		StringBuilder sb = new StringBuilder();
		for (MoveWrapper wrap : wraps)
		{
			sb.append(wrap.getAction().play() + ", ");
		}
		sb.setLength(sb.length() - 2);
		return sb.toString();
	}
	
	
	
}
