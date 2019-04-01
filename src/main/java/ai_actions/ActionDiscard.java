package ai_actions;

/**
 * The discard action, D + the index of the card that is desired to be discarded.
 * @author s164166
 *
 */
public class ActionDiscard implements Action {
	private int index;
	public int getTarget()
	{
		return index;
	}
	public ActionDiscard(int index)
	{
		this.index = index;
	}
	
	public String play()
	{
		return "D"+index;
	}

	@Override
	public String getActionType() {
		return "D";
	}
}
