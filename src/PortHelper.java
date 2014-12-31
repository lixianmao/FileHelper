import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PortHelper {

	// ����5�����ö˿ڣ�д��˿������ļ�
	public static void newPortFile(File file) {
		try {
			file.createNewFile();

			JSONArray array = new JSONArray();
			for (int i = 8000; i < 8005; i++) {
				JSONObject object = new JSONObject();
				object.put(Constants.PORT, i);
				object.put(Constants.PORT_CLIENTS, new JSONArray());
				object.put(Constants.PORT_OPEN, false);
				array.put(object);
			}

			FileWriter writer = new FileWriter(file);
			writer.write(array.toString());
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO: handle exception
		}
	}

	/**
	 * ��������ͻ��ˣ����ҿ��ö˿ڽ����ļ����� ���ȷ����Ѿ��򿪵Ķ˿�
	 * 
	 * @param client
	 * @return �˿ںͶ˿ڵĿ���״̬
	 */
	public static int[] getAvailablePort(String client) {
		int port = 0;
		int open = 0;

		File file = new File("port.txt");
		if (!file.exists() || file.length() == 0) {
			newPortFile(file);
			port = 8000;
		} else {
			try {
				FileReader reader = new FileReader(file);
				char[] buf = new char[(int) file.length()];
				reader.read(buf);

				JSONArray array = new JSONArray(new String(buf));
				for (int i = 0; i < array.length(); i++) {
					JSONObject object = array.getJSONObject(i);
					JSONArray clients = object
							.getJSONArray(Constants.PORT_CLIENTS);
					if (!hasClient(clients, client)) {
						if (object.getBoolean(Constants.PORT_OPEN)) {
							port = object.getInt(Constants.PORT);
							open = 1;
							break;
						} else if (port == 0) {
							port = object.getInt(Constants.PORT);
						}
					}
				}

				reader.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		}
		int[] result = { port, open };
		System.out.println("port " + port + " " + open);
		return result;
	}

	// �ж϶˿������ӵĿͻ������Ƿ���ָ��ip������
	private static boolean hasClient(JSONArray array, String ip) {
		for (int i = 0; i < array.length(); i++) {
			try {
				if (ip.equals(array.getString(i)))
					return true;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	// �ڶ˿��ļ��ж�ָ���˿�д�뷢���ߺͽ����ߵ�ip
	public static void writeClientToPort(int port, String client) {
		ClientGUI.printArea.append("���ڽ����Ͷ���Ϣд��˿��ļ�\n");

		File file = new File("port.txt");
		FileReader reader = null;
		FileWriter writer = null;

		try {
			reader = new FileReader(file);
			char[] buf = new char[(int) file.length()];
			reader.read(buf);
			JSONArray array = new JSONArray(new String(buf));

			for (int i = 0; i < array.length(); i++) {

				JSONObject object = array.getJSONObject(i);
				if (object.getInt(Constants.PORT) == port) {
					object.put(Constants.PORT_OPEN, true);
					object.getJSONArray(Constants.PORT_CLIENTS).put(client);
					break;
				}
			}

			writer = new FileWriter(file);
			writer.write(array.toString());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO: handle exception
		} finally {
			try {
				if (reader != null)
					reader.close();
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void removeClientFromPort(int port, String client) {
		ClientGUI.printArea.append("���ڽ����Ͷ˴Ӷ˿��ļ���ɾ��\n");

		File file = new File("port.txt");
		FileReader reader = null;
		FileWriter writer = null;

		try {
			reader = new FileReader(file);
			char[] buf = new char[(int) file.length()];
			reader.read(buf);
			JSONArray array = new JSONArray(new String(buf));

			for (int i = 0; i < array.length(); i++) {

				JSONObject object = array.getJSONObject(i);
				if (object.getInt(Constants.PORT) == port) {
					JSONArray clients = object
							.getJSONArray(Constants.PORT_CLIENTS);
					object.put(Constants.PORT_CLIENTS,
							removeElement(clients, client));
					break;
				}
			}

			writer = new FileWriter(file);
			writer.write(array.toString());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO: handle exception
		} finally {
			try {
				if (reader != null)
					reader.close();
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static JSONArray removeElement(JSONArray array, String str) {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < array.length(); i++) {
			String element;
			try {
				element = array.getString(i);
				if (!element.equals(str)) {
					list.add(element);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return new JSONArray(list);
	}

	// �˿��߳�û�н����ļ������߳�ʱ��Ӧ�ùرն˿��̲߳�д��˿��ļ���
	// TODO
}
