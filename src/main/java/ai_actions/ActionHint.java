package ai_actions;

public class ActionHint implements Action {
	private int target;
	private String type;
	public int getTarget()
	{
		return target;
	}
	public ActionHint(int target, String type)
	{
		this.target = target;
		this.type = type;
	}
	
	public String play()
	{
		return "H"+target+type;
	}

	@Override
	public String getActionType() {
		return "H";
	}
}
