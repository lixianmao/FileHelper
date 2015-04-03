package helper;

import javax.swing.JOptionPane;

import main.SendFileTask;
import main.ServerFileThread;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ui.GUI_V4;

/**
 * 处理服务器通知的工具类
 * 
 * @author Administrator
 *
 */
public class RecvHelper {

	private DataHelper dataHelper;

	public RecvHelper(DataHelper dataHelper) {
		this.dataHelper = dataHelper;
	}

	/**处理接收到服务器的消息  */
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
				break;
			case Constants.TYPE_RECORD:
				recvRecords(object);
				break;
			case Constants.TYPE_USERS:
				recvUsers(object);
				break;
			case Constants.TYPE_SERVERFILES:
				recvServerFiles(object);
				break;
			case Constants.TYPE_UPLOAD:
			case Constants.TYPE_DOWNLOAD:
				recvLoad(object);
				break;
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
		try {
			boolean response = object.getBoolean(Constants.RESPONSE);
			dataHelper.setRegisterLable(response);
			if (response) {
				dataHelper.getSendHelper().sendBreakpoints(object.getString(Constants.SRC_IP));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// 处理文件请求
	private void recvRequest(JSONObject object) {
		System.out.println("正在接收文件请求");
		try {
			String srcIP = object.getString(Constants.SRC_IP);
			String fileName = object.getString(Constants.FILE_NAME);
			long fileLen = object.getLong(Constants.FILE_LEN);

			// 提示接收方接收文件的对话框
			int option = JOptionPane.showConfirmDialog(null, "来自：" + srcIP
					+ "\n文件名：" + fileName + "\n文件大小：" + dataHelper.exFileLen(fileLen), "文件请求",
					JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				boolean PathSave = dataHelper.selectPathToSave();
				if (PathSave){
					object = dataHelper.addRecvObject(object);
					dataHelper.getSendHelper().sendResponse(object, true);
					GUI_V4.recvTableModel.addRow(object);
				}else{
					dataHelper.getSendHelper().sendResponse(object, false);
				}
			} else if (option == JOptionPane.NO_OPTION) {
				dataHelper.getSendHelper().sendResponse(object, false);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	}

	// 处理请求文件后的回应
	private void recvResponse(JSONObject object) {
		System.out.println("正在接收回应");

		try {
			boolean response = object.getBoolean(Constants.RESPONSE);
			if (response) {
				int availablePort = object.getInt(Constants.PORT);
				if (availablePort != 0) {
					SendFileTask sendTask = new SendFileTask(object);
					sendTask.execute();
					
					//更新行内JSONObject，包含了接收路径等内容
					String fileTime = object.getString(Constants.FILE_TIME);
					int index = GUI_V4.sendTableModel.findRowByTime(fileTime);
					GUI_V4.sendTableModel.setJSON(index, object);
				}
			} else {
				// TODO
				JOptionPane.showMessageDialog(null, "对方拒绝接受，文件已取消发送");
				GUI_V4.sendTableModel.deleteRow(object.getString(Constants.FILE_TIME));
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 发送方接收到接收方的关于继续发送文件的回复
	private void recvRespSend2(JSONObject object) {
		System.out.println("正在接收关于继续发送文件的回应");

		try {
			boolean response = object.getBoolean(Constants.RESPONSE);

			if (response) {
				int availablePort = object.getInt(Constants.PORT);
				if(availablePort != 0) {
					SendFileTask task = new SendFileTask(object);
					task.execute();
				} else {
					JOptionPane.showMessageDialog(null, "接收方繁忙，暂时无可用端口");
				}
			} else {
				JOptionPane.showMessageDialog(null, "对方拒绝接受，文件已取消发送");
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 接收方收到发送方关于文件继续发送的请求
	private void recvReqSend2(JSONObject object) {
		System.out.println("正在接收继续发送文件请求");

		try {
			String srcIP = object.getString(Constants.SRC_IP);
			String fileName = object.getString(Constants.FILE_NAME);
			long fileLen = object.getLong(Constants.FILE_LEN);
			
			int option = JOptionPane.showConfirmDialog(null, "发送方：" + srcIP
					+ "\n文件名：" + fileName + "\n文件大小：" + dataHelper.exFileLen(fileLen), "是否继续接收",
					JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				dataHelper.getSendHelper().sendRespSend2(object, true);
			} else if (option == JOptionPane.NO_OPTION) {
				dataHelper.getSendHelper().sendRespSend2(object, false);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 发送方收到接收方关于继续接收文件的请求
	private void recvReqRecv2(JSONObject object) {
		System.out.println("正在接收继续接收文件请求");
		
		try {
			String destIP = object.getString(Constants.DEST_IP);
			String fileName = object.getString(Constants.FILE_NAME);
			long fileLen = object.getLong(Constants.FILE_LEN);
			
			int option = JOptionPane.showConfirmDialog(null, "接收方：" + destIP
					+ "\n文件名：" + fileName + "\n文件大小：" + dataHelper.exFileLen(fileLen), "是否继续发送",
					JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				dataHelper.getSendHelper().sendRespRecv2(object, true);
			} else if (option == JOptionPane.NO_OPTION) {
				dataHelper.getSendHelper().sendRespRecv2(object, false);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 接受方收到继续接收文件的回复
	private void recvRespRecv2(JSONObject object) {
		try {
			if (object.getBoolean(Constants.RESPONSE)) {
				int port = PortHelper.getAvailablePort(object.getString(Constants.SRC_IP));
				object.put(Constants.PORT, port);
				
				// 发送确认消息和端口信息给发送方
				dataHelper.getSendHelper().sendRespSend2(object, true);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** 接收列表文件历史记录  */
	private void recvRecords(JSONObject object) {
		System.out.println("正在接收文件记录");
		try {
			String records = object.getString(Constants.RECORDS);
			JSONArray recordsArray = new JSONArray(records);
			JSONArray sendArray = recordsArray.getJSONArray(0);
			JSONArray recvArray = recordsArray.getJSONArray(1);
			JSONArray doneArray = recordsArray.getJSONArray(2);
			
			GUI_V4.sendTableModel.deleteAll();
			GUI_V4.recvTableModel.deleteAll();
			GUI_V4.doneTableModel.deleteAll();
			
			GUI_V4.sendTableModel.addRows(sendArray);
			GUI_V4.recvTableModel.addRows(recvArray);
			GUI_V4.doneTableModel.addRows(doneArray);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** 接收用户列表信息  */
	private void recvUsers(JSONObject object) {
		try {
			String str = object.getString(Constants.USER_LIST);
			JSONArray array = new JSONArray(str);
			dataHelper.setUserList(array);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** 接收云端文件列表信息  */
	private void recvServerFiles(JSONObject object) {
		System.out.println("正在接收云端文件列表");
		try {
			String files = object.getString(Constants.RECORDS);
			JSONArray array = new JSONArray(files);
			dataHelper.setServerFilesList(array);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** 接收上传下载回复  */
	private void recvLoad(JSONObject object) {
		System.out.println("正在接收上传下载回复");
		new Thread(new ServerFileThread(object)).start();
	}
	
}
