package dtu.hanabi_ai_game;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import dtu.AI.AI;
import dtu.AI.DFSStrategy;
import log.Log;
import monte_carlo.MonteCarloTreeSearch;

public class Game
{
	public Board board;
	int humanAmm, aiAmm, playerCount;
	int durationBetweenTurns = 0;
	int suitCount	=	5;
	int finalRound = -1;
	int finished = 0;
	int humanModifier = 0;
	int turn = 0;
	Scanner scanner = new Scanner(System.in);
	private ArrayList<AI> AIList = new ArrayList<AI>();
	
	public Game(Game game) {
		this.board = game.board.copyState();
		this.humanAmm = game.humanAmm;
		this.playerCount = game.playerCount;
		this.durationBetweenTurns = game.durationBetweenTurns;
		this.suitCount = game.suitCount;
		this.finalRound = game.finalRound;
		this.finished = game.finished;
		this.humanModifier = game.humanModifier;
		this.turn = game.turn;
		
		
	}

	public Game() {
		// TODO Auto-generated constructor stub
	}

	public int startGameGetScore(int AIcount)
	{
		humanAmm = 0;
		aiAmm = AIcount;
		playerCount = aiAmm;
		board = new Board();
		board.createNewBoard(playerCount);
		for (int i = humanAmm; i < playerCount; i++)
		{
			AIList.add(new AI(new DFSStrategy(board, i, playerCount)));
		}
		int turn = 0;
		while(true)
		{
			if (finished == 5 || board.getLife() == 0 || finalRound == turn)
			{
				break;
			}
			takeTurn(turn);
			turn = (turn+1 == playerCount) ? 0 : turn+1;
		}
		return board.getScore();
	}
	
	public void startRolloutGame(Board board, int noOfPlayers, int finished, int turn, int finalRound){
		playerCount = noOfPlayers;
		humanAmm = 0;
		aiAmm = playerCount;
		this.board = board;
		this.finished = finished;
		this.turn = turn;
		this.finalRound = finalRound;
				
	}
	
	public Boolean rolloutTakeAction(String action){
		takeAction(action, turn);
		turn = (turn + 1) % playerCount;
		
		return finished == 5 || board.getLife() == 0 || finalRound == turn;
	}
	
	//Returns -1 if the game is going, and the score if it is over
	public int isGameOver() {
		return (finished == 5 || board.getLife() == 0 || finalRound == turn) ? board.getScore() : -1;
	}
	
	public void randomizeHand(int playerNo) {
		// TODO Auto-generated method stub
		board.randomizHand(playerNo);
		
	}
	
