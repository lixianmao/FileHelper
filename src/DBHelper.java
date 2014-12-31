import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONException;
import org.json.JSONObject;

public class DBHelper {

	// 驱动程序名
	private String driver = "org.sqlite.JDBC";
	// url指向要访问的数据库名
	private String url = "jdbc:sqlite:FileRecord.db";
	// 创建表的命令
	private String sqlCreate = "create table if not exists RECORD "
			+ "(_id integer primary key autoincrement, "
			+ "fileName nvarchar(20) not null, " + "fileLen integer not null, "
			+ "srcIP char(15) not null, " + "destIP char(15) not null, "
			+ "complete integer, " + "breakpoint integer);";
	// 连接到数据库
	private Connection conn;
	// 数据库操作命令
	private Statement stat;

	public DBHelper() {
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url);
			stat = conn.createStatement();
			stat.executeUpdate(sqlCreate);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void insert(JSONObject object) {
		try {
			String fileName = object.getString(Constants.FILE_NAME);
			long fileLen = object.getLong(Constants.FILE_LEN);
			String srcIP = object.getString(Constants.SRC_IP);
			String destIP = object.getString(Constants.DEST_IP);

			String sqlInsert = "insert into RECORD(fileName, fileLen, srcIP, destIP, complete, breakpoint) values ('"
					+ fileName
					+ "', "
					+ fileLen
					+ ", '"
					+ srcIP
					+ "', '"
					+ destIP + "', 0, 0)";
			stat.executeUpdate(sqlInsert);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		// 关闭数据库连接
		close();
	}

	public void update(JSONObject object) {
		try {
			long breakpoint = object.getLong(Constants.BREAKPOINT);
			int complete = object.getInt(Constants.COMPLETE);
			String fileName = object.getString(Constants.FILE_NAME);
			String srcIP = object.getString(Constants.SRC_IP);
			String destIP = object.getString(Constants.DEST_IP);

			String sqlUpdate = "update RECORD set breakpoint = " + breakpoint
					+ ", complete = " + complete + " where fileName = '"
					+ fileName + "' and srcIP = '" + srcIP + "' and destIP = '"
					+ destIP + "'";
			;
			stat.executeUpdate(sqlUpdate);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		// 关闭数据库连接
		close();
	}

	public void delete(JSONObject object) {

		try {
			String fileName = object.getString(Constants.FILE_NAME);
			String srcIP = object.getString(Constants.SRC_IP);
			String destIP = object.getString(Constants.DEST_IP);

			String sqlDelete = "delete from RECORD where fileName = "
					+ fileName + " and srcIP = " + srcIP + " and destIP = "
					+ destIP;
			;
			stat.executeUpdate(sqlDelete);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		// 关闭数据库连接
		close();
	}

	public long select(JSONObject object) {
		long breakpoint = 0;
		try {
			String fileName = object.getString(Constants.FILE_NAME);
			String srcIP = object.getString(Constants.SRC_IP);
			String destIP = object.getString(Constants.DEST_IP);
			String sqlQuery = "select breakpoint from RECORD where fileName = '"
					+ fileName + "' and srcIP = '" + srcIP + "' and destIP = '"
					+ destIP + "' and complete = 0" + " order by _id";
			;

			ResultSet rs = stat.executeQuery(sqlQuery);
			rs.next();
			breakpoint = rs.getLong("breakpoint");
			rs.close();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		// 关闭数据库连接
		close();
		return breakpoint;
	}

	private void close() {

		try {
			if (conn != null)
				conn.close();
			if (stat != null)
				stat.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
