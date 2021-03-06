package dtu.hanabi_ai_game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import log.Log;

/**
 * Used to handle the statespace for a board.
 * @author s164166
 *
 */
public class Board 
{
	private ArrayList<Card> deck;
	private ArrayList<Card> playedCardsPile;
	private int[][] playedCardsMatrix = new int[5][5];
	private ArrayList<ArrayList<Card>> playerHands;
	private int[] fireworkStacks;
	private int score;

	private int tokens;
	private int life;

	private int[] cardNumbers;
	private SuitEnum[] suits;
	
	/**
	 * Add points to the board
	 * @author s164166
	 */
	public void addPoint()
	{
		score++;
	}
	
	/**
	 * Get the score of the board.
	 * @author s164166
	 * @return
	 */
	public int getScore()
	{
		return score;
	}
	
	/**
	 * Copies the board and create a copy of it.
	 * This is a deep copy method!
	 * @return
	 * @author s164166
	 */
	public Board copyState()
	{
		Board board = new Board();
		board.deck = new ArrayList<Card>(deck);
		board.playedCardsMatrix = new int[5][5];
		board.playedCardsPile = new ArrayList<Card>();
		for (Card card : playedCardsPile)
		{
			board.playedCardsPile.add(card.copyCard());
		}
		board.playerHands = new ArrayList<ArrayList<Card>>();
		board.score = score;
		for (int i = 0; i < 5; i++)
		{
			for (int j = 0; j < 5; j++)
			{
				board.playedCardsMatrix[i][j] = playedCardsMatrix[i][j];
			}
		}
				
		for (int i = 0; i < playerHands.size(); i++)
		{
			board.playerHands.add(new ArrayList<Card>(playerHands.get(i).size()));
			for (Card card : playerHands.get(i))
			{
				board.playerHands.get(i).add(card.copyCard());
			}
		}
		board.fireworkStacks = new int[fireworkStacks.length];
		
		for (int i = 0; i < fireworkStacks.length; i++)
		{
			board.fireworkStacks[i] = fireworkStacks[i];
		}
		board.tokens = tokens;
		board.life = life;
		board.cardNumbers = new int[cardNumbers.length];
		for (int i = 0; i < cardNumbers.length; i++)
		{
			board.cardNumbers[i] = cardNumbers[i];
		}
		board.suits = new SuitEnum[suits.length];
		for (int i = 0; i < suits.length; i++)
		{
			board.suits[i] = suits[i];
		}
		return board;
		
	}
	
	/**
	 * Discard Matric constraints
	 * <br>First is suit</br>
	 * <br>Second is value</br>
	 * @author s164166
	 * @return
	 */
	public int[][] getDiscardMatrix()
	{
		return playedCardsMatrix;
	}
	
	
	/**
	 * Returns the current amount of lives left
	 * @return
	 * @author s164166
	 */
	public int getLife()
	{
		return life;
	}
	
	/**
	 * Adds a clue token
	 * @author s164166
	 */
    public void addClueToken()
    {
    	tokens++;
    }
    
    /**
     * Removes a clue token.
     * @author s164166
     */
    public void removeClueToken()
    {
    	tokens--;
    }
    
    /**
     * Gets the amount of clues token
     * @author s164166
     * @return the amount of clue tokens left
     */
    public int getClueTokens()
    {
    	return tokens;
    }
	
    /**
     * Returns the hand of the player.
     * @author s164166
     * @param playerNumber
     * @return
     */
	public ArrayList<Card> getPlayerHand(int playerNumber)
	{
		return playerHands.get(playerNumber);
	}
	
	/**
	 * Removes a life
	 * @author s164166
	 */
	public void removeLife()
	{
		life--;
	}
	
    /**
     * PlayerNumber draws the topcard of the stack.
     * @param playerNumber
     * @author s164166
     */
    public void drawCard(int playerNumber)
    {
    	playerHands.get(playerNumber).add(deck.remove(0));
    }
    
