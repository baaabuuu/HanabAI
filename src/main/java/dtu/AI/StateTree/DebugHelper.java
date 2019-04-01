package dtu.AI.StateTree;

import java.util.ArrayList;

import ai_actions.Action;
import dtu.hanabi_ai_game.Board;
import dtu.hanabi_ai_game.Card;
import log.Log;

public class DebugHelper {
	/**
	 * File Author: Emil Bejder (s164161)
	 * 
	 * */
	
	
	public static void displayDebugPlayerhands(ArrayList<ArrayList<Card>>hands, int players)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		for (int i = 0; i < players; i++)
		{

			sb.append("player: ");
			sb.append((i+1));
			sb.append(" has ");
			for(Card card : hands.get(i))
			{
				sb.append(card.getStringRepresentation());
				sb.append(' ');
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append('\n');
		}
		sb.deleteCharAt(sb.length() - 1);
		Log.log(sb.toString());
	}
	
	public static void getStackPiles(Board board)
	{
		if (!Log.debug)
		{
			System.out.println("Top of each firework stack is:");
			System.out.println("W"+board.getTopCard(0) + " R"+board.getTopCard(1) + " B"+board.getTopCard(2) + " Y"+board.getTopCard(3) + " G"+board.getTopCard(4));
		}
		Log.log("Top of each firework stack is:");
		Log.log("W"+board.getTopCard(0) + " R"+board.getTopCard(1) + " B"+board.getTopCard(2) + " Y"+board.getTopCard(3) + " G"+board.getTopCard(4));

	}
	
	public static void printAllDebugInfo(StateTree s) {
		Board b = s.getGameState();
		ArrayList<ArrayList<Card>> hands = b.getPlayerHands();
		int size = hands.size();
		
		
		displayDebugPlayerhands(hands,size);
		getStackPiles(b);
		if(s instanceof BeliefState) {
			Log.important("Tokens left: "+b.getClueTokens());
		}else {
		Log.log("Tokens left: "+b.getClueTokens());
	}}
	
	
	public static void debugStateTree(StateTree s) {
		
		printActionSequences(s);
	}
	
	public  static void printActionSequences(StateTree s) {
		
		if((s.getRoot() == null)) {
			Log.log("Possible actions in root state: ");
			for(Action a : PlayHelper.getPossibleActions(s.getGameState(), s.getRootPlayer())) {
				Log.log(a.play());
			}
			for(StateTree sc : s.getChildern()) {
				Log.log("Child action: "+sc.getAction().play());
			}
			
			Log.log("State has "+s.getChildern().size()+ " childern");
			Log.log("State is of depth "+s.getDepth() );
			return;
		}
		
		Log.log("Possible actions in  state["+s.getDepth()+"]: ");
		Log.log("State has "+s.getChildern().size()+ " childern");
		for(Action a : PlayHelper.getPossibleActions(s.getGameState(), s.getRootPlayer())) {
			Log.log(a.play());
		}
		printActionSequences(s.getRoot());
		
	}
	
	public static void printChildren(StateTree s) {
		Log.log("Children:");
		for(StateTree sc : s.getChildern()) {
			Log.log("Child action: "+sc.getAction().play());
		}
		
	}
	public static void printStateTree(StateTree s) {
		
		if((s instanceof BeliefState)) {
			System.out.println(indenthelper(s.getDepth())+"(B)["+s.getDepth()+"]["+s.getAction().play()+"]");
		}else {
			String actionString = "No action";
			if(s.getAction()!=null) actionString = s.getAction().play();
			
			System.out.println(indenthelper(s.getDepth())+"(S)["+s.getDepth()+"]["+actionString+"]");
		}
		if(s.getChildern().size() == 0) {
			return;
		}
		
		for(StateTree child : s.getChildern()) {
			printStateTree(child);
			
		}
		
		
		
		
	}
	
	public static void printScoreObject(StateTree s) {
		
		Score temp = s.getScoreObject();
	if(s instanceof BeliefState) {
		Log.log("Belief State Score ["+s.getScoreObject().beliefStateScore+"]");
		Log.log("Depth-Corrected Score: ["+(s.getScoreObject().beliefStateScore)*(s.getScoreObject().getDepth()+2)+"]");
	}else {
		Log.log("CSum: ["+temp.weightedCertaintySum+"] Info: ["+temp.weightedInformationValue+"] BScore: ["+temp.weightedBoardScore+"] Life: ["+temp.weightedLife+"] Max :["+temp.weightedMaxScore+"] FScore: ["+temp.fireworkScore+"] HScore: ["+temp.weightedHandValue+"]");
		//Log.log("Root Score: ["+s.getRoot().getScore()+"]");
		Log.log("This score: ["+temp.getSum()+"]");
		Log.log("Depth-Corrected Score: ["+(temp.getDepthCorrectedSum()+s.getRoot().getScore())+"]");
		//Log.important("Total Score (This+Root): ["+(temp.getSum()+s.getRoot().getScore())+"]");
		
	}
	
		
	}
	public static void printWeightPath(StateTree s) {
		
		Log.important(""+s.getScoreWeight());
		
	}
	
	
public static void printStateTree(StateTree s,int depth) {
		
		if((s instanceof BeliefState)) {
			System.out.println(indenthelper(depth)+"(B)["+s.getDepth()+"]["+s.getAction().play()+"]["+s.getGameState().getClueTokens()+"]");
		}else {
			String actionString = "No action";
			if(s.getAction()!=null) actionString = s.getAction().play();
			
			System.out.println(indenthelper(depth)+"(S)["+s.getDepth()+"]["+actionString+"]["+s.getGameState().getClueTokens()+"]");
		}
		if(s.getChildern().size() == 0) {
			return;
		}
		
		for(StateTree child : s.getChildern()) {
			printStateTree(child,depth+1);
			
		}
		
		
		
		
	}
	
	private static String indenthelper(int depth) {
		String output="";
		for(int i=0;i<depth;i++) {
			output+=" ";
		}
		return output;
	}
	
}
