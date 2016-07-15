
package resources;

import java.io.IOException;
import java.nio.ByteBuffer;

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
        try {
            if (session.isOpen()) {
            	JSONObject obj = new JSONObject();
            	try {
					obj.put("name", "yoyoyo");
					obj.put("number", "123123123");
					obj.put("value", "ade");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	
               //try {
				session.getBasicRemote().sendText(obj.toString());
				//} catch (EncodeException e) {
					// TODO Auto-generated catch block
				//	e.printStackTrace();
				//};
            }
        } catch (IOException e) {
            try {
                session.close();
            } catch (IOException e1) {
                // Ignore
            }
        }
    }

    @OnMessage
    public void echoBinaryMessage(Session session, ByteBuffer bb,
            boolean last) {
        try {
            if (session.isOpen()) {
                session.getBasicRemote().sendBinary(bb, last);
            }
        } catch (IOException e) {
            try {
                session.close();
            } catch (IOException e1) {
                // Ignore
            }
        }
    }

    /**
     * Process a received pong. This is a NO-OP.
     *
     * @param pm    Ignored.
     */
    @OnMessage
    public void echoPongMessage(PongMessage pm) {
        // NO-OP
    }
}
