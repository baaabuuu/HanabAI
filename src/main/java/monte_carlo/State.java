package monte_carlo;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import dtu.hanabi_ai_game.*;

public class State {

	Game game;
	int playerNo;
	int noOfPlayers;
	int visitCount = 0;
	double winScore = 0.0;
	String movePeformed = "";
	int nodeIndex;
	
	
	public int getNodeIndex() {
		return nodeIndex;
	}

	public void setNodeIndex(int nodeIndex) {
		this.nodeIndex = nodeIndex;
	}

	public String getMovePeformed() {
		return movePeformed;
	}

	public void setMovePeformed(String movePeformed) {
		this.movePeformed = movePeformed;
	}

	public State(Game game, int playerNo2, int noOfPlayers) {
		this.game = new Game(game);
		this.playerNo = playerNo2;
		this.noOfPlayers = noOfPlayers;
	}

	public State(State state) {
		this.game = new Game(state.getGame());
		this.playerNo = state.getPlayerNo();
		this.noOfPlayers = state.getNoOfPlayers();
	}

	public double getWinScore() {
		return winScore;
	}

	public void setWinScore(double winScore) {
		this.winScore = winScore;
	}

	public int getVisitCount() {
		return visitCount;
	}

	public void setVisitCount(int visitCount) {
		this.visitCount = visitCount;
	}

	public int getPlayerNo() {
		return playerNo;
	}

	public void setPlayerNo(int playerNo) {
		this.playerNo = playerNo;
	}

	
	public List<List<State>> getAllPossibleStates(){
		//Constructs a list of all possible states from current state
		List<String> possibleMoves = getAllPossibleMoves();
		List<List<State>> possibleStates = new ArrayList<>();
		
		for(int i = 0; i < possibleMoves.size(); i++){
			
			if(possibleMoves.get(i).charAt(0) == 'H'){//If it is a hint peform the action
				State tempState = new State( this.game, this.playerNo, this.noOfPlayers);
				List<State> tempList = new ArrayList<>();
				tempState.setMovePeformed(possibleMoves.get(i));
				tempState.getGame().rolloutTakeAction(possibleMoves.get(i));
				
				tempList.add(tempState);
				possibleStates.add(tempList);
			} else {
				//If it is to discard or play, find all possible outcomes and 
				List<State> tempList = new ArrayList<>();
	
				for(Game tempGame: game.getAllPermutationsOfCard(playerNo, Character.getNumericValue(possibleMoves.get(i).charAt(1)))){
					State tempState = new State( tempGame, this.playerNo, this.noOfPlayers);	
					tempState.setMovePeformed(possibleMoves.get(i));
					tempState.getGame().rolloutTakeAction(possibleMoves.get(i));
					
					tempList.add(tempState);
					
				}
				possibleStates.add(tempList);
				
			}
			
		}
		
		
		return possibleStates;
	}
	
	
	
	private List<String> getAllPossibleMoves() {
		List<String> possibleMoves = new ArrayList<>();
		//Add play state
		possibleMoves.add(playLogic());
				
		//If there aren't full clues, add discard state
		if(game.getBoard().getClueTokens() < 8){
			possibleMoves.add(discardLogic());
		}
		
		//If there are clues, add hint states
		if(game.getBoard().getClueTokens() > 0){
			possibleMoves.addAll(getHintOptions());
		}
		return possibleMoves;
	}

	private List<String> getHintOptions() {
		List<ArrayList<Card>> hands = game.getBoard().getPlayerHands();
		List<String> moves = new ArrayList<>();
		
		//We go through each other players hand
		for(int i = 0; i < hands.size(); i++){
			if(i != playerNo){ //Makes sure we don't hint to our own hand
				
				//We use treeset to prevent duplicates
				TreeSet<SuitEnum> suits = new TreeSet<SuitEnum>();
				TreeSet<Integer> numbers = new TreeSet<Integer>();
				
				//Goes through all the cards and notes their colors and value
				//Only if they are not revealed
				for(Card card : hands.get(i)){
					if(!card.isSuitRevealed()){
						suits.add(card.getCardSuit());
					}
					if(!card.isValueRevealed()){
						numbers.add(card.getCardValue());
					}
					
				}
				
				//Hints the suit
				for(SuitEnum suit : suits){
					//Hint + player hinted + suit hinted
					moves.add("H" + i + suit.getSuitChar());				
				}
				
				for(Integer value : numbers){
					//Hint + player hinted + value hinted
					moves.add("H" + i + value);					
				}

			}
			
		}
		
		
		return moves;
	}

