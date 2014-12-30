import org.json.JSONException;
import org.json.JSONObject;

/**
 * 处理服务器通知的工具类
 * 
 * @author Administrator
 *
 */
public class RecvHelper {

	private ClientGUI client;

	public RecvHelper(ClientGUI client) {
		this.client = client;
	}

	public synchronized void handleMessage(String msg) {
		System.out.println("RecvHelper: " + Thread.currentThread());
		try {
			JSONObject object = new JSONObject(msg);
			int type = object.getInt(Constants.TYPE);
			switch (type) {
			case Constants.TYPE_LOGIN:
				recvLogin(object);
				break;
			case Constants.TYPE_REQUEST:
				recvRequest(object);
				break;
			case Constants.TYPE_RESPONSE:
				recvResponse(object);
				break;
			case Constants.TYPE_INTERUPT:
				System.out.println("收到中断");
				recvInterupt(object);
				break;
			case Constants.TYPE_REQSEND2:
				recvReqSend2(object);
				break;
			case Constants.TYPE_RESPSEND2:
				recvRespSend2(object);
				break;
			case Constants.TYPE_RESPRECV2:
				recvRespRecv2(object);
				break;
			case Constants.TYPE_REQRECV2:
				recvReqRecv2(object);
			default:
				break;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 处理服务器的登陆状态消息
	private void recvLogin(JSONObject object) {
		System.out.println("正在接收登陆状态");
		ClientGUI.printArea.append("正在接收登陆信息\n");
		try {
			boolean response = object.getBoolean(Constants.RESPONSE);
			if (response) {
				client.labelLogin.setText("注册成功");
			} else {
				client.labelLogin.setText("用户名已存在");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 处理文件请求
	private void recvRequest(JSONObject object) {
		System.out.println("正在接收文件请求");
		ClientGUI.printArea.append("正在接收文件请求\n");
		try {
			String srcIP = object.getString(Constants.SRC_IP);
			String destIP = object.getString(Constants.DEST_IP);
			String fileName = object.getString(Constants.FILE_NAME);
			long fileLen = object.getLong(Constants.FILE_LEN);

			if (client.getLocalIP().equals(destIP)) {
				client.taRecvRequest.setText("收到来自 " + srcIP + " 的文件请求："
						+ fileName + " " + fileLen / 1024 + "kb");
				client.recvObjects.add(object);
			} else {
				client.taRecvRequest.setText("请求错误，目的主机与本机不符");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 处理请求文件后的回应
	private void recvResponse(JSONObject object) {
		System.out.println("正在接收回应");
		ClientGUI.printArea.append("正在接收关于文件请求的回应\n");
		
		try {
			String srcIP = object.getString(Constants.SRC_IP);
			// String destIP = object.getString(Constants.DEST_IP);
			boolean response = object.getBoolean(Constants.RESPONSE);

			if (client.getLocalIP().equals(srcIP)) {
				if (response) {
					ClientGUI.taSendInfo.setText("文件开始发送");

					int availablePort = object.getInt(Constants.PORT);
					if (availablePort != 0) {
						// 启动文件发送线程
						SendFileThread sendThread = new SendFileThread(object);
						client.sendThreads.add(sendThread);
						new Thread(sendThread).start();
					} else {
						ClientGUI.taSendInfo.setText("接收方繁忙，暂时无可用端口");
					}

				} else {
					ClientGUI.taSendInfo.setText("对方拒绝接受，文件已取消发送");
				}
			} else {
				ClientGUI.taSendInfo.setText("回应错误，目的主机与本机不符");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 发送方收到并处理接收方的请求中断信号
	private void recvInterupt(JSONObject object) {
		//ClientGUI.taSendInfo.setText("对方请求中断文件发送");
		ClientGUI.printArea.append("文件中断\n");
		client.sendThreads.get(0).interupt();
	}

	// 发送方接收到接收方的关于继续发送文件的回复
	private void recvRespSend2(JSONObject object) {
		System.out.println("正在接收关于继续发送文件的回应");
		ClientGUI.printArea.append("正在接收关于继续发送文件的回复\n");
		
		try {
			String srcIP = object.getString(Constants.SRC_IP);
			boolean response = object.getBoolean(Constants.RESPONSE);

			if (client.getLocalIP().equals(srcIP)) {
				if (response) {
					ClientGUI.taSendInfo.setText("文件开始发送");
					
					int availablePort = object.getInt(Constants.PORT);
					if (availablePort != 0) {
						// 启动文件发送线程
						SendFileThread sendThread = new SendFileThread(object);
						client.sendThreads.add(sendThread);
						new Thread(sendThread).start();
					} else {
						ClientGUI.taSendInfo.setText("接收方繁忙，暂时无可用端口");
					}

				} else {
					ClientGUI.taSendInfo.setText("对方拒绝接受，文件已取消发送");
				}
			} else {
				ClientGUI.taSendInfo.setText("回应错误，目的主机与本机不符");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//接收方收到发送方关于文件继续发送的请求
	private void recvReqSend2(JSONObject object) {
		System.out.println("正在接收继续发送文件请求");
		ClientGUI.printArea.append("正在接收继续发送文件的请求\n");
		ClientGUI.recvState = 1;
		
		try {
			String srcIP = object.getString(Constants.SRC_IP);
			String destIP = object.getString(Constants.DEST_IP);
			String fileName = object.getString(Constants.FILE_NAME);
			long fileLen = object.getLong(Constants.FILE_LEN);

			if (client.getLocalIP().equals(destIP)) {
				client.taRecvRequest.setText("收到来自 " + srcIP + " 的文件请求："
						+ fileName + " " + fileLen / 1024 + "kb");
				//client.recvObjects.add(object);
			} else {
				client.taRecvRequest.setText("请求错误，目的主机与本机不符");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//发送方收到接收方关于继续接收文件的请求
	private void recvReqRecv2(JSONObject object) {
		System.out.println("正在接收继续接收文件请求");
		ClientGUI.printArea.append("正在接收继续接收文件的请求\n");
		ClientGUI.recvState = 2;
		client.sendObjects.set(0, object);
	}
	
	//接受方收到继续接收文件的回复
	private void recvRespRecv2(JSONObject object) {
		ClientGUI.printArea.append("正在接收关于继续接收文件的回复\n");
		
		try {
			if(object.getBoolean(Constants.RESPONSE)) {
				int[] port = PortHelper.getAvailablePort(object
						.getString(Constants.SRC_IP));
				object.put(Constants.PORT, port[0]);

				//发送确认消息和端口信息给发送方
				ClientGUI.sendHelper.sendRespSend2(object, true);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
