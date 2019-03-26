package dtu.AI;
import java.util.ArrayList;

import dtu.hanabi_ai_game.*;
public class AIBoardPerception {
	private final int HANDSIZE = 4;
	private	int unkwnHandSize = 4;
	private	int players = 5;
	private	int[][] cardsInPlay = new int[5][5];
	private	int[][] totalCards = new int[5][5];
	private	int cardCount;
	private int AiPlayer;
	private int score;
	private int life;
	private ArrayList<Card> myHand;
	private ArrayList<Card> perspectiveHand;
	private ArrayList<ArrayList<Card>> otherPlayersHand = new ArrayList<ArrayList<Card>>();
	private int perspective;
	public AIBoardPerception(Board state, int AiPlayer,int perspective) {
		this.AiPlayer = AiPlayer;
		score = state.getScore();
		life = state.getLife();
		this.perspective = perspective;
		//Init total cards (Should honestly be a constant somewhere)
		  /***************************************/
		  for(int i =0;i<5;i++) {
			  for(int j =0;j<5;j++) {
				  if(j==0) {
					  totalCards[i][j] = 3;
					  cardCount+=3;
				  }else if(j==4) {
					  totalCards[i][j] = 1;
					  cardCount+=1;
				  }else {
					  totalCards[i][j] = 2;
					  cardCount+=2;
				  }
			  }
		  }
		  
		  //Get player hands
		 /****************************************/
		  
		  
		 for(int i = 0; i<5;i++) {
			 //We dont have a playercount so try ?
			 try {
				 //Add all other player hands to list of known cards
				 ArrayList<Card> hand =state.getPlayerHand(i);
				 if(i != AiPlayer && i!=perspective) {
					 //Note that a card was played
					 
					 hand.forEach((x) -> cardsInPlay[x.getCardSuit().getID()][x.getCardValue()-1]++);
					 otherPlayersHand.add(hand); 
				 } else if(i == AiPlayer){
					 myHand = hand;
				 } else if(i == perspective) {
					 perspectiveHand = hand;
				 }
			 }catch(IndexOutOfBoundsException e) {
				 this.players = i;
				break;
			 }
		 }
		 
		 //Add Discardpile
		 /****************************************/
		 state.getDiscardPile().forEach((x) -> cardsInPlay[x.getCardSuit().getID()][x.getCardValue()-1]++);
		 
		 
		 //Extrapolate Firework stacks
		 /****************************************/
		 
		 
		 for(int i = 0;i<5;i++) {
			  int topCard =  state.getTopCard(i);
			  //If there is a card in the stack
			  if(topCard !=0) {
				  //Go through the suit adding all cards from the top to 1
				  for(int j = topCard;j> 0;j--) {
					  cardsInPlay[i][j]++;
				  }
				 
			  }
		 }
		
		
		
		
	}

