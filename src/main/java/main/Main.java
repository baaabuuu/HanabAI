package main;

import java.util.ArrayList;

import dtu.hanabi_ai_game.Game;
import log.Log;

public class Main {

	public static void main(String[] args)
	{
		runMultipleGames(50, 4);
		//runOneGame(3);
	}
	
	public static void runMultipleGames( int gameCount, int AIcount)
	{
		ArrayList<Integer> scores = new ArrayList<Integer>();
		ArrayList<int[]> stacks = new ArrayList<int[]>();
		for (int i = 0; i < gameCount; i++)
		{
			Game game = new Game();
			scores.add(game.startGameGetScore(AIcount));
			stacks.add(game.getStacks());
		}
		
		for (int i = 0; i < gameCount; i++)
		{
			System.out.println("Scored: " + scores.get(i));
			System.out.println("Stack: " + stackToString(stacks.get(i)));
		}
		Double averageScore = scores.stream().mapToDouble(a -> a).average().getAsDouble();
		System.out.println("Average score is : " + averageScore);
	}
	
	public static void runOneGame(int AIcount)
	{
		Log.debug(true);
		Game game = new Game();
		game.startGameGetScore(AIcount);

		
	}
	
	public static String stackToString(int[] stack)
	{
		return ""+stack[0]+stack[1]+stack[2]+stack[3]+stack[4];
	}
}
