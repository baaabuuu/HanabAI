package dtu.hanabi_ai_game;


public class Card
{
	private SuitEnum suit;
	private int cardValue;
	
	private boolean suitRevealed = false;
	private boolean valueRevealed = false;
	
	public Card copyCard()
	{
		Card card = new Card(getCardSuit(), getCardValue());
		card.suitRevealed = suitRevealed;
		card.valueRevealed = valueRevealed;
		return card;
	}
	
	public Card(SuitEnum suit, int value)
	{
		this.suit = suit;
		cardValue = value;
	}
	
	public boolean isSuitRevealed()
	{
		return suitRevealed;
	}
	
	public boolean isValueRevealed()
	{
		return valueRevealed;
	}
	
	
	public SuitEnum getCardSuit()
	{
		return suit;
	}
	
	public int getCardValue()
	{
		return cardValue;
	}
	
	public String getStringRepresentation()
	{
		StringBuilder sb = new StringBuilder();
		if (isSuitRevealed())
		{
			sb.append(suit.getColorCode() + "\u001B[0m");

		}
		else
		{
			sb.append("(" + suit.getColorCode() + "\u001B[0m)");
			
		}
		
		if (isValueRevealed())
		{
			sb.append(cardValue);
		}
		else
		{
			sb.append("(" + cardValue + ")");
		}
		return sb.toString();
	}
	
	public String getStringDuringTurn()
	{
		StringBuilder sb = new StringBuilder();
		if (isSuitRevealed())
		{
			sb.append(suit.getColorCode() + "\u001B[0m");
		}
		if (isValueRevealed())
		{
			sb.append(cardValue);
		}
		if (sb.length() == 0)
		{
			sb.append("?");
		}
		return sb.toString();
	}
}
