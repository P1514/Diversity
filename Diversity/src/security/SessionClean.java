package security;

import java.util.TimerTask;

import general.Data;

public class SessionClean extends TimerTask {

	private String id;
	public SessionClean(String _id){
		id=_id;
	}
    @Override
    public void run() {
        Data.deleteSession(id);
        this.cancel();
    }
}