package dtu.AI;

import java.util.ArrayList;

import ai_actions.Action;
import dtu.hanabi_ai_game.Board;

public class MoveWrapper
{
	private Action requiredAction;
	private Board expectedBoard;	
	private ArrayList<MoveWrapper> possibleMoves = new ArrayList<MoveWrapper>();
	
	public MoveWrapper(Action requiredAction, Board expectedBoard)
	{
		this.requiredAction = requiredAction;
		this.expectedBoard = expectedBoard;
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
