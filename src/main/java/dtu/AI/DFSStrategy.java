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
public class DFSStrategy implements Strategy {
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
		Log.important("Starting a searchin for Patrick&Christian AI");
		MoveWrapper wrapper = new MoveWrapper(null, gameState.copyState());
		return getBestMove(wrapper, 0, depth, id).play();
	}
	
	
	public Action getBestMove(MoveWrapper wrapper, int currDepth, int maxDepth, int currPlayer)
	{
		generator.generateMoves(wrapper, maxDepth, currPlayer, id, predictor);
		ArrayList<MoveWrapper> possibleMoves = wrapper.getPossibleMoves();
		ArrayList<Integer> scores = new ArrayList<Integer>();
		Log.important("Considering the following moves:  " );

		for (int i = 0; i < possibleMoves.size(); i++)
		{

			int nextPlayer = (currPlayer + 1 == playerCount) ? 0 : currPlayer+1;
			int points = scoreFutureMoves(possibleMoves.get(i), currDepth+1, maxDepth, nextPlayer);
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
		Log.important("First score found is:  " + currScore);
		Log.log("And the action is " + bestAction.play());
		for (int i = 1; i < scores.size(); i++)
		{
			Log.important("Comparing " + currScore + ">" + scores.get(i));

			Log.important("Better score found is:  " + currScore);

			if (scores.get(i) > currScore)
			{
				Log.important("TRUE");
				Log.log("And the action is " + bestAction.play());

				currScore = scores.get(i);
				bestAction = possibleMoves.get(i).getAction();
			}
		}
		
		return bestAction;
	}
	
	
	public int scoreFutureMoves(MoveWrapper wrapper, int currDepth, int maxDepth, int currPlayer)
	{
		if (currDepth == maxDepth) //No further - start grading
		{
			return scorer.getBoardScore(wrapper.getBoard(), id);
		}
		generator.generateMoves(wrapper, maxDepth, currPlayer, id, predictor);
		ArrayList<MoveWrapper> possibleMoves = wrapper.getPossibleMoves();
		if (possibleMoves.size() == 0)
		{
			return scorer.getBoardScore(wrapper.getBoard(), id);
		}
		ArrayList<Integer> scores = new ArrayList<Integer>();
		for (int i = 0; i < possibleMoves.size(); i++)
		{
			int nextPlayer = (currPlayer + 1 == playerCount) ? 0 : currPlayer+1;
			int points = scoreFutureMoves(possibleMoves.get(i), currDepth+1, maxDepth, nextPlayer);
			scores.add(points);
		}
		return Collections.max(scores);
	}
	
	
	
	
	
	
}
