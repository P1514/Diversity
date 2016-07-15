
package resources;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.SQLException;

import javax.websocket.EncodeException;
import javax.websocket.OnMessage;
import javax.websocket.PongMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.json.*;

@ServerEndpoint("/server")
public class Server {

	@OnMessage
	public void echoTextMessage(Session session, String msg, boolean last) {

		Assistant assist = new Assistant(session, msg);
		
		assist.runn();

	}

	public class Assistant /*implements Runnable*/ {
		private Session session;
		private String msg;
		private Operations op;
		private Backend be;

		public Assistant(Session _session, String _msg) {
			session = _session;
			msg = _msg;
			op = new Operations();
			be = new Backend();
		}

		//@Override
		public void runn() {

			System.out.println(msg);
			try {
				if (session.isOpen()) {

					switch (op.getOP(msg)) {
					case 1:
						session.getBasicRemote().sendText(be.chartrequest().toString());
						break;
					case 2: Data dat = new Data();
						try {
							dat.load();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					break;
					default:
						session.getBasicRemote().sendText("MISTAKE");
					}

					/*
					 * JSONObject obj = new JSONObject(); try { obj.put("name",
					 * "yoyoyo"); obj.put("number", "123123123");
					 * obj.put("value", "ade"); } catch (JSONException e) { //
					 * TODO Auto-generated catch block e.printStackTrace(); }
					 * 
					 * // try {
					 * session.getBasicRemote().sendText(obj.toString()); // }
					 * catch (EncodeException e) { // TODO Auto-generated catch
					 * block // e.printStackTrace(); // };
					 */
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
