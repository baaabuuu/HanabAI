package dtu.AI.StateTree;

import java.util.ArrayList;

import ai_actions.Action;
import ai_actions.ActionDiscard;
import ai_actions.ActionHint;
import ai_actions.ActionPlay;
import dtu.hanabi_ai_game.Board;
import dtu.hanabi_ai_game.Card;
import dtu.hanabi_ai_game.SuitEnum;
import log.Log;
/**
 * File Author: Emil Bejder (s164161)
 * 
 * */
public class ScoreHelper {
	static private double MAXLIFE = 3;
	static private int[][] totalCards = new int[5][5];
	static private int cardCount;
	static private boolean isSetup = false;
	private static int HANDSIZE=4;
	private static void setup() {
		if(isSetup) return;
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
		isSetup = true;
		
	}
	
	public static Score calculateStateScore(int rootPlayer,Board board,int depth) {
		setup();
		
		return applyConfig(rootPlayer, board,depth);
	}
	
	private static double  fireworkScoring(int conf,Board b) {
		switch(conf) {
		
		case 0:
			return 1;
			
		
		case 1:
			double fireworksModifier=0.0;
			for(int i=0;i<5;i++) {
				//cuberootScoring
				fireworksModifier+=	Math.sqrt(b.getTopCard(i));
			}
			return fireworksModifier+1;
			
		
		
		default:
			return 1;
		}
		
	}
	private static Score applyConfig(int rootPlayer,Board board,int depth) {
						//Weighted certainty
		
		Score score = new Score((getStateCertaintySum(rootPlayer, board,depth)*ScoreConfig.getCertaintyWeight()),
				calculateInformationValue(board)*ScoreConfig.getInformationWeight(),
						((1+board.getScore())*ScoreConfig.getBoardScoreWeight()),
						((board.getLife()/MAXLIFE)*ScoreConfig.getLifeWeight()),
						(fireworkScoring(ScoreConfig.getFireworkScoreFunction(),board)*ScoreConfig.getFireworkScoreWeight()),
						(calculatedWeightedMaxScore(board)*ScoreConfig.getPotentialscoreWeight()),
						(calculateHandValue(board,rootPlayer,depth)*ScoreConfig.getHandValueWeight()),
						depth
						);
		
		
		return score;
		
	}
	
	private static double calculateHandValue(Board b,int rootPlayer,int depth) {
			//define as what is the probablity that a card can be played in the future
		int currentPlayer = (rootPlayer+depth)%b.getPlayerHands().size();
		double handScore= 0.0;
		for(Card c : b.getPlayerHand(currentPlayer)) {
			
			handScore+=handValueHelper(b,c,rootPlayer,currentPlayer);
			
			
		}
		
		
		return handScore;
		
		
	}
	//Calculate what the chances are that a card can be played in any of the stacks
	
