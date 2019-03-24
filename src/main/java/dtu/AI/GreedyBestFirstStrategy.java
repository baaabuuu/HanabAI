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
public class GreedyBestFirstStrategy implements Strategy {
	private Board gameState;
	private int id;
	private int playerCount;
	private Predictor predictor = new PredictorSimple();
	private MoveGenerator generator = new MoveGeneratorSimple();
	
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
		generator.generateMoves(wrapper, depth, id, id, predictor);
		return "IMPLEMENT ME";
	}
	
	
	
	
	
	
}
