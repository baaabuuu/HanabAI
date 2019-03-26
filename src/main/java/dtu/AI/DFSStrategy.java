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
import ai_actions.MoveWrapper;
import dtu.hanabi_ai_game.Board;
import dtu.hanabi_ai_game.Card;
import dtu.hanabi_ai_game.SuitEnum;
import log.Log;
public class DFSStrategy implements Strategy
{
	private Board gameState;
	private int id;
	private int playerCount;
	private Predictor predictor = new PredictorSimple();
	private MoveGenerator generator = new MoveGeneratorSimple();
	private BoardScorer scorer = new BoardScorerSimple();

	public DFSStrategy(Board gameState, int id, int playerCount)
	{
		this.gameState = gameState;
		this.id = id;
		this.playerCount = playerCount;
	}
	
	public String search(int depth)
	{
		Log.important("Starting a search for DFS AI " + id);
		MoveWrapper wrapper = new MoveWrapper(null, gameState.copyState());
		return getBestMove(wrapper, 0, depth, id).play();
	}
	
	private Action getBestMove(MoveWrapper wrapper, int currDepth, int maxDepth, int currPlayer)
	{
		
		generator.generateMoves(wrapper, maxDepth, currPlayer, id, predictor);
		Log.important("Generated the following moves: ");
		Log.log(moveWrapperPossibleMovesToString(wrapper));

		ArrayList<MoveWrapper> possibleMoves = wrapper.getPossibleMoves();
		ArrayList<Integer> scores = new ArrayList<Integer>();
		
		int nextPlayer = (currPlayer + 1 == playerCount) ? 0 : currPlayer+1;
		for (int i = 0; i < possibleMoves.size(); i++)
		{

			int points = scoreFutureMoves(possibleMoves.get(i), currDepth+1, maxDepth, nextPlayer, possibleMoves.get(i).getAction());
			scores.add(points);

		}
		
		if (Log.debug)
		{
			for (int i = 0; i < possibleMoves.size(); i++)
			{
				Log.important("Move " + i  );
				Log.log("Action:  " + possibleMoves.get(i).getAction().play() );
				Log.log("Score:  " + scores.get(i));
	
			}
		}
		Action bestAction = possibleMoves.get(0).getAction();
		int currScore = scores.get(0);
		for (int i = 1; i < scores.size(); i++)
		{
			if (scores.get(i) > currScore)
			{
				currScore = scores.get(i);
				bestAction = possibleMoves.get(i).getAction();
			}
		}
		
		return bestAction;
	}
	
	
	private int scoreFutureMoves(MoveWrapper wrapper, int currDepth, int maxDepth, int currPlayer, Action baseAction)
	{
		if (currDepth == maxDepth) //No further - start grading
		{
			return scorer.getBoardScore(wrapper.getBoard(), id, baseAction, currDepth, maxDepth) ;
		}
		generator.generateMoves(wrapper, maxDepth, currPlayer, id, predictor);
		ArrayList<MoveWrapper> possibleMoves = wrapper.getPossibleMoves();
		if (possibleMoves.size() == 0)
		{
			return scorer.getBoardScore(wrapper.getBoard(), id, wrapper.getAction(), currDepth, maxDepth);
		}
		ArrayList<Integer> scores = new ArrayList<Integer>();
		int nextPlayer = (currPlayer + 1 == playerCount) ? 0 : currPlayer+1;
		
		for (int i = 0; i < possibleMoves.size(); i++)
		{
			int points = scoreFutureMoves(possibleMoves.get(i), currDepth+1, maxDepth, nextPlayer, baseAction);
			scores.add(points);
		}
		return Collections.max(scores)+scorer.getBoardScore(wrapper.getBoard(), id, baseAction, currDepth, maxDepth);
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
