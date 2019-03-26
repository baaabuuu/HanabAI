package dtu.AI;

import java.util.ArrayList;

import dtu.hanabi_ai_game.Board;
import dtu.hanabi_ai_game.Card;

public interface Predictor
{
	public ArrayList<Card> predict(ArrayList<ArrayList<Card>> hands, int turn, int origTurn, Board board);
}
