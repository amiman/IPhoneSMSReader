package Controller.Model;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import Controller.IPhoneContactReader;
import Controller.IPhoneSMSReader;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.control.Notifications;

public class Model {

	// Create basic data
	private IPhoneSMSReader reader = null;
	private IPhoneContactReader readerContact = null;
	private ObservableList<String> contactsIDs;
	private ObservableList<String> contactsNames;
	private LinkedHashMap<String, String> contactsNamesAndPhoneID;
	private String smsFilePath = null;
	private String contactFilePath = null;
	
	@FXML
    private ListView nameListView;
	//private javafx.scene.control.TextArea messageArea;
	private ListView<javafx.scene.control.TextArea> messageArea;
	private Button loadSMSFile;
	private Button loadContactFile;
	private Button generate;
	private javafx.scene.control.TextField smsTextField;
	private javafx.scene.control.TextField contactTextField;
	
	private Desktop desktop = Desktop.getDesktop();
	private Scene mScene;
	private Stage mStage;

	
	public Model(Scene scene,Stage stage) {
		
		mScene = scene;
		mStage = stage;
		
		
		smsTextField = (javafx.scene.control.TextField) mScene.lookup("#SmsTextField");
		contactTextField = (javafx.scene.control.TextField) mScene.lookup("#ContactTextField");
		
		// Create Load buttons
		final FileChooser fileChooser = new FileChooser();
		
		loadSMSFile = (Button) mScene.lookup("#LoadSMSFile");
		loadContactFile = (Button) mScene.lookup("#LoadContactFile");
		
		loadSMSFile.setOnAction(
	        new EventHandler<ActionEvent>() {
	            @Override
	            public void handle(final ActionEvent e) {
	                configureFileChooser(fileChooser);
	                File file = fileChooser.showOpenDialog(stage);
	                if (file != null && file.isFile()) {
	                    //openFile(file);
	                	
	                	//smsFilePath = file.getAbsolutePath();
	                	smsTextField.setText(file.getAbsolutePath());
	                	
	                	// Create IPhoneSMSReader
	                	/*
	            		reader = new IPhoneSMSReader(file.getAbsolutePath());
	            		reader.ConnectToSQL();
	            		
	            		*/
	                }
	            }

				private void configureFileChooser(FileChooser fileChooser) {
					fileChooser.setTitle("Choose File");
			        fileChooser.setInitialDirectory(new File(System.getProperty("user.home"))); 
				}
	

	        });
		
		loadContactFile.setOnAction(
			new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                configureFileChooser(fileChooser);
                File file = fileChooser.showOpenDialog(stage);
                if (file != null && file.isFile()) {
                    //openFile(file);
                	
                	//contactFilePath = file.getAbsolutePath();
            		contactTextField.setText(file.getAbsolutePath());

                	// Create IPhoneContactReader
            		/*
            		readerContact = new IPhoneContactReader(file.getAbsolutePath());
            		readerContact.ConnectToSQL();
            		*/
                }
            }

			private void configureFileChooser(FileChooser fileChooser) {
				fileChooser.setTitle("Choose File");
		        fileChooser.setInitialDirectory(new File(System.getProperty("user.home"))); 
			}


        });
		
		// Create generate button
		generate = (Button) mScene.lookup("#GenrateSMS");
		
		generate.setOnAction(
			new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                
            	// Check if the sms reader was generated
            	smsFilePath = smsTextField.getText();
            	contactFilePath = contactTextField.getText();
            	
            	if(smsFilePath.equals(""))
            	{
            		smsFilePath = null;
            	}
            	
            	if(contactFilePath.equals(""))
            	{
            		contactFilePath = null;
            	}
            	
            	if(smsFilePath != null)
            	{
            		// Create sms file reader
            		reader = new IPhoneSMSReader(smsFilePath);
            		reader.ConnectToSQL();
            		
            		// Create NameListView
        			BuildContatcsListView();
        			
        			// Create contact file reader
            		readerContact = new IPhoneContactReader(contactFilePath);
            		readerContact.ConnectToSQL();
            		
        			// Create Text Area for messages
        			BuildTextAreaForMessagess();
            	}
            	else
            	{
            		new JFXPanel();
                    notifier("You must choose an SMS file!","You must choose an SMS file!");
            	}
            }});
	}
	
	private static void notifier(String pTitle, String pMessage) {
        Platform.runLater(() -> {
                    Stage owner = new Stage(StageStyle.TRANSPARENT);
                    StackPane root = new StackPane();
                    root.setStyle("-fx-background-color: TRANSPARENT");
                    Scene scene = new Scene(root, 1, 1);
                    scene.setFill(Color.TRANSPARENT);
                    owner.setScene(scene);
                    owner.setWidth(1);
                    owner.setHeight(1);
                    owner.toBack();
                    owner.show();
                    Notifications.create().title(pTitle).text(pMessage).showInformation();
                }
        );
    }
	public void BuildContatcsListView()
	{
		nameListView = (ListView<String>) mScene.lookup("#NameListView");
		contactsIDs = reader.GetNamesOfContacts();
		nameListView.getItems().clear();
		nameListView.setItems(contactsIDs);
		
		// Add functionality of pressing a contatcID
		nameListView.setOnMouseClicked(new EventHandler<MouseEvent>() {

	        @Override
	        public void handle(MouseEvent event) {
	        	
	        	// Get messages from IPhoneSMSReader
	        	//reader.GetMessages(nameListView.getSelectionModel().getSelectedItem().toString());
	        	String name = nameListView.getSelectionModel().getSelectedItem().toString();
	        	ObservableList<javafx.scene.control.TextArea> messageFromContactObservable;
	        	if(contactFilePath != null)
	        	{
	        		messageFromContactObservable = reader.GetMessages(contactsNamesAndPhoneID.get(name));
	        	}
	        	else
	        	{
	        		messageFromContactObservable = reader.GetMessages(name);
	        	}
	        
	        	// Clear old messages from view
	        	//messageArea.clear();
	        	messageArea.getItems().clear();
	        	
	        	// Write messages on messageArea
	        	//messageArea.appendText(messagesFromContatc.toString());
	        	messageArea.setItems(messageFromContactObservable);
	        	
	        }
	    });
				
	}


	public void BuildTextAreaForMessagess() 
	{
		//messageArea = (javafx.scene.control.TextArea) mScene.lookup("#MessagesTextView");
		messageArea = (ListView<javafx.scene.control.TextArea>) mScene.lookup("#MessagesTextView");
		messageArea.getItems().clear();
		
		// Get contact phonesID
    	String[] contactPhoneID = reader.GetContactPhones();
    	
		// Convert handleID to names using Contact backup file
    	if(contactFilePath != null)
    	{
    		contactsNamesAndPhoneID = readerContact.ConvertHandleIDToNames(contactsIDs,contactPhoneID);
    		// Set the list of contatcs
        	contactsNames = FXCollections.observableArrayList();
        	for (String key: contactsNamesAndPhoneID.keySet()) {
        		contactsNames.add(key);
        	}
        	
    	} else {
    		contactsNames = contactsIDs;
    	}
    	
    	
    	nameListView.setItems(contactsNames);
	}
	
	private void openFile(File file) {
        try {
            desktop.open(file);
        } catch (IOException ex) {

        }
    }
}
