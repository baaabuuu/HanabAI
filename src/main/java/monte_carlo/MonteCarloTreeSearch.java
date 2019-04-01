package monte_carlo;

import java.util.ArrayList;
import java.util.List;

import dtu.hanabi_ai_game.Board;
import dtu.hanabi_ai_game.Game;
import log.Log;

public class MonteCarloTreeSearch {

	static final long MAX_TIME_MILLIS = 10000;
	int level;
	long end;
	
	
	public String findNextMove(Board board, int playerNo, Game game, int noOfGames, int maxTimeMILLIS, int noOfPlayers) {
        // define an end time which will act as a terminating condition
		if(maxTimeMILLIS == 0){
			maxTimeMILLIS = Integer.MAX_VALUE;
		}
		end = System.currentTimeMillis() + maxTimeMILLIS;
		State rootState = new State(game, playerNo, noOfPlayers);
		List<State> tempList = new ArrayList<>();
		tempList.add(rootState);
		Node rootNode = new Node(tempList, playerNo);
        Tree tree = new Tree(rootNode);
 
 
        //Goes for a no of games or a time
        for(int i = 0; i < noOfGames && System.currentTimeMillis() < end; i++) {
            Node promisingNode = selectPromisingNode(rootNode);
            
            //Checks if the game has ended. Returns -1 or the game score
            if (promisingNode.getState().getGame().isGameOver() == -1) {
                expandNode(promisingNode);
            }
            Node nodeToExplore = promisingNode;
            if (promisingNode.getChildArray().size() > 0) {
                nodeToExplore = promisingNode.getRandomChildNode();
            }
            int playoutResult = simulateRandomPlayout(nodeToExplore);
            backPropogation(nodeToExplore, playoutResult);
            
            
        }
        
        if(rootNode.getChildArray().isEmpty()){
        	expandNode(rootNode);
        }
        Node winnerNode = rootNode.getChildWithMaxScore();
        //printTree(rootNode);
        tree.setRoot(winnerNode);
        return winnerNode.getState().getMovePeformed();
        
        
    }

	private void printTree(Node rootNode) {
		// TODO Auto-generated method stub
		for(Node node1: rootNode.getChildArray()){
    		System.out.println("Action peformed: " + node1.getState().getMovePeformed() + ". Score: " + node1.getAvgState().getWinScore() + ". Number of times visited: "+ (node1.getAvgState().getVisitCount()) );
    	}
		
		
		
	}

	private void expandNode(Node node) {
		int stateIndex = (int) ((node.getChildArray().size()-1)*Math.random());
		List<List<State>> possibleStates = node.getState(stateIndex).getAllPossibleStates();
	    possibleStates.forEach(state -> {
	        Node newNode = new Node(state, node.getState(stateIndex).nextOpponent());
	        newNode.setParent(node);
	        node.getChildArray(stateIndex).add(newNode);
	    });
		
	}

	private Node selectPromisingNode(Node rootNode) {
		Node node = rootNode;
		while (node.getChildArray().size() != 0) {
			node = UCT.findBestNodeWithUCT(node);
			}
		return node;
	}
	
	private void backPropogation(Node nodeToExplore, int playoutResult) {
	    Node tempNode = nodeToExplore;
	    while (tempNode != null) {
	        tempNode.getAvgState().incrementVisit();
	        tempNode.getAvgState().addScore(playoutResult);   
	        tempNode = tempNode.getParent();
	    }
	    
	}
	
	private int simulateRandomPlayout(Node node) {
	    Node tempNode = new Node(node);
	    State tempState = tempNode.getState();
	    int boardStatus = tempState.getGame().isGameOver();
	    tempState.getGame().randomizeHand(tempState.getPlayerNo());
	    while (boardStatus == -1) { //-1 means game running
	        tempState.togglePlayer();
	        tempState.randomPlay();
	        boardStatus = tempState.getGame().isGameOver();
	    }
	    return boardStatus;
	}
	
}
