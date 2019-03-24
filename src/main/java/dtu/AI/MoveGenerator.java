package dtu.AI;

public interface MoveGenerator
{
	public void generateMoves(MoveWrapper wrapper, int maxDepth, int currPlayer, int origPlayer, Predictor predictor);

}
