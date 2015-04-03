package ui;

import helper.Constants;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.SwingWorker;
import javax.swing.table.AbstractTableModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BaseTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private List<Object[]> list = new ArrayList<Object[]>();
	private List<JSONObject> JSONList = new ArrayList<JSONObject>();
	// �߳��б����б��ÿһ�к�һ���߳�ӳ������
	private List<SwingWorker<Long, Long>> taskList = new ArrayList<SwingWorker<Long, Long>>();

	// �������
	private int tableType;
	private String[] titles;
	private String[][] titlesArray = {
			{ "�Ƿ�ѡ��", "Ŀ��IP", "�ļ���", "�ļ���С", "���ʱ��", "����" },
			{ "�Ƿ�ѡ��", "ԴIP", "�ļ���", "�ļ���С", "���ʱ��", "����" },
			{ "�Ƿ�ѡ��", "״̬", "�ļ���", "�ļ���С", "���ʱ��" } };

	public BaseTableModel(int tableType) {
		this.tableType = tableType;
		titles = titlesArray[tableType];
	}

	public List<Object[]> getList() {
		return list;
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return titles.length;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return (list.size() > 0) ? list.size() : 0;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		if (list.size() > rowIndex) {
			return list.get(rowIndex)[columnIndex];
		}
		return null;
	}

	@Override
	public String getColumnName(int arg0) {
		// TODO Auto-generated method stub
		return titles[arg0];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		// TODO Auto-generated method stub
		return getValueAt(0, columnIndex).getClass();
	}

	public void addRow(JSONObject object) {
		JSONList.add(object);
		Object[] row = convertToRow(object);
		list.add(row);
		taskList.add(null);
		fireTableDataChanged();
	}

	public void addRows(JSONArray array) {
		if (array.length() < 1) {
			return;
		}
		for (int i = 0; i < array.length(); i++) {
			try {
				JSONObject object = array.getJSONObject(i);
				JSONList.add(object);
				Object[] row = convertToRow(object);
				list.add(row);
				taskList.add(null);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		fireTableDataChanged();
	}

	/** ͨ�����ʱ���ҵ�������  */
	public int findRowByTime(String fileTime) {
		for (int i = 0; i < list.size(); i++) {
			Object[] row = list.get(i);
			if (fileTime.equals(row[4])) {
				return i;
			}
		}
		return -1;
	}

	/** ˢ���������ļ����ͽ���  */
	public void setRowProgress(String time, String progress) {
		int rowIndex = findRowByTime(time);
		if (rowIndex != -1) {
			setValueAt(progress, rowIndex, 5);
			fireTableCellUpdated(rowIndex, 5);
		}
	}

	/** ����ʱ��ɾ��������  */
	public void deleteRow(String time) {
		int rowIndex = findRowByTime(time);
		if (rowIndex != -1) {
			list.remove(rowIndex);
			JSONList.remove(rowIndex);
			taskList.remove(rowIndex);
			fireTableRowsDeleted(rowIndex, rowIndex + 1);
		}
	}
	
	/** �����±�ɾ������  */
	public void deleteRows(int[] rows) {
		System.out.println("delete rows:" + rows.length);
		if(tableType != 2)
			return ;
		int hasDeleted = 0;
		for(int row: rows) {
			row = row - hasDeleted;
			list.remove(row);
			JSONList.remove(row);
			taskList.remove(row);
			hasDeleted++;
		}
		fireTableDataChanged();
	}
	
	/** �����±��ȡ�ж�Ӧ���ļ���Ϣ  */
	public JSONArray getSelectedObjects(int[] rows) {
		JSONArray array = new JSONArray();
		for (int row : rows) {
			array.put(JSONList.get(row));
		}
		return array;
	}
	
	/** ת��Ϊ������ʾ�ļ���Ϣ  */
	private Object[] convertToRow(JSONObject object) {
		try {
			String fileName = object.getString(Constants.FILE_NAME);
			long filelen = object.getLong(Constants.FILE_LEN);
			String exFileLen = exFileLen(filelen);
			String srcIP = object.getString(Constants.SRC_IP);
			String destIP = object.getString(Constants.DEST_IP);
			String fileTime = object.getString(Constants.FILE_TIME);
			
			if (tableType == 0) {
				long breakpoint = object.getLong(Constants.BREAKPOINT);
				Object[] row = { new Boolean(false), destIP, fileName,
						exFileLen, fileTime, breakpoint * 100 / filelen + "%" };
				return row;
			} else if (tableType == 1) {
				long breakpoint = object.getLong(Constants.BREAKPOINT);
				Object[] row = { new Boolean(false), srcIP, fileName,
						exFileLen, fileTime, breakpoint * 100 / filelen + "%" };
				return row;
			} else {
				Object[] row = { new Boolean(false), srcIP + " - " + destIP, fileName, exFileLen,
						fileTime };
				return row;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void updateRow(int index, JSONObject object) {
		Object[] row = convertToRow(object);
		list.set(index, row);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		list.get(rowIndex)[columnIndex] = aValue;
		fireTableDataChanged();
	}

	private String exFileLen(long fileLen) {
		String exrecentfileLength = "";
		DecimalFormat df = new DecimalFormat("#.00");
		if (fileLen < 1024) {
			exrecentfileLength = fileLen + "B";
		} else if (fileLen < 1048576) {
			exrecentfileLength = df.format(fileLen / 1024.0) + "KB";
		} else if (fileLen < 1073741824) {
			exrecentfileLength = df.format(fileLen / 1048576.0) + "MB";
		} else {
			exrecentfileLength = df.format(fileLen / 1073741824.0) + "GB";
		}
		return exrecentfileLength;
	}

	public void setTask(int index, SwingWorker<Long, Long> task) {
		if (taskList.size() > index) {
			taskList.set(index, task);
		}
	}

	public List<SwingWorker<Long, Long>> getTaskList() {
		return taskList;
	}

	public List<JSONObject> getJSONList() {
		return JSONList;
	}

	public void setJSON(int index, JSONObject object) {
		if (JSONList.size() > index) {
			JSONList.set(index, object);
		}
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// return columnIndex == 0;
		return (getValueAt(rowIndex, columnIndex) instanceof Boolean);
	}

	public int[] getSelectedRows() {
		int[] rows = new int[list.size()];
		int count = 0;
		for(int i = 0; i < list.size(); i++) {
			if((boolean)getValueAt(i, 0)) {
				rows[count] = i;
				count++;
			}
		}
		return Arrays.copyOf(rows, count);
	}
	
	/** ɾ��������  */
	public void deleteAll() {
		list.removeAll(list);
		taskList.removeAll(taskList);
		JSONList.removeAll(JSONList);
		fireTableDataChanged();
	}
	
}
