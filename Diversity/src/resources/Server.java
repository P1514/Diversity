
package resources;

import java.io.IOException;
import java.sql.SQLException;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.json.*;

@ServerEndpoint("/server")
public class Server {

	@OnMessage
	public void echoTextMessage(Session session, String msg, boolean last) {
		
		JSONObject resolve=null;
		try {
			resolve = new JSONObject(msg);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Assistant assist = new Assistant(session, resolve);
		
		assist.runn();

	}

	public class Assistant /*implements Runnable*/ {
		private Session session;
		private JSONObject msg;
		private Operations op;
		private Backend be;
		public Assistant(Session _session, JSONObject _msg) {
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

					try {
						switch (op.getOP(msg.getString("op"))) {
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
							session.getBasicRemote().sendText("LOADED SUCCESSFULLY");
						break;
						case 3:
							session.getBasicRemote().sendText(be.globalsentiment(1,5,null, 0, null).toString());
							break;
						case 4:
							session.getBasicRemote().sendText(be.globalsentiment(1, 5, msg.getString("param"), msg.getInt("n_values"), msg.getString("values")).toString());
						default:
							session.getBasicRemote().sendText("MISTAKE");
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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
