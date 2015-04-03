package helper;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import main.FilePortThread;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SendHelper {

	private DataOutputStream dos;

	public SendHelper(DataOutputStream dos) {
		this.dos = dos;
	}
	
	public void setDos(DataOutputStream dos) {
		this.dos = dos;
	}

	/** �ͻ�������������͵�½����  */
	public void sendLogin(String localIP) {
		System.out.println(localIP + " ���ڷ��͵�½����");
		
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
	
		try {
			if (response) {
				int port = PortHelper.getAvailablePort(object
						.getString(Constants.SRC_IP));
				object.put(Constants.PORT, port);
				object.put(Constants.BREAKPOINT, 0);
				object.put(Constants.COMPLETE, 0);

				new Thread(new FilePortThread(port)).start();
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

	// �����ļ��жϣ�����������Ͷϵ���Ϣ
	public void sendBreakpoint(JSONObject object, long breakpoint) {
		System.out.println("��������������Ͷϵ���Ϣ");
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

	//��ȡ������ʱ�ļ�
	private JSONArray readTmpFile() {
		File folder = new File("breakpoint");
		if(!folder.exists()) 
			return null;
		JSONArray array = new JSONArray();
		File[] tmpFiles = folder.listFiles();
		for (File file : tmpFiles) {
			try {
				FileReader reader = new FileReader(file);
				char[] buf = new char[(int) file.length()];
				reader.read(buf);
				array.put(new JSONObject(new String(buf)));
				reader.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch(IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return array;
	}
	
	//���ն˻ָ��ͷ����������Ӻ�����������Ͷϵ���Ϣ
	public void sendBreakpoints(String srcIP) {
		System.out.println("��������������ͱ��ضϵ��¼");
		
		JSONArray array = readTmpFile();
		JSONObject object = new JSONObject();
		try {
			object.put(Constants.TYPE, Constants.TYPE_BREAKPOINTS);
			object.put(Constants.SRC_IP, srcIP);
			if(array == null || array.length() == 0) {
				object.put(Constants.RECORDS, "");
			} else {
				object.put(Constants.RECORDS, array.toString());
			}
			dos.writeUTF(object.toString());
			deleteFolder();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//ɾ�������ʱ�ļ����ļ���
	private void deleteFolder() {
		File folder = new File("breakpoint");
		if(!folder.exists()) 
			return;
		File[] files = folder.listFiles();
		for (File file : files) {
			file.delete();
		}
		folder.delete();
	}
	
	// ���շ�����ļ����գ����������������ź�
	public void sendComplete(JSONObject object) {
		System.out.println("��������������淢���ļ����������Ϣ");
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
		try {
			if (response) {
				int port = PortHelper.getAvailablePort(object
						.getString(Constants.SRC_IP));
				object.put(Constants.PORT, port);
				object.put(Constants.BREAKPOINT, 0);

				new Thread(new FilePortThread(port)).start();
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
	
	//����ɾ���б��ļ����ݵ���Ϣ
	public void sendDelete(JSONArray array) {
		JSONObject object = new JSONObject();
		System.out.println("���������������ɾ����Ϣ");
		try {
			object.put(Constants.TYPE, Constants.TYPE_DELETE);
			object.put(Constants.RECORDS, array.toString());
			dos.writeUTF(object.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** �����ϴ��ļ����ƶ˵�����  */
	public void sendUpload(File file) {
		System.out.println("����������������ϴ�����");
		JSONObject object = new JSONObject();
		try {
			String fileName = file.getName();
			String filePath = file.getAbsolutePath();
			object.put(Constants.TYPE, Constants.TYPE_UPLOAD);
			object.put(Constants.FILE_NAME, fileName);
			object.put(Constants.SEND_PATH, filePath);
			dos.writeUTF(object.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** ���������ļ�����  */
	public void sendDownload(String fileName, String filePath) {
		System.out.println("�����������������������");
		JSONObject object = new JSONObject();
		try {
			object.put(Constants.TYPE, Constants.TYPE_DOWNLOAD);
			object.put(Constants.FILE_NAME, fileName);
			object.put(Constants.RECV_PATH, filePath);
			dos.writeUTF(object.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** �����ƶ��ļ��б�  */
	public void sendServerFiles() {
		System.out.println("����������������ļ��б�����");
		JSONObject object = new JSONObject();
		try {
			object.put(Constants.TYPE, Constants.TYPE_SERVERFILES);
			dos.writeUTF(object.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
