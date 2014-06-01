import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.PageAttributes;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.Pageable;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.lowagie.text.DocumentException;


public class GUI extends JFrame {
	
	
	private JTabbedPane tabPane;

	public GUI() {
		// TODO Auto-generated constructor stub
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();	
		setLocation(size.width/4, size.height/4);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initPanel();
		pack();
	}
	
	private void initPanel(){
		tabPane = new JTabbedPane();
		tabPane.addTab("DB", new MainPanel());
		
		add(tabPane);
	}
	
	private class MainPanel extends JPanel {
		DBHelper helper = new DBHelper();
		Button btnAdd = new Button("add"); 
		Button btnPdf = new Button("create PDF");
		Button btnExcel = new Button("create Excel");
		private JPanel tablePanel;
		public MainPanel() {
			// TODO Auto-generated constructor stub
			setLayout(new BorderLayout());
			JPanel btnPanel = new JPanel();
			btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
			btnAdd.addActionListener(myAction);
			btnExcel.addActionListener(myAction);
			btnPdf.addActionListener(myAction);
			btnPanel.add(btnAdd);
			btnPanel.add(btnExcel);
			btnPanel.add(btnPdf);
			add(btnPanel, BorderLayout.EAST);
			tablePanel = new JPanel();
			try {
				tablePanel.add(addTable());
			} catch (SQLException e) {e.printStackTrace();}
			add(tablePanel, BorderLayout.WEST);
			pack();
		}
		private JTable addTable() throws SQLException{
			Vector<String> columns = new Vector<>();
			columns.add("");
			columns.add("");
			columns.add("");
			JTable table = new JTable(helper.print(), columns);
			return table;
		}
		
		
		
		ActionListener myAction = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (e.getSource() == btnAdd) {
					addData();
				}
				if (e.getSource() == btnExcel) {
					JPanel excelPanel = new JPanel();
					excelPanel.setLayout(new BoxLayout(excelPanel, BoxLayout.PAGE_AXIS));
					try {
						helper.writeExcel();
						showData(helper.getExcel(), excelPanel);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}
				if (e.getSource() == btnPdf) {
					try {
						helper.writePDF();
					} catch (DocumentException | IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}
			}
		};
		private void showData(ArrayList<String[]> arr, JPanel p){
			for (String[] strArr : arr) {
				p.add(new JLabel("ID:" +strArr[0]+ "  NAME: " +strArr[1]+ "  LOGIN: " +strArr[2]));
			}
			tabPane.addTab("Excel", p);
			pack();
		}
		
		private void addData(){
			
			JPanel addPanel = new JPanel();
			JTextField nameField = new JTextField(5);
			JTextField loginField = new JTextField(5);
			addPanel.add(new JLabel("name:"));
			addPanel.add(nameField);
			addPanel.add(new JLabel("login:"));
			addPanel.add(loginField);
			int res = JOptionPane.showConfirmDialog(this,addPanel,"Put username and password",JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
			if (res == JOptionPane.OK_OPTION) {
				if (!(nameField.getText().equals("") || loginField.getText().equals(""))) {
					try {
						helper.putData(nameField.getText(), loginField.getText());
						tablePanel.removeAll();
						tablePanel.add(addTable());
						tablePanel.updateUI();
						pack();
					} catch (SQLException e) {e.printStackTrace();}
				} else {
					JOptionPane.showMessageDialog(new JFrame(), "enter some name & login!", "Innane worning", JOptionPane.WARNING_MESSAGE);
				}
			}
		}
		
	}
}
