package general;

public class Role {
	private String name;
	private String description;
	private boolean view_model = false;
	private boolean edit_model = false;
	private boolean view_sentiment = false;
	private boolean save_snap = false;
	private boolean use_pred = false;
	private boolean admin=false;

	public Role(){
		//no need
	}
	public Role(String _name, String _description, boolean _view_model, boolean _edit_model, boolean _view_sentiment,
			boolean _save_snap, boolean _use_pred, boolean _admin) {

		name = _name;
		description = _description;
		view_model = _view_model;
		edit_model = _edit_model;
		view_sentiment = _view_sentiment;
		save_snap = _save_snap;
		use_pred = _use_pred;
		admin=_admin;
	}

	public boolean getPermission(int id) {
		switch (id) {
		case -1:
			return true;
		case 0:
			return view_model;
		case 1:
			return edit_model;
		case 2:
			return view_sentiment;
		case 3:
			return save_snap;
		case 4:
			return use_pred;
		case 99:
			return admin;
		default:
			return false;
		}
	}
}