    /**
     * Adds a card to the firework stack, with the stack ID.<br><b>WHITE = 0<br>RED = 1<br>BLUE = 2<br>YELLOW = 3<br>GREEN = 4
     * @param card - Card object being played
     * @param stack, the stack its added to
     * @author s164166
     */
    public void playCard(Card card, int stack)
    {
    	playedCardsPile.add(card);
    	//playedCardsMatrix[card.getCardSuit().getID()][card.getCardValue()-1]++;
    	fireworkStacks[stack] = card.getCardValue();
    }
    
    /**
     * Gets the amount of cards left in the deck
     * @return the amm of cards left.
     * @author s164166
     */
    public int getDeckSize()
    {
		return deck.size();
    	
    }
    
    /**
     * Returns the card value of the card with the highest value in the stack with the stack ID.
     * <br><b>WHITE = 0<br>RED = 1<br>BLUE = 2<br>YELLOW = 3<br>GREEN = 4
     * @param stack
     * @return value of the highest card in that stack.
     * @author s164166
     */
    public int getTopCard(int stack)
    {
    	return fireworkStacks[stack];
    }
    
    public int[] getFireworkStacks()
    {
    	return fireworkStacks;
    }
    
    
    /**
     * Puts a card in the playCard pile.
     * @param card
     * @author s164166
     */
    public void discardCard(Card card)
    {
    	playedCardsMatrix[card.getCardSuit().getID()][card.getCardValue()-1]++;
    	playedCardsPile.add(card);
    }
    
    public ArrayList<Card> getPlayedCards()
    {
    	return playedCardsPile;
    }

	/**
	 * Creates a new board with the following playerCount.
	 * @param playerCount
	 * @author s164166
	 */
	public void createNewBoard(int playerCount)
    {
    	tokens = 8;
    	life = 3;
    	cardNumbers = new int[5];
    	cardNumbers[0]	=	3;
    	cardNumbers[1]	=	2;
    	cardNumbers[2]	=	2;
    	cardNumbers[3]	=	2;
    	cardNumbers[4]	=	1;
    	
    	suits = new SuitEnum[5];
    	suits[0]	=	SuitEnum.WHITE;
    	suits[1]	=	SuitEnum.RED;
    	suits[2]	=	SuitEnum.BLUE;
    	suits[3]	=	SuitEnum.YELLOW;
    	suits[4]	=	SuitEnum.GREEN;
    	
    	deck = new ArrayList<Card>();
    	playedCardsMatrix = new int[5][5];
    	playedCardsPile = new ArrayList<Card>();

    	fireworkStacks = new int[suits.length+1];
    	deck = generateDeck();
    	for (int a = 0; a < suits.length; a++)
    	{
    		fireworkStacks[a] = 0;
    	}
    	
    	//Log.important("THE SHUFFLE IS SEEDED!");
    	Collections.shuffle(deck);
    	//Collections.shuffle(deck, new Random(1));

    	int cardsToDraw = playerCount < 4 ? 5 : 4;
    	playerHands = new ArrayList<ArrayList<Card>>();
    	for (int i = 0; i < playerCount; i++)
    	{
    		playerHands.add(new ArrayList<Card>());
   			for (int j = 0; j < cardsToDraw; j++)
   			{
   				drawCard(i);
   			}
    	}
    }

	/**
	 * Genereate the deck
	 * @author s164166
	 * @return
	 */
	public ArrayList<Card> generateDeck() {
		ArrayList<Card> deck = new ArrayList<Card>();
		for (int a = 0; a < suits.length; a++)
    	{
    		for (int i = 0; i < cardNumbers.length; i++)
    		{
    			for (int j = 0; j < cardNumbers[i]; j++)
    			{
        			deck.add(new Card(suits[a], i+1));
    			}
    		}
    	}
		return deck;
	}   
	
	/**
	 * Get the player hands.
	 * @author s164166
	 * @return
	 */
	public ArrayList<ArrayList<Card>> getPlayerHands()
	{
		return playerHands;
	}
}