	public void startGame()
	{
		Log.important("Generating new Game");
		System.out.println("Welcome to a new game of Hanabi.");
		while(true)
		{
			System.out.println("In order to player, write the number of human, followed by the number of AIs that are going to play the game with a maximum of 5 in total");
			System.out.println("Ex, 13 indicates 1 human and 3 AIs playing");
			String input = getNextInput();
			Log.important(input + " was gotten?");
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
		board = new Board();
		board.createNewBoard(playerCount);
		for (int i = humanAmm; i < playerCount; i++)
		{
			AIList.add(new AI(new DFSStrategy(board, i, playerCount)));
		}
		while(true)
		{
			if (finished == 5 || board.getLife() == 0 || finalRound == turn)
			{
				System.out.println("Game is now over... And the score is " + board.getScore());

				break;
			}
			takeTurn(turn);
			turn = (turn+1 == playerCount) ? 0 : turn+1;
		}
	}
	
	private void takeTurn(int turn)
	{
		Log.important("turn is equal to: " +( turn+1));
		ArrayList<Card>[] hands = getPlayerHands();

		if (Log.debug)
		{
			getNextInput();
			Log.log("Life count is: " + board.getLife());
			Log.log("Token count is: " + board.getClueTokens());
			Log.log("Deck Size is: " + board.getDeckSize());
			displayDebugPlayerhands(hands);
		}
		getStackPiles();
		if (turn < humanAmm)
		{
			humanModifier = 1;
			Log.log("HUMAN TURN - applying human hooks");
			System.out.println("Player "  + (turn+1) + " its your turn!");
			System.out.println("Revealing information specific to that player in..");
			timer(durationBetweenTurns);
			displayPlayerhands(hands, turn);
			while(true)
			{
				getActions();
				String action = getNextInput();
				Log.log("action string is: " + action);
				if (takeAction(action, turn))
				{
					break;
				}
			}
			
		}
		else
		{
			humanModifier = 0;
			Log.important("AI TURN - applying AI hooks for AI " + (turn - humanAmm+1));
			while(true)
			{
				//int MaxDepth = playerCount;
				MonteCarloTreeSearch ai = new MonteCarloTreeSearch();
				
				boolean tempbool = Log.debug;
				
				Log.debug(false);
				String action = ai.findNextMove(board, turn, this, 2000, 500, playerCount);
				if(tempbool)
					Log.debug(true);
				Log.log("action string is: " + action);
				if (takeAction(action, turn))
				{
					break;
				}
			}	
		}
	}
	
	private String getNextInput()
	{
		String input = "";
		input = scanner.nextLine();
		return input;

	}
	/**
	 * Prints which actions are available.
	 */
	private void getActions()
	{
		System.out.println("To discard a card from your hand write D[1/2/3/4/5]");
		System.out.println("To give a hint about a color suite write H[playerNumber][W/R/B/Y/G]");
		System.out.println("To give a hint about a numbere write H[playerNumber][1/2/3/4/5]");
		System.out.println("To play a card write P[1/2/3/4/5]");
	}
	
	private boolean takeAction(String action, int turn)
	{
		
		char symbol = action.charAt(0);
		if (symbol == 'D' || symbol == 'd')
		{
			int value = Character.getNumericValue(action.charAt(1)) - humanModifier;
			if (value >= 0 && value < board.getPlayerHand(turn).size() && board.getClueTokens() < 8)
			{
				Card card = board.getPlayerHand(turn).remove(value);
				card.revealSuit();
				card.revealValue();
				board.discardCard(card);
				if (board.getClueTokens() < 8)
				{
					board.addClueToken();
				}
				if (board.getDeckSize() > 0)
				{
					board.drawCard(turn);
					if (board.getDeckSize() == 0)
					{
						finalRound = turn;
					}
				}
				Log.log("Handling discard action for player " + (turn+1) + " they discarded " + card.getStringRepresentation());
				return true;
			}
			Log.log("Handling discard action for player " + (turn+1) + " they tried to discard an invalid card");
			return false;
		} else if (symbol == 'H' || symbol == 'h')
		{
			if (board.getClueTokens() > 0)
			{
				board.removeClueToken();
				//Since range is 1-5 we want it to be 0-4 for ease of use
				int playerNumber = Character.getNumericValue(action.charAt(1))	-	humanModifier;
				char identifier = action.charAt(2);
				if (playerNumber < playerCount)
				{
					if (identifier == '1' || identifier == '2' || identifier == '3' || identifier == '4' || identifier == '5')
					{

							int charValue = Character.getNumericValue(identifier);
							for (Card card : board.getPlayerHand(playerNumber))
							{
								if (card.getCardValue() == charValue)
								{
									card.revealValue();
								}
							}
							return true;
					}
					else if (identifier == 'W' || identifier == 'R' || identifier == 'B' || identifier == 'Y' || identifier == 'G' ||
							 identifier == 'w' || identifier == 'r' || identifier == 'b' || identifier == 'y' || identifier == 'g')
					{
						int id = 0;
						if (identifier == 'R' || identifier == 'r')
						{
							id = 1;
						}
						else if (identifier == 'B' || identifier == 'b')
						{
							id = 2;
						}
						else if (identifier == 'Y' || identifier == 'y')
						{
							id = 3;
						}
						else if (identifier == 'G' || identifier == 'g')
						{
							id = 4;
						}

						for (Card card : board.getPlayerHand(playerNumber))
						{
							if (card.getCardSuit().getID() == id)
							{
								card.revealSuit();
							}
						}
						return true;
					}
					else
					{
						Log.log("Handling help action for player " + (turn+1) + " they tried to help " + playerNumber + " but the option " + identifier + " cannot be used!");
					}

				}
				else
				{
					Log.log("Handling help action for player " + (turn+1) + " they tried to help an invalid player");
					
				}
				return false;
			}
		} else if (symbol == 'P' || symbol == 'p')
		{
			int cardBeingPlayed = Character.getNumericValue(action.charAt(1));
			if (cardBeingPlayed >= 0 && cardBeingPlayed < board.getPlayerHand(turn).size())
			{
				Card card = board.getPlayerHand(turn).remove(cardBeingPlayed - humanModifier);
				card.revealSuit();
				card.revealValue();
				int cardSuite = card.getCardSuit().getID();
				Log.log("Top of the stack is: " + board.getTopCard(cardSuite));
				if (board.getTopCard(cardSuite)+1 ==  card.getCardValue())
				{
					board.playCard(card, cardSuite);
					board.addPoint();
					if (card.getCardValue() == 5 && board.getClueTokens() < 8)
					{
						finished++;
						board.addClueToken();
						Log.log("player play action for player " + (turn+1) + " they played " + card.getStringRepresentation() + " succesfully.");

					}
				}
				else
				{
					board.discardCard(card);
					board.removeLife();
					Log.log("player play action for player " + (turn+1) + " they played " + card.getStringRepresentation() + " unsuccesfully.");

				}
				
				if (board.getDeckSize() > 0)
				{
					board.drawCard(turn);
					if (board.getDeckSize() == 0)
					{
						finalRound = turn;
					}
				}
				return true;
			}
			Log.log("Discarding from play due to invalid play for player " + (turn+1) + " they tried to discard an invalid card");
			return false;
		}
		return false;
	
	}
	
	public void getStackPiles()
	{
		if (!Log.debug)
		{
			System.out.println("Top of each firework stack is:");
			System.out.println("W"+board.getTopCard(0) + " R"+board.getTopCard(1) + " B"+board.getTopCard(2) + " Y"+board.getTopCard(3) + " G"+board.getTopCard(4));
		}
		Log.log("Top of each firework stack is:");
		Log.log("W"+board.getTopCard(0) + " R"+board.getTopCard(1) + " B"+board.getTopCard(2) + " Y"+board.getTopCard(3) + " G"+board.getTopCard(4));

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
	
	private void displayDebugPlayerhands(ArrayList<Card>[] hands)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		for (int i = 0; i < playerCount; i++)
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
		sb.deleteCharAt(sb.length() - 1);
		Log.log(sb.toString());
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
		Log.log(sb.toString());
	}
	
	public int[] getStacks()
	{
		return board.getFireworkStacks();
	}

	public Board getBoard() {
		return board;
	}

	public List<Game> getAllPermutationsOfCard(int playerNo, int card) {
		Board boardHolder =  board.copyState();
		List<Game> games = new ArrayList<>();
		//Gets all the possible board states
		for(Board tempBoard : board.getAllPermutationsOfCard(playerNo, card)){
			//Copies this game with the board changed
			board = tempBoard;
			games.add(new Game(this));
		}
		
		//Resets the board
		board = boardHolder;
		
		return games;
	}

	



}
