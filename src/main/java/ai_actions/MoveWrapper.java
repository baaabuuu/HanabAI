package ai_actions;

import java.util.ArrayList;
import java.util.HashMap;
import dtu.hanabi_ai_game.Board;
import log.Log;

public class MoveWrapper
{
	private int indexOfBestChild=0;
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
	public int getIndexofBestChild() {
		return indexOfBestChild;
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
	//Only works after a score has been assigned
	public String getActionToHighestScore() {
	
		HashMap<String, Integer> scoreSum = new HashMap<String, Integer>();
		HashMap<String, Integer> scoreCount = new HashMap<String, Integer>();

		for(MoveWrapper w : possibleMoves) {
			
			String key = w.getAction().play();
			scoreSum.put(key,scoreSum.getOrDefault(key,0)+w.getBaseScore());
			scoreCount.put(key,scoreCount.getOrDefault(key,0)+1);
		
		}
		String bestAction="";
		double bestScore=0.0;
		for(String key: scoreSum.keySet()) {
			double currentAvg = scoreSum.get(key)/scoreCount.get(key);
			Log.log("Current Action: "+ key + " has score "+currentAvg+ "("+ scoreSum.get(key)+"/"+scoreCount.get(key)+")");
			if(currentAvg > bestScore) {
				bestScore = currentAvg;
				bestAction = key;
			}
			
			
			
		}
	
		return bestAction;
	}

	public void setActionToHighestScore() {
		// TODO Auto-generated method stub
		MoveWrapper best = null;
		int i =0;
		for(MoveWrapper w : possibleMoves) {
			if(best == null) {
				best = w;
			}else {
				if(w.getBaseScore() > best.getBaseScore()) {
					indexOfBestChild =i;
					best = w;
				};
			}
			
		i++;
		}
		
	}
	
}
