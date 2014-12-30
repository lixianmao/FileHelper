import java.io.DataOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

public class SendHelper {

	private DataOutputStream dos;

	// private ClientGUI client;

	public SendHelper(DataOutputStream dos) {
		this.dos = dos;
	}

	public void sendLogin(String localIP) {
		System.out.println(localIP + " 正在发送登陆请求");
		ClientGUI.printArea.append("正在发送登陆请求 \n");
		try {
			JSONObject object = new JSONObject();
			object.put(Constants.TYPE, Constants.TYPE_LOGIN);
			object.put(Constants.SRC_IP, localIP);
			dos.writeUTF(object.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	// 发送方请求发送文件
	public void sendRequest(JSONObject object) {
		System.out.println("正在发送文件请求");
		ClientGUI.printArea.append("正在发送文件请求 \n");
		try {
			object.put(Constants.TYPE, Constants.TYPE_REQUEST);
			dos.writeUTF(object.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	// 接收方发送回应
	public void sendResponse(JSONObject object, boolean response) {
		System.out.println("正在发送回应");
		ClientGUI.printArea.append("正在发送回应 \n");

		try {
			if (response) {
				int[] port = PortHelper.getAvailablePort(object
						.getString(Constants.SRC_IP));
				object.put(Constants.PORT, port[0]);
				object.put(Constants.BREAKPOINT, 0);
				object.put(Constants.COMPLETE, 0);

				// 如果选中端口处于关闭状态，则开新线程打开端口
				if (port[1] == 0)
					new Thread(new FilePortThread(port[0])).start();
			}

			object.put(Constants.TYPE, Constants.TYPE_RESPONSE);
			object.put(Constants.RESPONSE, response);
			dos.writeUTF(object.toString());

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	// 接收方请求中断文件传送
	public void sendInterupt(JSONObject object) {

		ClientGUI.printArea.append("正在发送中断接收请求 \n");
		try {
			object.put(Constants.TYPE, Constants.TYPE_INTERUPT);
			dos.writeUTF(object.toString());
			System.out.println("正在发送中断接收请求");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	// 接收文件中断，向服务器发送断点信息
	public void sendBreakpoint(JSONObject object, long breakpoint) {
		System.out.println("正在向服务器发送断点信息");
		ClientGUI.printArea.append("正在向服务器发送断点信息\n");
		try {
			object.put(Constants.TYPE, Constants.TYPE_BREAKPOINT);
			object.put(Constants.BREAKPOINT, breakpoint);
			dos.writeUTF(object.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	// 接收方完成文件接收，向服务器发送完成信号
	public void sendComplete(JSONObject object) {
		System.out.println("正在向服务器报告发送文件接收完成信息");
		ClientGUI.printArea.append("正在向服务器报告发送文件接收完成信息\n");
		try {
			object.put(Constants.TYPE, Constants.TYPE_COMPLETE);
			object.put(Constants.COMPLETE, 1);
			object.put(Constants.BREAKPOINT, 0);
			dos.writeUTF(object.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	// 发送方在中断后请求继续发送文件
	public void sendReqSend2(JSONObject object) {
		System.out.println("正在请求继续发送文件");
		ClientGUI.printArea.append("正在请求继续发送文件\n");
		try {
			object.put(Constants.TYPE, Constants.TYPE_REQSEND2);
			dos.writeUTF(object.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	// 接收方回复发送方关于继续发送文件请求
	public void sendRespSend2(JSONObject object, boolean response) {
		System.out.println("正在发送继续传输文件回应");
		ClientGUI.printArea.append("正在发送关于继续发送文件请求的回应\n");
		try {
			if (response) {
				int[] port = PortHelper.getAvailablePort(object
						.getString(Constants.SRC_IP));
				object.put(Constants.PORT, port[0]);
				object.put(Constants.BREAKPOINT, 0);

				// 如果选中端口处于关闭状态，则开新线程打开端口
				if (port[1] == 0)
					new Thread(new FilePortThread(port[0])).start();
			}

			object.put(Constants.TYPE, Constants.TYPE_RESPSEND2);
			object.put(Constants.RESPONSE, response);
			dos.writeUTF(object.toString());

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	// 接收方在中断后请求继续接收文件
	public void sendReqRecv2(JSONObject object) {
		System.out.println("正在请求继续接收文件");
		ClientGUI.printArea.append("正在请求继续接收文件\n");
		try {
			object.put(Constants.TYPE, Constants.TYPE_REQRECV2);
			dos.writeUTF(object.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	// 发送方回复接收方关于继续接收文件的请求
	public void sendRespRecv2(JSONObject object, boolean response) {
		System.out.println("正在发送请求继续接收文件的回复");
		ClientGUI.printArea.append("正在发送请求继续接收文件的回复\n");
		try {
			object.put(Constants.TYPE, Constants.TYPE_RESPRECV2);
			object.put(Constants.RESPONSE, response);
			dos.writeUTF(object.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
