package ai_actions;

/**
 * The action for a play the letter P + the index and a few getter methods.
 * @author s164166
 *
 */
public class ActionPlay implements Action {
	
	private int index;
	public ActionPlay(int index)
	{
		this.index = index;
	}
	
	public int getTarget()
	{
		return index;
	}
	
	public String play()
	{
		return "P"+index;
	}

	@Override
	public String getActionType() {
		return "P";
	}
}
