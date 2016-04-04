package Controller;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.TextArea;

public class IPhoneContactReader {
	
	//private static final String PATH = "jdbc:sqlite:C://Users//Amitay//Desktop//IphoneSMSReader//31bb7ba8914766d4ba40d6dfb6113c8b614be442";
	private final String PATH;
	
	private static final String DISTINCT = "DISTINCT";
	private static final String COMMOA = ",";
	private static final String FIRST_NAME_FIELD = "c0First";
	private static final String LAST_NAME_FIELD = "c1Last"; 
	private static final String PHONE = "c15Phone";
	private static final String CONTACT_TABLE = "ABPersonFullTextSearch_content";
	private static final String WHERE = "WHERE";
	private static final String LIKE = "LIKE";
	
	// Vars
	Connection c = null;
	Statement stmt = null;	
	ZoneOffset zoneOffSet;
	
	public IPhoneContactReader(String contactFilePath) {
			
		PATH = "jdbc:sqlite:" + contactFilePath;
	}
	
	public void ConnectToSQL()
	{
		// Connect to DB
		try 
		{
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection(PATH);
			stmt = c.createStatement();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}
	
	
	public void CloseConnection()
	{
		try {
			stmt.close();
			c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public LinkedHashMap<String, String> ConvertHandleIDToNames(ObservableList<String> contactsNames, String[] contactPhoneID) 
	{
		//ObservableList<String> namesOfContatcs = FXCollections.observableArrayList();
		
		LinkedHashMap<String, String> nameToPhoneID = new LinkedHashMap<String,String>();
		
		// Get all the contact names
		//where name like '%BMW%'
		String nameSelect = "SELECT " + FIRST_NAME_FIELD + COMMOA + LAST_NAME_FIELD + COMMOA + PHONE  + " FROM " 
				+ CONTACT_TABLE + " " + WHERE + " " + PHONE + " " + LIKE;
		
		for(int i = 0 ; i < contactsNames.size() ; i++)
		{
			// Get phoneid
			int contactNumber = Integer.parseInt(contactsNames.get(i).toString());
			String phoneID = contactPhoneID[contactNumber];
			try {		
				ResultSet rs = stmt.executeQuery(nameSelect + " '%" + phoneID + "%';" );
				while (rs.next()) {
					
					String name = rs.getString(FIRST_NAME_FIELD) + " " + rs.getString(LAST_NAME_FIELD) + phoneID;
					String val = contactsNames.get(i).toString();
					//namesOfContatcs.add(rs.getString(FIRST_NAME_FIELD) + " " + rs.getString(LAST_NAME_FIELD) + phoneID);
					nameToPhoneID.put(name, val);
					
				}
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return nameToPhoneID;
	}
}
