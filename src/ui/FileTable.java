package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

public class FileTable extends JTable {

	private static final long serialVersionUID = 1L;
	private BaseTableModel model;

	public FileTable(BaseTableModel model, int type) {
		this.model = model;
		setView(type);
	}

	public void setView(int type) {
		// 文件信息表格
		setModel(model);
		setBackground(new Color(228, 237, 254)); // 列表背景颜色
		setSelectionBackground(new Color(228, 237, 254));
		setSelectionForeground(Color.black);
		setRowHeight(30); // fileTable的行高
		setForeground(Color.black); // 列表文字颜色
		setShowHorizontalLines(false);
		setShowVerticalLines(false);
		Font font = new Font("微软雅黑", Font.PLAIN, 13);
		// 设置字体居中
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(JLabel.CENTER);
		// 设置字形
		setFont(font);
		// 丢弃表头
		DefaultTableCellRenderer noHeader = new DefaultTableCellRenderer();
		noHeader.setPreferredSize(new Dimension(0, 0));
		getTableHeader().setDefaultRenderer(noHeader);
		// Table每列的宽度
		TableColumnModel fileTcm = getColumnModel();

		switch (type) {
		case 0:
		case 1:
			fileTcm.getColumn(0).setPreferredWidth(30);
			fileTcm.getColumn(1).setPreferredWidth(140);
			fileTcm.getColumn(2).setPreferredWidth(240);
			fileTcm.getColumn(3).setPreferredWidth(80);
			fileTcm.getColumn(4).setPreferredWidth(180);
			fileTcm.getColumn(5).setPreferredWidth(50);

			fileTcm.getColumn(1).setCellRenderer(renderer);
			fileTcm.getColumn(2).setCellRenderer(renderer);
			fileTcm.getColumn(3).setCellRenderer(renderer);
			fileTcm.getColumn(4).setCellRenderer(renderer);
			fileTcm.getColumn(5).setCellRenderer(renderer);
			break;
		case 2:
			fileTcm.getColumn(0).setPreferredWidth(30);
			fileTcm.getColumn(1).setPreferredWidth(290);
			fileTcm.getColumn(2).setPreferredWidth(170);
			fileTcm.getColumn(3).setPreferredWidth(80);
			fileTcm.getColumn(4).setPreferredWidth(150);

			fileTcm.getColumn(1).setCellRenderer(renderer);
			fileTcm.getColumn(2).setCellRenderer(renderer);
			fileTcm.getColumn(3).setCellRenderer(renderer);
			fileTcm.getColumn(4).setCellRenderer(renderer);
			break;
		default:
			break;
		}

	}
}