	@SuppressWarnings("unchecked")
	public double[][] calculateBoardCertainty(){
		int i = 0;
		int j= 0;
		double certainty = 0.0;
		double denominator = 0.0;
		double[][] certainties = new double[this.players][HANDSIZE];
		
		//Handle player first
		
		
		for(Card c : myHand) {
			certainties[AiPlayer][j] = calcCardProbablity(c,true);	
				
			j++;
		}
		j = 0;
		//Handle perspective if any
		
		if(AiPlayer != perspective) {
			
			for(Card c : perspectiveHand) {
				certainties[AiPlayer][j] = calcCardProbablity(c,true);	
					
				j++;
			}
			
		}
		//Increment and work on other players
		
		
		
		for(ArrayList<Card> hand: otherPlayersHand) {
			j = 0;
			if(i == AiPlayer || i==perspective) i++;
			for(Card c : hand) {
					certainties[i][j] = calcCardProbablity(c,false);	
						
				j++;
			}
			
			i++;
		}
		
		
		return certainties;
	}
	private double calcCardProbablity(Card c, boolean ownCard) {
		
		double certainty = 0.0;
		double denominator = 0.0;
		 
		if(c.isSuitRevealed() && c.isValueRevealed()) {
			return 1.0;
		}
		
		else if(c.isSuitRevealed()) {
			
			for(int k=0;k<5;k++) {
				certainty+=(double) cardsInPlay[c.getCardSuit().getID()][k];
				denominator+=(double) totalCards[c.getCardSuit().getID()][k];
			}
			return certainty / (denominator-1.0);
		}else if(c.isValueRevealed()) {
			
			for(int k=0;k<5;k++) {
				certainty+=(double) cardsInPlay[k][c.getCardValue()-1];
				denominator+=(double) totalCards[k][c.getCardValue()-1];
				
			}
			return certainty / (denominator-1.0);
			
		} else {
			denominator = (double)(cardCount-1);
				if(ownCard) {
					int sumOfCardsPlayed=0;
					for(int k = 0;k<5;k++) {
						for(int l = 0;l<5;l++) {
							sumOfCardsPlayed+=cardsInPlay[k][l];
						}
					}
					
					
					return (double)sumOfCardsPlayed / (denominator-1.0);
				}
			
				int cardsOfThisKindLeft = totalCards[c.getCardSuit().getID()][c.getCardValue()-1]-cardsInPlay[c.getCardSuit().getID()][c.getCardValue()-1];
				
				 return (double)cardsOfThisKindLeft / (denominator-1.0);
		}
		
	}
	//Sum of certainty times 1+score 
	public double getStateScore() {
		double[][] probablitites = calculateBoardCertainty();
		
		
		double sumOfCertainty=0.0;
		for(int k = 0;k<players;k++) {
			for(int l = 0;l<HANDSIZE;l++) {
				
				sumOfCertainty+=probablitites[k][l];
			}
		}
		//If state leads to a gameover, score is 0
		return (sumOfCertainty*(1+score))*life ;
		
	}
	
	public ArrayList<Action> getVaildMovesFromState(Board state) {
		
		//Add all valid actions
		//Note: Unsafe reference to player
		
		ArrayList<Action> actions = new ArrayList<Action>();
		//We can always play or discard
		for(int j = 0;j<4;j++) {
			actions.add(new Action(GameAction.PLAY, j));
			actions.add(new Action(GameAction.DISCARD, j));
		}
		//If it is possible to hint
		if(state.getClueTokens() != 0) {
			
		for(int i=0;i<this.players;i++) {
				//We can only hint players that are not ourselves
				int otherPlayerIndex = i;
				if(i != this.AiPlayer) {
					//For a given player we only consider valid hints i.e. don't consider hinting about green if a player has no green	
				Boolean[] suits = {false,false,false,false,false};
				Boolean[] values = {false,false,false,false,false};
				//Handle offset
				if (i > AiPlayer) {
					otherPlayerIndex-=1;
				}
				
				this.otherPlayersHand.get(otherPlayerIndex).forEach((x)-> {
					suits[x.getCardSuit().getID()] = true;
					values[x.getCardValue()-1] = true;
				});
				//Itterate the possiblites
				for(int k = 0;k<4;k++) {
					if(suits[k]) {
						actions.add(new Action(GameAction.HINTSUIT,getEnumFromId(k),i));
					}
					if(values[k]) {
						actions.add(new Action(GameAction.HINTVALUE,k+1,i));
						}
					}
					
				}
				
				}
			}
		return actions;
		}
		
		
		
private SuitEnum getEnumFromId(int id) {
		
		switch(id) {
		case 0:
			return SuitEnum.WHITE;
		case 1:
			return SuitEnum.RED;
		case 2:
			return SuitEnum.BLUE;
		case 3:
			return SuitEnum.YELLOW;
		case 4:
			return SuitEnum.GREEN;
		default:
			return null;
		}
		
	
}	
		
	}


