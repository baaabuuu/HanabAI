package dtu.AI;

import java.util.ArrayList;

import dtu.hanabi_ai_game.Board;
import dtu.hanabi_ai_game.Card;
/**
 * The predictor class is made for eliminating cards with information about them to give full information
 * @author s160902
 */
public interface Predictor
{
	public ArrayList<Card> predict(ArrayList<ArrayList<Card>> hands, int turn, int origTurn, Board board);
}
