package dtu.AI;

import java.util.ArrayList;

import ai_actions.Action;
import dtu.AI.StateTree.BeliefState;
import dtu.AI.StateTree.DebugHelper;
import dtu.AI.StateTree.PlayHelper;
import dtu.AI.StateTree.ScoreHelper;
import dtu.AI.StateTree.State;
import dtu.AI.StateTree.StateTree;
import dtu.AI.StateTree.TreeHelper;
import dtu.hanabi_ai_game.Board;
import log.Log;
/**
 * File Author: Emil Bejder (s164161)
 * 
 * */
public class BeleifStrategy implements Strategy {
	private double bestScore = 0.0;
	private StateTree bestLeaf = null;
	private Action bestAction = null;
	private Board rootBoard;
	private int rootPlayer;
	private int players;
	public BeleifStrategy(int rootPlayer, Board b) {
		this.rootBoard = b;
		this.rootPlayer = rootPlayer;
		this.players = b.getPlayerHands().size();
	}

	@Override
	//Ye be warned, only use depth 2
	public String search(int depth) {
		// TODO Auto-generated method stub
		//int trueDepth = depth+1;
		bestScore = 0.0;
		 bestLeaf = null;
		
		StateTree gameTree = buildTreeOfDepth(depth);
		
		//DebugHelper.printAllDebugInfo(rootBoard);
		//DebugHelper.printStateTree(gameTree,0);
		StateTree bestLeaf = calculateBestProperLeaf(gameTree);
	
		//Log.log("Best score in leaf: "+bestLeaf.getScore() + "with score modifier: "+bestLeaf.getScoreWeight());
		//DebugHelper.printStateTree(bestLeaf);
		
		StateTree best = getBestMove(bestLeaf,null);
		//DebugHelper.printStateTree(best, 0);
		return best.getAction().play();
	}
	
	
	
	public StateTree buildTreeOfDepth(int maxDepth) {
		
		 StateTree rootNode = new StateTree(null, null, rootBoard, rootPlayer);
		 
		 
		return buildProperTree(rootNode,maxDepth);
	}
	
 
	
	private StateTree buildProperTree(StateTree s, int maxDepth) {
		
		if(s.getDepth() == maxDepth) {
			s.calculateScore();
			return s;
		}
		int currentPlayer = (rootPlayer+s.getDepth())%players;
		
		if((s instanceof BeliefState)) {
			s.calculateScore(); //Implictly calculcates childscores to find own score
			for(StateTree child : s.getChildern()) {
				child.setScoreWeight(s.getBeliefWeight()*s.getFlatScoreWeight());
				buildProperTree(child,maxDepth);
				
			}
			
		} else {
			//If parrent was a beliefState then we already have a score
		    if(!(s.getRoot() instanceof BeliefState))	{
		    	
		    	s.calculateScore();
		    	if(s.getRoot() != null) {
		    	s.setScoreWeight(s.getRoot().getFlatScoreWeight());
		    	}
		    }
		
		ArrayList<Action> possibleActions = PlayHelper.getPossibleActions(s.getGameState().copyState(), currentPlayer);
		
		for(Action a: possibleActions) {
			//--------------------
			
			
			ArrayList<Board> resultStates = TreeHelper.getBoardStatesFromAction(a, s.getGameState().copyState(), rootPlayer, s.getDepth());
			
			// If we have more than one possible move we make a belief state
			if(resultStates.size()> 1) {
				
				//BeliefStates have the same boardState as root.
				
				BeliefState bState = new BeliefState(s, a, s.getGameState().copyState(), s.getRootPlayer());
				for(Board b: resultStates) {
					bState.addChild(a,b);
				}
				
				s.addChild(buildProperTree(bState,maxDepth));
				
				//remember that constructor automatically increments depth
				
			} else {
				//Be aware that we assume the resulting States to always have 1 or more.
				//Add normal state
				
				StateTree newState = new State(s, a, resultStates.get(0), s.getRootPlayer());
				
				
				s.addChild(buildProperTree(newState,maxDepth));
				
			}
		
			
			//--------------------
			
		}
		
		
		
		}
		
		return s;
	}	
	
	
	
	
	private StateTree calculateBestProperLeaf(StateTree s) {
		
		
		if(s.getChildern().size() == 0) {
		
			return s;
		}
		//double bestScore =  Double.NEGATIVE_INFINITY;
		double bestScore =  0.0;
		
		StateTree bestLeaf = null;
		
		
		
		
		for(StateTree child : s.getChildern()) {
			double score = 0;
			StateTree currentLeaf = calculateBestProperLeaf(child);
			//if(bestLeaf == null) bestLeaf = currentLeaf;

			if(child.getRoot() instanceof BeliefState) {
				score = currentLeaf.getRoot().getScore()*currentLeaf.getRoot().getScoreWeight();
			}else {
				score = currentLeaf.getScore()*currentLeaf.getScoreWeight();
			}
			
				
			
			
	
			if(score>bestScore) {
				bestLeaf = currentLeaf;
				bestScore = score;
				
			}
			
		}
		
		return bestLeaf;
	}
	

	private StateTree getBestMove(StateTree bestLeaf,StateTree child) {
		
		//if the root above is the final root, we are at the correct action
		
		if((bestLeaf.getRoot() == null)) {
			
		//	Log.important("Best move was: "+child.getAction().play()+" with score: "+child.getScore()+" and "+child.getScoreWeight());
		//	DebugHelper.printScoreObject(child);
			for(StateTree c : bestLeaf.getChildern()) {
		//	Log.log("Move: "+c.getAction().play()+" with score: ");
		//	DebugHelper.printScoreObject(c);
				}
	//		DebugHelper.printAllDebugInfo(bestLeaf);
	//		DebugHelper.printAllDebugInfo(child);
		//	DebugHelper.printChildren(bestLeaf);
	
			return child;
		}

		//Log.important("Predicted gameState at depth"+bestLeaf.getDepth());
	//	Log.log("Action was: "+bestLeaf.getAction().play());
	//	DebugHelper.printAllDebugInfo(bestLeaf);
		//Log.log("Move: "+bestLeaf.getAction().play()+" with score: "+bestLeaf.getScore()+" and "+bestLeaf.getScoreWeight());
		//DebugHelper.printScoreObject(bestLeaf);
		return getBestMove(bestLeaf.getRoot(),bestLeaf);
		
	}
}
