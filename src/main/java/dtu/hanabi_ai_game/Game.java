package dtu.hanabi_ai_game;

import java.util.ArrayList;
import java.util.Scanner;

import log.Log;

public class Game
{
	public Board board;
	int humanAmm, aiAmm, playerCount;
	int durationBetweenTurns = 5;
	public Game()
	{
		Log.important("Generating new Game");
		Scanner scanner = new Scanner(System.in);
		System.out.println("Welcome to a new game of Hanabi.");
		while(true)
		{
			System.out.println("In order to player, write the number of human, followed by the number of AIs that are going to play the game with a maximum of 5 in total");
			System.out.println("Ex, 13 indicates 1 human and 3 AIs playing");
			String input = scanner.nextLine();
			humanAmm = input.charAt(0)-48;
			aiAmm = input.charAt(1)-48;
			playerCount = humanAmm + aiAmm;
			if (aiAmm + humanAmm < 2)
			{
				System.out.println("You need at least 2 players to play this game");
			}
			else if (aiAmm + humanAmm > 5)
			{
				System.out.println("You cannot have more than 5 players!");
			}
			else
			{
				System.out.println("Great, you will have " + humanAmm + " human playing and " + aiAmm + " AI playing!");
				break;
			}
		}
		scanner.close();
		board = new Board();
		board.createNewBoard(playerCount);
		int turn = 0;
		while(true)
		{
			takeTurn(turn);
			turn = (turn+1 == playerCount) ? 0 : turn+1;
			System.out.println(turn);
		}
	}
	private void takeTurn(int turn)
	{
		Log.important("turn is equal to: " + turn + " human amm is: " + humanAmm);
		if (turn < humanAmm)
		{
			Log.log("HUMAN TURN - applying human hooks");
			System.out.println("Player "  + (turn+1) + " its your turn!");
			System.out.println("Revealing information specific to that player in..");
			timer(durationBetweenTurns);
			ArrayList<Card>[] hands = getPlayerHands();
			displayPlayerhands(hands, turn);
			
		}
		else
		{
			Log.important("AI TURN - applying AI hooks");
		}
		
	}
	
	/**
	 * Does a quick countdown.
	 * @param duration
	 */
	private void timer(int duration)
	{
		System.out.println(duration);
		while(duration > 0)
		{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			duration--;
			if (duration>0)
			System.out.println(duration);
		}
	}
	
	@SuppressWarnings("unchecked")
	private ArrayList<Card>[] getPlayerHands()
	{
		ArrayList<Card> hands[] = new ArrayList[playerCount];
		for (int i = 0; i < playerCount; i++)
		{
			hands[i] = board.getPlayerHand(i);
		}
		return hands;
	}
	
	private void displayPlayerhands(ArrayList<Card>[] hands, int turn)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < playerCount; i++)
		{
			if (i != turn)
			{
				sb.append("player: ");
				sb.append((i+1));
				sb.append(" has ");
				for(Card card : hands[i])
				{
					sb.append(card.getStringRepresentation());
					sb.append(' ');
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.append('\n');
			}
			else
			{
				sb.append("Your hand is ");
				for(Card card : hands[i])
				{
					sb.append(card.getStringDuringTurn() + ' ');
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.append('\n');
			}
			
		}
		sb.deleteCharAt(sb.length() - 1);
		System.out.println(sb);
	}
	

}
