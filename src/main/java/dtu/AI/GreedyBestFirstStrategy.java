package dtu.AI;
import dtu.hanabi_ai_game.*;
import java.util.ArrayList;

import dtu.hanabi_ai_game.Board;
import dtu.hanabi_ai_game.Card;
public class GreedyBestFirstStrategy implements Strategy {
	private Board gameState;
	private int player;
	public GreedyBestFirstStrategy(Board gameState,int player){
		this.gameState = gameState;
		this.player = player;
	}
	
	public Action search(int depth) {
		// TODO Auto-generated method stub
		AIBoardPerception vision = new AIBoardPerception(gameState,player,player);
		vision.calculateBoardCertainty();
		System.out.println(vision.getStateScore());
		
		return new Action(GameAction.PLAY, 1);
	}
	
	private void buildStateTree(int perspective,int depth) {
		AIBoardPerception vision = new AIBoardPerception(gameState,player,player);
		StateTree tree = new StateTree(null, gameState);
		ArrayList<Action> vaildActions = vision.getVaildMovesFromState(tree.getState());
		
		
		
		
	}
	


static void combinationUtil(int arr[], int data[], int start, int end, int index, int r) 
{ 
// Current combination is ready to be printed, print it 
if (index == r) 
{ 
for (int j=0; j<r; j++) 
System.out.print(data[j]+" "); 
System.out.println(""); 
return; 
} 

// replace index with all possible elements. The condition 
// "end-i+1 >= r-index" makes sure that including one element 
// at index will make a combination with remaining elements 
// at remaining positions 
for (int i=start; i<=end && end-i+1 >= r-index; i++) 
{ 
data[index] = arr[i]; 
combinationUtil(arr, data, i+1, end, index+1, r); 
} 
} 

// The main function that prints all combinations of size r 
// in arr[] of size n. This function mainly uses combinationUtil() 
static void printCombination(int arr[], int n, int r) 
{ 
// A temporary array to store all combination one by one 
int data[]=new int[r]; 

// Print all combination using temprary array 'data[]' 
combinationUtil(arr, data, 0, n-1, 0, r); 
} 
}