	private String discardLogic() {
		List<Card> myHand = game.getBoard().getPlayerHand(playerNo);
		
		int cardValue[] = new int[myHand.size()];
		
		for(int i = 0; i < myHand.size()-1 ; i++){
			
			Card card = myHand.get(i);
			if(card.isSuitRevealed() && card.isValueRevealed()){
				//If all information is revealed, and the card is the next to go on the stack play it
				if(game.getBoard().getTopCard(card.getCardSuit().getID()) >= card.getCardValue()){
					cardValue[i] = -1;
				} else {
					cardValue[i] = 2;
				}
				
				
			} else if (card.isSuitRevealed()){
				// If the card is in a color that can be played mark it for play
				if(game.getBoard().getTopCard(card.getCardSuit().getID()) == 5){
					cardValue[i] = -1;
				} else {
					cardValue[i] = 1;
				}

			} else if(card.isValueRevealed() ){
				//If the cards value is revealed, and it is not lower than or equal to all current cards played, mark it to play
				boolean discard = true;
				for(int j = 0; j < 5; j++){
					if(game.getBoard().getTopCard(j) < card.getCardValue()){
						discard = false;
						break;
					}
				}
				if(discard){
					cardValue[i] = -1;
				} else {
					cardValue[i] = 1;
				}
			} else {
				cardValue[i] = 0;
			}
		}
		int cardToDiscard = 0;
		
		for(int i = 0; i < myHand.size()-1 ; i++){
			if(cardValue[cardToDiscard] > cardValue[i]){
				cardToDiscard = i;
			}
				
		}
		
		
		return "D" + (cardToDiscard);
	}

	public String playLogic() {
		
		List<Card> myHand = game.getBoard().getPlayerHand(playerNo);
		
		
		int cardValue[] = new int[myHand.size()];
		for(int i = myHand.size()-1; i >= 0 ; i--){
			
			Card card = myHand.get(i);
			if(card.isSuitRevealed() && card.isValueRevealed()){
				//If all information is revealed, and the card is the next to go on the stack play it
				if(game.getBoard().getTopCard(card.getCardSuit().getID())+1 == card.getCardValue()){
					cardValue[i] = 2;
				} else {
					cardValue[i] = -1;
				}
				
				
			} else if (card.isSuitRevealed()){
				// If the card is in a color that can be played mark it for play
				if(game.getBoard().getTopCard(card.getCardSuit().getID()) == 5){
					cardValue[i] = -1;
				} else {
					cardValue[i] = 1;
				}

			} else if(card.isValueRevealed() ){
				//If the cards value is revealed, and it is not lower than or equal to all current cards played, mark it to play
				int play = 0;
				for(int j = 0; j < 5; j++){
					if(game.getBoard().getTopCard(j)+1 == card.getCardValue()){
						play++;
					}
				}
				if(play == 5){
					cardValue[i] = 2;
				}else if(play>0){
					cardValue[i] = 1;
				} else {
					cardValue[i] = -1;
				}
			} else {
				cardValue[i] = 0;
			}
		}
		int cardToPlay = myHand.size()-1;
		
		for(int i = myHand.size()-1; i >= 0 ; i--){
			if(cardValue[cardToPlay] < cardValue[i]){
				cardToPlay = i;
			}
			
				
		}
		
		//Matches play string Play + CardNo
		//CardNo starts at 1
		return "P" + (cardToPlay);
	}

	
	
	public void randomPlay() {
		List<String> possiblePlays = getAllPossibleMoves();
		int randomPlayNo = (int) Math.random()*possiblePlays.size();
		
		game.rolloutTakeAction(possiblePlays.get(randomPlayNo));
		
	}

	public void incrementVisit() {
		visitCount++;
	}

	public void addScore(double winScore2) {
		winScore = winScore + winScore2;
	}

	public int nextOpponent() {
		return (playerNo+1) % noOfPlayers;
	}

	public void togglePlayer() {
		this.playerNo = nextOpponent();
	}

	public Game getGame() {
		return game;
	}
	public void setGame(Game game) {
		this.game = game;
	}

	public int getNoOfPlayers() {
		return noOfPlayers;
	}

	public void setNoOfPlayers(int noOfPlayers) {
		this.noOfPlayers = noOfPlayers;
	}
	
}
