package dtu.AI;

import dtu.hanabi_ai_game.Board;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AITest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AITest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AITest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
    	
    	
    	Board testBoard = new Board();
    	testBoard.createNewBoard(5);
    	AI testAI = new AI(new GreedyBestFirstStrategy(testBoard, 2));
    	testAI.play(3);
        
    	
    }
}
