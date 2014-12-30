import org.json.JSONException;
import org.json.JSONObject;

/**
 * ���������֪ͨ�Ĺ�����
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
				System.out.println("�յ��ж�");
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

	// ����������ĵ�½״̬��Ϣ
	private void recvLogin(JSONObject object) {
		System.out.println("���ڽ��յ�½״̬");
		ClientGUI.printArea.append("���ڽ��յ�½��Ϣ\n");
		try {
			boolean response = object.getBoolean(Constants.RESPONSE);
			if (response) {
				client.labelLogin.setText("ע��ɹ�");
			} else {
				client.labelLogin.setText("�û����Ѵ���");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// �����ļ�����
	private void recvRequest(JSONObject object) {
		System.out.println("���ڽ����ļ�����");
		ClientGUI.printArea.append("���ڽ����ļ�����\n");
		try {
			String srcIP = object.getString(Constants.SRC_IP);
			String destIP = object.getString(Constants.DEST_IP);
			String fileName = object.getString(Constants.FILE_NAME);
			long fileLen = object.getLong(Constants.FILE_LEN);

			if (client.getLocalIP().equals(destIP)) {
				client.taRecvRequest.setText("�յ����� " + srcIP + " ���ļ�����"
						+ fileName + " " + fileLen / 1024 + "kb");
				client.recvObjects.add(object);
			} else {
				client.taRecvRequest.setText("�������Ŀ�������뱾������");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ���������ļ���Ļ�Ӧ
	private void recvResponse(JSONObject object) {
		System.out.println("���ڽ��ջ�Ӧ");
		ClientGUI.printArea.append("���ڽ��չ����ļ�����Ļ�Ӧ\n");
		
		try {
			String srcIP = object.getString(Constants.SRC_IP);
			// String destIP = object.getString(Constants.DEST_IP);
			boolean response = object.getBoolean(Constants.RESPONSE);

			if (client.getLocalIP().equals(srcIP)) {
				if (response) {
					ClientGUI.taSendInfo.setText("�ļ���ʼ����");

					int availablePort = object.getInt(Constants.PORT);
					if (availablePort != 0) {
						// �����ļ������߳�
						SendFileThread sendThread = new SendFileThread(object);
						client.sendThreads.add(sendThread);
						new Thread(sendThread).start();
					} else {
						ClientGUI.taSendInfo.setText("���շ���æ����ʱ�޿��ö˿�");
					}

				} else {
					ClientGUI.taSendInfo.setText("�Է��ܾ����ܣ��ļ���ȡ������");
				}
			} else {
				ClientGUI.taSendInfo.setText("��Ӧ����Ŀ�������뱾������");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ���ͷ��յ���������շ��������ж��ź�
	private void recvInterupt(JSONObject object) {
		//ClientGUI.taSendInfo.setText("�Է������ж��ļ�����");
		ClientGUI.printArea.append("�ļ��ж�\n");
		client.sendThreads.get(0).interupt();
	}

	// ���ͷ����յ����շ��Ĺ��ڼ��������ļ��Ļظ�
	private void recvRespSend2(JSONObject object) {
		System.out.println("���ڽ��չ��ڼ��������ļ��Ļ�Ӧ");
		ClientGUI.printArea.append("���ڽ��չ��ڼ��������ļ��Ļظ�\n");
		
		try {
			String srcIP = object.getString(Constants.SRC_IP);
			boolean response = object.getBoolean(Constants.RESPONSE);

			if (client.getLocalIP().equals(srcIP)) {
				if (response) {
					ClientGUI.taSendInfo.setText("�ļ���ʼ����");
					
					int availablePort = object.getInt(Constants.PORT);
					if (availablePort != 0) {
						// �����ļ������߳�
						SendFileThread sendThread = new SendFileThread(object);
						client.sendThreads.add(sendThread);
						new Thread(sendThread).start();
					} else {
						ClientGUI.taSendInfo.setText("���շ���æ����ʱ�޿��ö˿�");
					}

				} else {
					ClientGUI.taSendInfo.setText("�Է��ܾ����ܣ��ļ���ȡ������");
				}
			} else {
				ClientGUI.taSendInfo.setText("��Ӧ����Ŀ�������뱾������");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//���շ��յ����ͷ������ļ��������͵�����
	private void recvReqSend2(JSONObject object) {
		System.out.println("���ڽ��ռ��������ļ�����");
		ClientGUI.printArea.append("���ڽ��ռ��������ļ�������\n");
		ClientGUI.recvState = 1;
		
		try {
			String srcIP = object.getString(Constants.SRC_IP);
			String destIP = object.getString(Constants.DEST_IP);
			String fileName = object.getString(Constants.FILE_NAME);
			long fileLen = object.getLong(Constants.FILE_LEN);

			if (client.getLocalIP().equals(destIP)) {
				client.taRecvRequest.setText("�յ����� " + srcIP + " ���ļ�����"
						+ fileName + " " + fileLen / 1024 + "kb");
				//client.recvObjects.add(object);
			} else {
				client.taRecvRequest.setText("�������Ŀ�������뱾������");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//���ͷ��յ����շ����ڼ��������ļ�������
	private void recvReqRecv2(JSONObject object) {
		System.out.println("���ڽ��ռ��������ļ�����");
		ClientGUI.printArea.append("���ڽ��ռ��������ļ�������\n");
		ClientGUI.recvState = 2;
		client.sendObjects.set(0, object);
	}
	
	//���ܷ��յ����������ļ��Ļظ�
	private void recvRespRecv2(JSONObject object) {
		ClientGUI.printArea.append("���ڽ��չ��ڼ��������ļ��Ļظ�\n");
		
		try {
			if(object.getBoolean(Constants.RESPONSE)) {
				int[] port = PortHelper.getAvailablePort(object
						.getString(Constants.SRC_IP));
				object.put(Constants.PORT, port[0]);

				//����ȷ����Ϣ�Ͷ˿���Ϣ�����ͷ�
				ClientGUI.sendHelper.sendRespSend2(object, true);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