	//ew this code is ugly
	private static double handValueHelper(Board b, Card c,int rootPlayer,int currentPlayer) {
		
		//Case: we know nothing of the card: Value is the playable fraction of the remaining cards
		//playable defined as having a value greater than the topcard of a stack in a give suit
		//The cards this card could be
		//find out how many of them can be played
		ArrayList<Card> candidateCards = TreeHelper.filterPossibilities(TreeHelper.getUnknowns(b, rootPlayer, currentPlayer),c);
		ArrayList<Card> potentiallyPlayable = new ArrayList<Card>();
		int[][] discardAndPLayed = b.getTrueDiscardMatrix();
		//As a result of the filter cards function the knowledge of the card c is implict for all possibilities of it.
		for(Card cc : candidateCards) {
			//This condition should only happen in the case where candidateCards size = 1;
			
			if(c.isSuitRevealed() && c.isValueRevealed()) {
				
				//if we know what suit the card is we simply check if card is 
				int id = cc.getCardSuit().getID();
				int topcard = b.getTopCard(id);
				if(cc.getCardValue()>topcard) {	
					
						int score =5;
						if(topcard == 5) {					
						continue;
						}
						for(int j =topcard;j<5;j++) {
							//Beware of spooky off by one here
							
							if(totalCards[id][j] ==discardAndPLayed[id][j]) { score = j-1;
							
							break;
							}
							}
							//if then we know the value is greater than the current and if not imediatly above it is possible to play it
						if(score>=cc.getCardValue()) {
							potentiallyPlayable.add(cc);
						}
							
					
				}
				
			} else if(c.isSuitRevealed()){
				//find number of playable cards under playable threshold+
				int id =  b.getTopCard(cc.getCardSuit().getID());
				int topcard = b.getTopCard(cc.getCardSuit().getID());
				int score = 5;
				if(topcard<5) {
					for(int j =topcard;j<5;j++) {
						//Beware of spooky off by one here
						
						if(totalCards[id][j] ==discardAndPLayed[id][j]) { score = j-1;
						
						break;
						}
						}
					if(score> topcard) {
						potentiallyPlayable.add(cc);
					}
				}
				
			}else if(c.isValueRevealed()){
				
				int value = cc.getCardValue();
				for(int i =0;i<5;i++) {
					int topcard = b.getTopCard(i);
					if(value>topcard) {
						
						
						int score = 5;
						if(topcard<5) {
							for(int j =topcard;j<5;j++) {
								//Beware of spooky off by one here
								
								if(totalCards[i][j] ==discardAndPLayed[i][j]) { score = j-1;
								
								break;
								}
								}
							if(score> topcard) {
								potentiallyPlayable.add(cc);
								break;
							}
						}
						
						
					}
					
					
					
					
				}
				
				
			}
			else {
				
				for(int i =0;i<5;i++) { //suit
					int topcard = b.getTopCard(i);
					int score =5;
					if(topcard == 5) {
					
						
						continue;
					}
					for(int j =topcard;j<5;j++) {
						//Beware of spooky off by one here
						
						if(totalCards[i][j] ==discardAndPLayed[i][j]) { score = j-1;
						break;}
						
					}
					if (score>topcard) {
						potentiallyPlayable.add(cc);
						break;
					}
				}
				
			}	
		}
		
		
		double cardScore = potentiallyPlayable.size()/candidateCards.size();
		
		
		
		
		
		
		return cardScore;
		
		
		
	}
	
	
	private static double calculatedWeightedMaxScore(Board b) {
		
		int[][] discardAndPLayed = b.getTrueDiscardMatrix();
		double[][] maxScoresAndWeight = new double[5][2];
		double weightedMax = 0.0;
		for(int i =0;i<5;i++) { //suit
			int topcard = b.getTopCard(i);
			int score =5;
			if(topcard == 5) {
				maxScoresAndWeight[i][0]=5;
				maxScoresAndWeight[i][1]=boardScoreProbablityFunction(totalCards[i][topcard-1],discardAndPLayed[i][topcard-1]);
				weightedMax+=maxScoresAndWeight[i][0]*maxScoresAndWeight[i][1];
				continue;
			}
			for(int j =topcard;j<5;j++) {
				//Beware of spooky off by one here
				
				if(totalCards[i][j] ==discardAndPLayed[i][j]) { score = j-1;
				break;}
				
			}
			maxScoresAndWeight[i][0]=score;
			maxScoresAndWeight[i][1]=boardScoreProbablityFunction(totalCards[i][topcard],discardAndPLayed[i][topcard]);
			weightedMax+=maxScoresAndWeight[i][0]*maxScoresAndWeight[i][1];
		}
		
		
		
		return weightedMax;
		
		
	}
	private static double boardScoreProbablityFunction(int maxCards, int discarded) {
		
		return ((maxCards-discarded)/maxCards)+Math.log(1+(maxCards-discarded));
		
	}
	
	private static double calculateInformationValue(Board b) {
		
		
		ArrayList<ArrayList<Card>> hands = b.getPlayerHands();
		double informationSum = 0.0;
		for(ArrayList<Card> hand : hands) {
			
			for(Card c : hand) {
				if(c.isValueRevealed()) informationSum+= informationFunction(b,c.getCardValue()) ;
			}
			
			
		}

		
	return informationSum;
		
		
	}
	
	private static double informationFunction(Board b,int value) {
		//make sure we never go zero
		double fireworksSum=1.0;
		for(int i=0;i<5;i++) {
			//cuberootScoring
			fireworksSum+=	b.getTopCard(i);
		}
		
		double sumSquare= Math.sqrt(fireworksSum);
		
		
		
		double result = 5+sumSquare+(Math.log(value-(sumSquare-1))-value);
		//Handle NaN
		if(result == Double.NEGATIVE_INFINITY || result != result) result = 0;
		
		return result;
		
	}
	
