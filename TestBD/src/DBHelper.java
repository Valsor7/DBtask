import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfCell;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import com.mysql.jdbc.PreparedStatement;


public class DBHelper {
	
	String url = "jdbc:mysql://localhost:3306/sample";
	String driver = "com.mysql.jdbc.Driver";
	String pass = "0409";
	String user = "root";
	String allSql = "SELECT * FROM users";
	Connection conn = null;
	Statement st = null;
	java.sql.PreparedStatement ps = null;
	ResultSet rs = null;
	private Vector<Object> vector;
	private ArrayList<String[]> arrList;
	
	public DBHelper() {
		// TODO Auto-generated constructor stub
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, pass);
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			if (conn != null) {
				System.out.println("connected to db");
			}
	}
	public void putData(String userName, String userLogin) throws SQLException {
		conn = DriverManager.getConnection(url, user, pass);
		ps = conn.prepareStatement("INSERT INTO users(name, login) VALUES(?, ?)");
		ps.setString(1, userName);
		ps.setString(2, userLogin);
		ps.executeUpdate();
		ps.close();
		conn.close();
	}
	public Vector<Object> print() throws SQLException{
		vector = new Vector<>();
		conn = DriverManager.getConnection(url, user, pass);
		st = conn.createStatement();
		rs = st.executeQuery(allSql);		
		while (rs.next()) {
			Vector<Object> data = new Vector<>();
			data.add(rs.getInt("id"));
			data.add(rs.getString("name"));
			data.add(rs.getString("login"));
			vector.add(data);
		}
		st.close();
		rs.close();
		conn.close();
		return vector;
	}
	
	public void writeExcel() throws IOException {
		HSSFWorkbook workBook = new HSSFWorkbook();
		HSSFSheet sheet = workBook.createSheet("first");
		for (int i = 0; i < vector.size(); i++) {
			Row row = sheet.createRow(i);
			Vector<Object> v = (Vector<Object>) vector.elementAt(i);
			for (int j = 0; j < 3; j++) {
				Cell cell = row.createCell(j);
				if (v.elementAt(j).getClass() == Integer.class) {
					cell.setCellValue((Integer)v.elementAt(j));
				} else 
					cell.setCellValue((String)v.elementAt(j));
			}
		}
		FileOutputStream out = new FileOutputStream(new File("testSheet.xls"));
		workBook.write(out);
		out.close();
	}
	
	
	public ArrayList<String[]> getExcel() throws IOException{
		arrList = new ArrayList<String[]>();
		
		FileInputStream in = new FileInputStream(new File("testSheet.xls"));
		HSSFWorkbook book = new HSSFWorkbook(in);
		HSSFSheet sheet = book.getSheet("first");
		for (int i = 0; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			String[] arrStr = new String[3];
			for (int j = 0; j < 3; j++) {
				
				Cell cell = row.getCell(j);
				if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
					arrStr[j] = String.valueOf(cell.getNumericCellValue());
				} else 
					arrStr[j] = cell.getStringCellValue();
			}
			arrList.add(arrStr);
		}
		in.close();
		return arrList;
	}
	
	public void writePDF() throws DocumentException, IOException{
		FileOutputStream out = new FileOutputStream(new File("myPdf.pdf"));
		Document doc = new Document();
		PdfWriter.getInstance(doc, out);
		doc.open();
		Paragraph title = new Paragraph();
		title.setAlignment(Element.ALIGN_CENTER);
		title.add("USERS");
		doc.add(title);
		Paragraph p = new Paragraph(20);
		p.add(" ");
		doc.add(p);
		PdfPTable table = new PdfPTable(3);
		for (String[] arrStr : getExcel()) {
			for (String s : arrStr) {
				PdfPCell cell = new PdfPCell(new Paragraph(s));
				table.addCell(cell);
			}
		}
		doc.add(table);
		doc.close();
	}
}
