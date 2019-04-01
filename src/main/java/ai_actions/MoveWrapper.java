package ai_actions;

import java.util.ArrayList;

import dtu.hanabi_ai_game.Board;

/**
 * A wrapper class for each node.
 * Essentially each nod eis contained within here and points can be grabbed from it.
 * @author s164166
 *
 */
public class MoveWrapper
{
	private Action requiredAction;
	private Board expectedBoard;
	private int baseScore = 0;
	private int fullScoreModifier = 0;
	private ArrayList<MoveWrapper> possibleMoves = new ArrayList<MoveWrapper>();
	
	public MoveWrapper(Action requiredAction, Board expectedBoard)
	{
		this.requiredAction = requiredAction;
		this.expectedBoard = expectedBoard;
	}
	
	public int getBaseScore()
	{
		return baseScore;
	}
	
	public int getFullScore()
	{
		return baseScore+fullScoreModifier;
	}
	public void AddToFullScore(int value)
	{
		fullScoreModifier += value;
	}
	
	public void setBaseScore(int value)
	{
		this.baseScore = value;
	}
	
	public Board getBoard()
	{
		return expectedBoard;
	}
	
	public Action getAction()
	{
		return requiredAction;
	}
	
	public ArrayList<MoveWrapper> getPossibleMoves()
	{
		return possibleMoves;
		
	}
	
	public void addMove(MoveWrapper newMove)
	{
		possibleMoves.add(newMove);
	}
	
}
