package dtu.AI;

import ai_actions.MoveWrapper;

public interface MoveGenerator
{
	public void generateMoves(MoveWrapper wrapper, int maxDepth, int currPlayer, int origPlayer, Predictor predictor);

}