	public static double getScoreWeightFromState(StateTree s) {
		
		if(s instanceof BeliefState) {
			
			double children = 0;
			double betterChildern=0;
			for(StateTree child : s.getChildern()) {
				double score = child.getScore();
			
				children++;
				if(score> s.getRoot().getScore()) betterChildern++;
			}
			
			return (betterChildern/children)*getScoreWeightFromState(s.getRoot());
			
			
			
			
		}else {
		
	
		if(s.getRoot() == null) {
			return 1.0;
		}
		//Log.log("Score weight: "+this.stateScoreWeight);
		return s.stateScoreWeight*getScoreWeightFromState(s.getRoot());
		}
	}
	

	public static double getStateCertaintySum(int rootPlayer,Board board,int depth) {
		double[][] probablitites = calculateBoardCertainty(rootPlayer, board, depth);
		double sumOfCertainty=0.0;
		for(int k = 0;k<board.getPlayerHands().size();k++) {
			for(int l = 0;l<HANDSIZE;l++) {	
				sumOfCertainty+=probablitites[k][l];
			}
		}
		//If state leads to a gameover, score is 0
		return  sumOfCertainty;
		
	}
	
	public static double[][] calculateBoardCertainty(int rootPlayer,Board board,int depth){
		
		int players = board.getPlayerHands().size();
		//int currentPlayer = rootPlayer;
		int currentPlayer = (rootPlayer+depth)%players;
		ArrayList<Card> rootPlayerHand = board.getPlayerHand(rootPlayer);
		ArrayList<Card> currentPlayerHand = board.getPlayerHand(currentPlayer);
		int i = 0;
		int j= 0;
		
		double[][] certainties = new double[players][HANDSIZE];
		
		//Handle player first
		int[][] cardsInPlay = getCardsInPlay(board, rootPlayer, depth);
		
		for(Card c : rootPlayerHand) { 
			certainties[rootPlayer][j] = calcCardProbablity(c,true, board,cardsInPlay);	
				
			j++;
		}
		j = 0;
		//Handle perspective if any
		
		if(rootPlayer != currentPlayer) {
			
			for(Card c : currentPlayerHand) {
				certainties[rootPlayer][j] = calcCardProbablity(c,true, board,cardsInPlay);	
					
				j++;
			}
			
		}
		//Increment and work on other players
		
		
		
		for(i = 0;i<players;i++) {
			j = 0;
			if((i == rootPlayer || i==currentPlayer)&& i !=players) {
				i++;
				continue;
			} 
			ArrayList<Card> hand = board.getPlayerHand(i);
			
			for(Card c : hand) {
					certainties[i][j] = calcCardProbablity(c,false, board,cardsInPlay);	
						
				j++;
			}
			
			
		}
		
		
		
		return certainties;
	}
	
	
	private static int[][] getCardsInPlay(Board board,int rootPlayer,int depth) {
		int players = board.getPlayerHands().size();
		int currentPlayer = (rootPlayer+depth)%players;
		
		 int[][]  cardsInPlay = new int[5][5];
		

		int[][] temp = board.getDiscardMatrix();
		for(int q =0;q<2;q++) {
			for(int p =0;p<4;p++) {
			cardsInPlay[q][p]=cardsInPlay[q][p]+temp[q][p];
				
			}
		}
		for(int i = 0; i<5;i++) {
			 //We dont have a playercount so try ?
			 try {
				 //Add all other player hands to list of known cards
				 ArrayList<Card> hand =board.getPlayerHand(i);
				 if(i != rootPlayer && i!=currentPlayer) {
					 //Note that a card was played
					 
					 hand.forEach((x) -> cardsInPlay[x.getCardSuit().getID()][x.getCardValue()-1]++);
					 
				 } 
			 }catch(IndexOutOfBoundsException e) {
				break;
			 }
		 }
		 
		 //Add Discardpile
		 /****************************************/
		
		 
		 
		 //Extrapolate Firework stacks
		 /****************************************/
		 
		 
		 for(int i = 0;i<5;i++) {
			  int topCard =  board.getTopCard(i);
			  //If there is a card in the stack
			  if(topCard !=0) {
				  //Go through the suit adding all cards from the top to 1
				  for(int j = topCard;j> 0;j--) {
					  
					  cardsInPlay[i][j-1]++;
				  }
				 
			  }
}
		
		return cardsInPlay;
		
	}
private static double calcCardProbablity(Card c, boolean ownCard,Board board, int[][] cardsInPlay) {
		
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
	
	
	private static SuitEnum getEnumFromId(int id) {

		switch (id) {
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
