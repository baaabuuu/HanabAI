package dtu.hanabi_ai_board;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Board
{
	private ArrayList<Card> deck;
	private ArrayList<Card> discardPile;
	private ArrayList<Card>[] playerHand;
	private int[] fireworkStacks;

	private int tokens;
	private int life;

	private int[] cardNumbers;
	private SuitEnum[] suits;
	
	@SuppressWarnings("unchecked")
	public Board copyState()
	{
		Board board = new Board();
		board.deck = new ArrayList<Card>(deck);
		board.discardPile = new ArrayList<Card>(discardPile);
		board.playerHand = new ArrayList[playerHand.length];
		for (int i = 0; i < playerHand.length; i++)
		{
			board.playerHand[i] = new ArrayList<Card>(playerHand[i].size());
			for (Card card : playerHand[i])
			{
				board.playerHand[i].add(card.copyCard());
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
	
	public int getLife()
	{
		return life;
	}
	
    public void addClueToken()
    {
    	tokens++;
    }
    
    public void removeClueToken()
    {
    	tokens--;
    }
    
    public int getClueTokens()
    {
    	return tokens;
    }
	
	public List<Card> getPlayerHand(int playerNumber)
	{
		return playerHand[playerNumber];
	}
	
    /**
     * PlayerNumber draws the topcard of the stack.
     * @param playerNumber
     */
    public void drawCard(int playerNumber)
    {
    	playerHand[playerNumber].add(deck.remove(0));
    }
    
    /**
     * Adds a card to the firework stack, with the stack ID.<br><b>WHITE = 0<br>RED = 1<br>BLUE = 2<br>YELLOW = 3<br>GREEN = 4
     * @param card - Card object being played
     * @param stack, the stack its added to
     */
    public void playCard(Card card, int stack)
    {
    	fireworkStacks[stack] = card.getCardValue();
    }
    
    /**
     * Returns the card value of the card with the highest value in the stack with the stack ID.
     * <br><b>WHITE = 0<br>RED = 1<br>BLUE = 2<br>YELLOW = 3<br>GREEN = 4
     * @param stack
     * @return
     */
    public int getTopCard(int stack)
    {
    	return fireworkStacks[stack];
    }
    
    
    /**
     * Puts a card in the discard pile.
     * @param card
     */
    public void discardCard(Card card)
    {
    	discardPile.add(card);
    }
    

	/**
	 * Creates a new game with the following playerCount.
	 * It throws an unchecked due to how java handles declaration of arrayList array.
	 * @param playerCount
	 */
    @SuppressWarnings("unchecked")
	public void createNewGame(int playerCount)
    {
    	tokens = 6;
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
    	discardPile = new ArrayList<Card>();
    	fireworkStacks = new int[suits.length];
    	for (int a = 0; a < suits.length; a++)
    	{
    		fireworkStacks[a] = 1;
    		for (int i = 0; i < cardNumbers.length; i++)
    		{
    			for (int j = 0; j < cardNumbers[i]; j++)
    			{
        			deck.add(new Card(suits[a], i+1));
    			}
    		}
    	}
    	Collections.shuffle(deck);
    	int cardsToDraw = playerCount < 4 ? 5 : 4;
    	playerHand = new ArrayList[playerCount];
    	for (int i = 0; i < playerCount; i++)
    	{
    		System.out.println("drawing cards for player: " + i);
    		playerHand[i] = new ArrayList<Card>();
   			for (int j = 0; j < cardsToDraw; j++)
   			{
   				drawCard(i);
   			}
    	}
    }    
}
