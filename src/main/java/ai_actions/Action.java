package ai_actions;
/**
 * Generic Action interface
 * Used to create different action types.
 * @author s164166
 */
public interface Action {
	public String play();
	public String getActionType();
	public int getTarget();
}
