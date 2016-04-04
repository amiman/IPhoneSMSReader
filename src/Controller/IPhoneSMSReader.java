package Controller;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.TextArea;

public class IPhoneSMSReader {

	//private static final String PATH = "jdbc:sqlite:C://Users//Amitay//Desktop//IphoneSMSReader//3d0d7e5fb2ce288813306e4d4636395e047a3d28";
	private final String PATH;
	
	// Tables
	private static final String MESSAGE_TABLE = "message";
	private static final String HANDLE_TABLE = "handle";

	// Fields
	private static final String DISTINCT = "DISTINCT";
	private static final String HANDLE_ID = "handle_id";
	private static final String CONTACT_ID = Integer.toString(246);
	private static final String TEXT_FIELD = "text";
	private static final String FORM_ME_FIELD = "is_from_me";
	private static final String ROWID = "ROWID";
	private static final String ID = "id";
	private static final String COMMOA = ",";
	private static final String DATE_FIELD = "date";
	private static final String IPHONE_START_DATE = "2001-1-1";
	
	// In order to correct date in sms iphone to starting date of 1/1/2001
	private static final long OFFSET_CORRECTION = Date.valueOf(IPHONE_START_DATE).getTime()/1000;
	
	// Middle east ZoneID
	private static final String MY_ZONE_ID = "+3";

	
	
	// Vars
	Connection c = null;
	Statement stmt = null;	
	ZoneOffset zoneOffSet;
	
	public IPhoneSMSReader(String smsFilePath) {
			
		PATH = "jdbc:sqlite:" + smsFilePath;
		
		// Get zoneoffset
		// TODO: Need to fix time of messages!!!!!!
		ZoneId  zone = ZoneId.of(MY_ZONE_ID);
		LocalDateTime tempDateTime = LocalDateTime.of(2001, 1, 1, 0, 0);
		ZonedDateTime zdt = tempDateTime.atZone(zone);
		zoneOffSet = zdt.getOffset();
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
	
	public ObservableList<String> GetNamesOfContacts()
	{
		ObservableList<String> namesOfContatcs = FXCollections.observableArrayList();
		
		// Get all the messages of handle_id == name
		String messagesSelect = "SELECT " + DISTINCT + " " + HANDLE_ID + " FROM " + MESSAGE_TABLE + ";";
		try {		
			ResultSet rs = stmt.executeQuery(messagesSelect);
			while (rs.next()) {
				namesOfContatcs.add(rs.getString(HANDLE_ID));
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return namesOfContatcs;
	}
	
	public String[] GetContactPhones()
	{
		String[] contactPhones = null;
		// Get from the handle table the phone id that corresponds to each handle id
		String numberOfIDSSelect = "SELECT MAX(" + HANDLE_ID + ") as RECORDCOUNT FROM "  + MESSAGE_TABLE + ";";
		int numberOfIDs = 0;
		try {
			ResultSet rs = stmt.executeQuery(numberOfIDSSelect);
			numberOfIDs = rs.getInt("RECORDCOUNT");
			rs.close();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		

		String handleIdSelect = "SELECT " + ROWID + COMMOA + ID + " FROM " + HANDLE_TABLE + ";";
		try {		
			ResultSet rs = stmt.executeQuery(handleIdSelect);
			contactPhones = new String[numberOfIDs+1];
			while (rs.next()) {
				
				String phoneID = rs.getString(ID);
				if(!Character.toString(phoneID.charAt(0)).equals("+") || phoneID.length() < 6)
				{
					 continue;
				}
				int rowID = rs.getInt(ROWID);
				contactPhones[rowID] = phoneID;
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return contactPhones;
	}
	
	public ObservableList<TextArea> GetMessages(String contatcID)
	{
		ObservableList<TextArea> messages = FXCollections.observableArrayList();
		
		// Get all the messages of handle_id == name
		String messagesSelect = "SELECT " + TEXT_FIELD + COMMOA + DATE_FIELD + COMMOA + FORM_ME_FIELD + " FROM " + MESSAGE_TABLE + " WHERE " + HANDLE_ID + "="+ contatcID + ";";
		ResultSet rs;
		try {
			rs = stmt.executeQuery(messagesSelect);
			while (rs.next()) {
				String message = rs.getString(TEXT_FIELD) + "\n";
				long date = Long.parseLong(rs.getString(DATE_FIELD)) + OFFSET_CORRECTION;
				int isDelivered = rs.getInt(FORM_ME_FIELD);
				
				LocalDateTime dateTime = LocalDateTime.ofEpochSecond(date, 0, zoneOffSet);
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d,MM,yyyy h:mm,a", Locale.ENGLISH);
				String formattedDate = dateTime.format(formatter) + "\n";
				
				// Create the tile message to be displayed
				TextArea messageTile = new TextArea();
				if(isDelivered==0)
				{
					messageTile.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT) ;
				} 
				else 
				{
					messageTile.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT) ;

				}
				messageTile.appendText(formattedDate);
				messageTile.appendText(message);
				
				// Add to messages list
				messages.add(messageTile);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return messages;

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
}
