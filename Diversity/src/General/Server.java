
package General;

import java.io.IOException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint.Async;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.json.*;

@ServerEndpoint("/server")
public class Server {
	public static boolean isloading = false;
	private Session session;
	private Operations op = new Operations();
	Async as;
	
	@OnOpen
	public void open(Session session){
		as = session.getAsyncRemote();
		this.session=session;
	}
	@OnMessage
	public void receivedMessage(Session session, String msg, boolean last) {

		JSONObject resolve = null;
		try {
			resolve = new JSONObject(msg);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Assistant a = new Assistant(session, resolve);
		//Thread assist = new Thread(a);
		runn(resolve);

	}

	@OnClose
	public void onClose(Session session) {
		try {
			session.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void send_message(String msg) {
			System.out.println("\r\nOUT: "+ msg);
			as.sendText(msg);
			return;
	}

	@OnError
	public void onError(Session session, Throwable thr) {
		try {
			session.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*public void Assistantimplements Runnable {
		private Session session;
		private JSONObject msg;
		private Operations op;
		private Backend be;

		public void Assistant(Session _session, JSONObject _msg) {
			session = _session;
			msg = _msg;
			op = new Operations();
		}

		// @Override*/
		public void runn(JSONObject msg) {
			JSONArray result = new JSONArray();
			JSONObject obj = new JSONObject();
			// System.out.println(msg);
			Backend be;

			try {
				if (session.isOpen()) {
					try {
						while (true) {
							if (Server.isloading == true) {
								if (op.getOP(msg.getString("Op")) == 2) {
									obj.put("Op", "Error");
									obj.put("Message", "Loading in Progress please wait a few minutes and try again");
									result.put(obj);
									session.getBasicRemote().sendText(result.toString());
									return;
								}
								Thread.sleep(1000);

							} else {
								break;
							}
						}
						if (op.getOP(msg.getString("Op")) == 2) {
							Server.isloading = true;
							obj.put("Op", "Error");
							obj.put("Message",
									"Close this Message and wait for the next one to confirm Database loading");
							result.put(obj);
							session.getAsyncRemote().sendText(result.toString());
						}
						be = new Backend(op.getOP(msg.getString("Op")), msg);
						if (op.getOP(msg.getString("Op")) == 2)
							Server.isloading = false;
						String answer = be.resolve().toString();
						send_message(answer);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
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
			// System.out.println(result.toString());
		}
	//}
}