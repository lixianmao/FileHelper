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
		System.out.println(localIP + " ���ڷ��͵�½����");
		ClientGUI.printArea.append("���ڷ��͵�½���� \n");
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

	// ���ͷ��������ļ�
	public void sendRequest(JSONObject object) {
		System.out.println("���ڷ����ļ�����");
		ClientGUI.printArea.append("���ڷ����ļ����� \n");
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

	// ���շ����ͻ�Ӧ
	public void sendResponse(JSONObject object, boolean response) {
		System.out.println("���ڷ��ͻ�Ӧ");
		ClientGUI.printArea.append("���ڷ��ͻ�Ӧ \n");

		try {
			if (response) {
				int[] port = PortHelper.getAvailablePort(object
						.getString(Constants.SRC_IP));
				object.put(Constants.PORT, port[0]);
				object.put(Constants.BREAKPOINT, 0);
				object.put(Constants.COMPLETE, 0);

				// ���ѡ�ж˿ڴ��ڹر�״̬�������̴߳򿪶˿�
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

	// ���շ������ж��ļ�����
	public void sendInterupt(JSONObject object) {

		ClientGUI.printArea.append("���ڷ����жϽ������� \n");
		try {
			object.put(Constants.TYPE, Constants.TYPE_INTERUPT);
			dos.writeUTF(object.toString());
			System.out.println("���ڷ����жϽ�������");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	// �����ļ��жϣ�����������Ͷϵ���Ϣ
	public void sendBreakpoint(JSONObject object, long breakpoint) {
		System.out.println("��������������Ͷϵ���Ϣ");
		ClientGUI.printArea.append("��������������Ͷϵ���Ϣ\n");
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

	// ���շ�����ļ����գ����������������ź�
	public void sendComplete(JSONObject object) {
		System.out.println("��������������淢���ļ����������Ϣ");
		ClientGUI.printArea.append("��������������淢���ļ����������Ϣ\n");
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

	// ���ͷ����жϺ�������������ļ�
	public void sendReqSend2(JSONObject object) {
		System.out.println("����������������ļ�");
		ClientGUI.printArea.append("����������������ļ�\n");
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

	// ���շ��ظ����ͷ����ڼ��������ļ�����
	public void sendRespSend2(JSONObject object, boolean response) {
		System.out.println("���ڷ��ͼ��������ļ���Ӧ");
		ClientGUI.printArea.append("���ڷ��͹��ڼ��������ļ�����Ļ�Ӧ\n");
		try {
			if (response) {
				int[] port = PortHelper.getAvailablePort(object
						.getString(Constants.SRC_IP));
				object.put(Constants.PORT, port[0]);
				object.put(Constants.BREAKPOINT, 0);

				// ���ѡ�ж˿ڴ��ڹر�״̬�������̴߳򿪶˿�
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

	// ���շ����жϺ�������������ļ�
	public void sendReqRecv2(JSONObject object) {
		System.out.println("����������������ļ�");
		ClientGUI.printArea.append("����������������ļ�\n");
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

	// ���ͷ��ظ����շ����ڼ��������ļ�������
	public void sendRespRecv2(JSONObject object, boolean response) {
		System.out.println("���ڷ���������������ļ��Ļظ�");
		ClientGUI.printArea.append("���ڷ���������������ļ��Ļظ�\n");
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
