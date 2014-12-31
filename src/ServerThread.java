import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 监听服务器默认端口的服务线程
 * 
 * @author Administrator
 *
 */
public class ServerThread implements Runnable {

	private DataOutputStream destDos; // 发往目的主机的输出流
	private DataInputStream srcDis; // 来自源主机的输入流
	private DataOutputStream srcDos; // 发往源主机的输出流
	private boolean runFlag = true; // 线程运行标志

	public ServerThread(Socket socket) {
		initStream(socket);
	}

	private void initStream(Socket socket) {
		try {
			srcDis = new DataInputStream(new BufferedInputStream(
					socket.getInputStream()));
			srcDos = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("来自源主机的IO流发生错误");
		}
	}

	private void handleMessage() {
		String senderIP = "";
		String receiverIP = "";
		try {
			String msg = srcDis.readUTF();
			JSONObject object = new JSONObject(msg);
			int type = object.getInt(Constants.TYPE);
			System.out.println(type);
			switch (type) {
			case Constants.TYPE_LOGIN:
				senderIP = object.getString(Constants.SRC_IP);
				System.out.println("ip: " + senderIP + " dos: " + srcDos);
				if (Server.dosMap.containsKey(senderIP)) {
					object.put(Constants.RESPONSE, false);
				} else {
					object.put(Constants.RESPONSE, true);
					Server.dosMap.put(senderIP, srcDos);
				}
				srcDos.writeUTF(object.toString());
				break;
			case Constants.TYPE_REQUEST:
				senderIP = object.getString(Constants.SRC_IP);
				receiverIP = object.getString(Constants.DEST_IP);
				if (Server.dosMap.containsKey(receiverIP)) {
					destDos = Server.dosMap.get(receiverIP);
					destDos.writeUTF(msg);
				} else {
					System.out.println(receiverIP + ": 目标客户不存在!");
					// TODO
				}
				break;
			case Constants.TYPE_RESPONSE:
				// 将接收端的回应转发给发送端
				senderIP = object.getString(Constants.SRC_IP);
				receiverIP = object.getString(Constants.DEST_IP);
				destDos = Server.dosMap.get(senderIP);
				destDos.writeUTF(msg);

				if (object.getBoolean(Constants.RESPONSE)) {
					// 向数据库中添加文件记录
					new DBHelper().insert(object);
				}
				break;
			case Constants.TYPE_BREAKPOINT:
			case Constants.TYPE_COMPLETE:
				new DBHelper().update(object);
				break;
			case Constants.TYPE_REQSEND2:
			case Constants.TYPE_RESPRECV2:
				senderIP = object.getString(Constants.SRC_IP);
				receiverIP = object.getString(Constants.DEST_IP);
				destDos = Server.dosMap.get(receiverIP);
				destDos.writeUTF(msg);
				break;
			case Constants.TYPE_RESPSEND2:
				senderIP = object.getString(Constants.SRC_IP);
				receiverIP = object.getString(Constants.DEST_IP);
				if (object.getBoolean(Constants.RESPONSE)) {
					long breakpoint = new DBHelper().select(object);
					object.put(Constants.BREAKPOINT, breakpoint);
				}

				destDos = Server.dosMap.get(senderIP);
				destDos.writeUTF(object.toString());
				break;
			case Constants.TYPE_REQRECV2:
			case Constants.TYPE_INTERUPT:
				senderIP = object.getString(Constants.SRC_IP);
				receiverIP = object.getString(Constants.DEST_IP);
				destDos = Server.dosMap.get(senderIP);
				destDos.writeUTF(msg);
				break;
			default:

				senderIP = object.getString(Constants.SRC_IP);
				receiverIP = object.getString(Constants.DEST_IP);
				destDos = Server.dosMap.get(senderIP);
				destDos.writeUTF(msg);
				break;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			stopThread();
		} catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("ServerThread: " + Thread.currentThread());
		while (runFlag) {
			handleMessage();
		}
	}

	public void stopThread() {
		this.runFlag = false;
	}

}
