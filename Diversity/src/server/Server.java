
package server;

import java.io.IOException;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.json.*;
import backend.Backend;

@ServerEndpoint("/server")
public class Server {

	@OnMessage
	public void echoTextMessage(Session session, String msg, boolean last) {

		JSONObject resolve = null;
		try {
			resolve = new JSONObject(msg);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Assistant assist = new Assistant(session, resolve);

		assist.runn();

	}

	public class Assistant /* implements Runnable */ {
		private Session session;
		private JSONObject msg;
		private Operations op;
		private Backend be;

		public Assistant(Session _session, JSONObject _msg) {
			session = _session;
			msg = _msg;
			op = new Operations();
		}

		// @Override
		public void runn() {

			
			
			try {
				if (session.isOpen()) {
					try {
						be = new Backend(op.getOP(msg.getString("op")), msg);
						session.getBasicRemote().sendText(be.resolve());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				try {
					session.close();
				} catch (IOException e1) {
					// Ignore
				}
			}

		}
	}
}
