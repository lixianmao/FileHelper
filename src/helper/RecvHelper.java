package helper;

import javax.swing.JOptionPane;

import main.SendFileTask;
import main.ServerFileThread;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ui.GUI_V4;

/**
 * ���������֪ͨ�Ĺ�����
 * 
 * @author Administrator
 *
 */
public class RecvHelper {

	private DataHelper dataHelper;

	public RecvHelper(DataHelper dataHelper) {
		this.dataHelper = dataHelper;
	}

	/**������յ�����������Ϣ  */
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

	// ����������ĵ�½״̬��Ϣ
	private void recvLogin(JSONObject object) {
		System.out.println("���ڽ��յ�½״̬");
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

	// �����ļ�����
	private void recvRequest(JSONObject object) {
		System.out.println("���ڽ����ļ�����");
		try {
			String srcIP = object.getString(Constants.SRC_IP);
			String fileName = object.getString(Constants.FILE_NAME);
			long fileLen = object.getLong(Constants.FILE_LEN);

			// ��ʾ���շ������ļ��ĶԻ���
			int option = JOptionPane.showConfirmDialog(null, "���ԣ�" + srcIP
					+ "\n�ļ�����" + fileName + "\n�ļ���С��" + dataHelper.exFileLen(fileLen), "�ļ�����",
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

	// ���������ļ���Ļ�Ӧ
	private void recvResponse(JSONObject object) {
		System.out.println("���ڽ��ջ�Ӧ");

		try {
			boolean response = object.getBoolean(Constants.RESPONSE);
			if (response) {
				int availablePort = object.getInt(Constants.PORT);
				if (availablePort != 0) {
					SendFileTask sendTask = new SendFileTask(object);
					sendTask.execute();
					
					//��������JSONObject�������˽���·��������
					String fileTime = object.getString(Constants.FILE_TIME);
					int index = GUI_V4.sendTableModel.findRowByTime(fileTime);
					GUI_V4.sendTableModel.setJSON(index, object);
				}
			} else {
				// TODO
				JOptionPane.showMessageDialog(null, "�Է��ܾ����ܣ��ļ���ȡ������");
				GUI_V4.sendTableModel.deleteRow(object.getString(Constants.FILE_TIME));
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ���ͷ����յ����շ��Ĺ��ڼ��������ļ��Ļظ�
	private void recvRespSend2(JSONObject object) {
		System.out.println("���ڽ��չ��ڼ��������ļ��Ļ�Ӧ");

		try {
			boolean response = object.getBoolean(Constants.RESPONSE);

			if (response) {
				int availablePort = object.getInt(Constants.PORT);
				if(availablePort != 0) {
					SendFileTask task = new SendFileTask(object);
					task.execute();
				} else {
					JOptionPane.showMessageDialog(null, "���շ���æ����ʱ�޿��ö˿�");
				}
			} else {
				JOptionPane.showMessageDialog(null, "�Է��ܾ����ܣ��ļ���ȡ������");
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ���շ��յ����ͷ������ļ��������͵�����
	private void recvReqSend2(JSONObject object) {
		System.out.println("���ڽ��ռ��������ļ�����");

		try {
			String srcIP = object.getString(Constants.SRC_IP);
			String fileName = object.getString(Constants.FILE_NAME);
			long fileLen = object.getLong(Constants.FILE_LEN);
			
			int option = JOptionPane.showConfirmDialog(null, "���ͷ���" + srcIP
					+ "\n�ļ�����" + fileName + "\n�ļ���С��" + dataHelper.exFileLen(fileLen), "�Ƿ��������",
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

	// ���ͷ��յ����շ����ڼ��������ļ�������
	private void recvReqRecv2(JSONObject object) {
		System.out.println("���ڽ��ռ��������ļ�����");
		
		try {
			String destIP = object.getString(Constants.DEST_IP);
			String fileName = object.getString(Constants.FILE_NAME);
			long fileLen = object.getLong(Constants.FILE_LEN);
			
			int option = JOptionPane.showConfirmDialog(null, "���շ���" + destIP
					+ "\n�ļ�����" + fileName + "\n�ļ���С��" + dataHelper.exFileLen(fileLen), "�Ƿ��������",
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

	// ���ܷ��յ����������ļ��Ļظ�
	private void recvRespRecv2(JSONObject object) {
		try {
			if (object.getBoolean(Constants.RESPONSE)) {
				int port = PortHelper.getAvailablePort(object.getString(Constants.SRC_IP));
				object.put(Constants.PORT, port);
				
				// ����ȷ����Ϣ�Ͷ˿���Ϣ�����ͷ�
				dataHelper.getSendHelper().sendRespSend2(object, true);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** �����б��ļ���ʷ��¼  */
	private void recvRecords(JSONObject object) {
		System.out.println("���ڽ����ļ���¼");
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
	
	/** �����û��б���Ϣ  */
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
	
	/** �����ƶ��ļ��б���Ϣ  */
	private void recvServerFiles(JSONObject object) {
		System.out.println("���ڽ����ƶ��ļ��б�");
		try {
			String files = object.getString(Constants.RECORDS);
			JSONArray array = new JSONArray(files);
			dataHelper.setServerFilesList(array);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** �����ϴ����ػظ�  */
	private void recvLoad(JSONObject object) {
		System.out.println("���ڽ����ϴ����ػظ�");
		new Thread(new ServerFileThread(object)).start();
	}
	
}
