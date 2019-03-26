package dtu.AI;

import java.util.ArrayList;

import ai_actions.Action;
import dtu.hanabi_ai_game.Board;
import dtu.hanabi_ai_game.Card;

public class BoardScorerCertainty implements BoardScorer {
		private int[][] totalCards;
		private int cardCount;
		private int HANDSIZE;
		public BoardScorerCertainty() {
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
			
			
			
		}
	@Override
	public int getBoardScore(Board board, int origTurn, Action action, int currDepth, int maxDepth) {
		// TODO Auto-generated method stub
		
		
		return (int) getStateScore(origTurn, board,currDepth);
	}

	
	@SuppressWarnings("unchecked")
	public double[][] calculateBoardCertainty(int AiPlayer,Board board,int depth){
		//Hardcoded perspective for now
		int players = board.getPlayerHands().size();
		int perspective = (AiPlayer+depth)%players;
		ArrayList<Card> myHand = board.getPlayerHand(AiPlayer);
		ArrayList<Card> perspectiveHand = board.getPlayerHand(perspective);
		int i = 0;
		int j= 0;
		
		double[][] certainties = new double[players][HANDSIZE];
		
		//Handle player first
		
		
		for(Card c : myHand) {
			certainties[AiPlayer][j] = calcCardProbablity(c,true, board);	
				
			j++;
		}
		j = 0;
		//Handle perspective if any
		
		if(AiPlayer != perspective) {
			
			for(Card c : perspectiveHand) {
				certainties[AiPlayer][j] = calcCardProbablity(c,true, board);	
					
				j++;
			}
			
		}
		//Increment and work on other players
		
		
		
		for(i = 0;i<players;i++) {
			j = 0;
			if(i == AiPlayer || i==perspective) i++;
			ArrayList<Card> hand = board.getPlayerHand(i);
			for(Card c : hand) {
					certainties[i][j] = calcCardProbablity(c,false, board);	
						
				j++;
			}
			
			
		}
		
		
		
		return certainties;
	}
	private double calcCardProbablity(Card c, boolean ownCard,Board board) {
		int[][] cardsInPlay = board.getDiscardMatrix();
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
	public double getStateScore(int AiPlayer,Board board,int depth) {
		double[][] probablitites = calculateBoardCertainty(AiPlayer, board, depth);
		
		
		double sumOfCertainty=0.0;
		for(int k = 0;k<board.getPlayerHands().size();k++) {
			for(int l = 0;l<HANDSIZE;l++) {
				
				sumOfCertainty+=probablitites[k][l];
			}
		}
		//If state leads to a gameover, score is 0
		return ((sumOfCertainty*(1+board.getScore()))*board.getLife())*100 ;
		
	}
	
	 
	
}
