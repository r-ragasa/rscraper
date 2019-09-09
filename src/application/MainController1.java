
package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.apache.poi.xwpf.usermodel.IBody;
/*
 * Apache POI imports
 */
import org.apache.poi.xwpf.usermodel.XWPFDocument;
//for .docx files
//import org.apache.poi.xwpf.extractor.XPFFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
//For word extractor docx
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.hwpf.HWPFDocument;
//For older than Word 2003 docs
import org.apache.poi.hwpf.extractor.WordExtractor;

/*
 * JDBC sql imports
 */
import java.sql.*;

public class MainController1 implements Initializable{
	/*
	 * Controller class variables
	 */
	private Scanner fileimportScanner;
	String instructionsR = "Drag and drop files to scrap...";
	/*
	 * Menu bar items
	 */
	@FXML
	private MenuItem menuexitbutton;
	@FXML
	private MenuItem helpButton;
	/*
	 * Progress bar
	 */
	@FXML
	private ProgressBar progressbar = new ProgressBar();
	
	/*
	 * Local tab variables
	 */
	@FXML
	private ListView<String> locallistview;
	@FXML
	private Button localremovebutton;
	@FXML
	private Button localremoveallbutton;
	@FXML
	private Button scrapbutton;
	
	/*
	 * Server tab variables
	 */
	@FXML
	private ListView<String> serverlistview;
	@FXML
	private Button serverremovebutton;
	@FXML
	private Button serverremoveallbutton;
	@FXML
	private Button serverimportbutton;
	@FXML
	private Button serverexportbutton;
	@FXML
	private TextField userName;
	@FXML
	private TextField passWord;
	@FXML
	private TextField hdbAddress;
	@FXML
	private TextField dbPort;
	@FXML
	private TextField tableName;
	@FXML
	private TextField pathtoTable;
	
	//MySQL JDBC variables
	static Connection myConn;
	static Statement myStmt;
	static ResultSet myRs;
	private String address; //for MySQL database address

	/*
	 * Status bar variables
	 */
	@FXML
	private Label statusbarlabel;
	
	/*
	 * Main controller methods
	 * 
	 */

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//On startup, this is what happens
		statusbarlabel.setText("Application started...");
		/*
		 * Display instructions
		 */		
		locallistview.getItems().add(instructionsR);
		serverlistview.getItems().add(instructionsR);
				
		
		/*
		 * DRAG AND DROP FEATURE 
		 */
		 
	     //******************User drops file in ListView***************************
	     
		locallistview.setOnDragOver(new EventHandler <DragEvent>() {
			   
            @Override
            public void handle(DragEvent event) {
            	/* data is dragged over the target */
                //if (event.getGestureSource() != locallistview){
                        //&& event.getDragboard().hasFiles()) {
                    /* allow for both copying and moving, whatever user chooses */
                    event.acceptTransferModes(TransferMode.ANY);
                //}
                event.consume();   
            }
    	});
		
		serverlistview.setOnDragOver(new EventHandler <DragEvent>() {
			   
            @Override
            public void handle(DragEvent event) {
            	/* data is dragged over the target */
                    event.acceptTransferModes(TransferMode.ANY);
                event.consume();   
            }
    	});
		
		
		 //*******************Dropping over surface**********************************
		locallistview.setOnDragDropped(new EventHandler<DragEvent>() {
           @Override
           public void handle(DragEvent event) {
        	   progressbar.setProgress(0);
               Dragboard db = event.getDragboard();
               boolean success = false;
               /*
                * Print instructions
                */
               if(locallistview.getItems().isEmpty()){
       				locallistview.getItems().add(instructionsR);
       			}else{
       				locallistview.getItems().removeAll(instructionsR);
       			}
               if (db.hasFiles()) {
                   success = true;
                   String fileName = null;
                   String filePath = null;
                   double fileCounter =0;
                   for (File file:db.getFiles()) {
                	   
                       //fileName = file.getName();
                       filePath = file.getAbsolutePath();
                       /*
                        * Add file names to the ListView here
                        */                    
                       locallistview.getItems().addAll(filePath);
                       locallistview.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                       statusbarlabel.setText("Files added...");
                       fileCounter++;
                       progressbar.setProgress(fileCounter/db.getFiles().size());
                   }
                  
               }
               event.setDropCompleted(success);
               event.consume();
           }
           
       });
		
		serverlistview.setOnDragDropped(new EventHandler<DragEvent>() {
	           @Override
	           public void handle(DragEvent event) {
	        	   progressbar.setProgress(0);
	               Dragboard db = event.getDragboard();
	               boolean success = false;
	               /*
	                * Print instructions
	                */
	               if(serverlistview.getItems().isEmpty()){
	            	   serverlistview.getItems().add(instructionsR);
	       			}else{
	       				serverlistview.getItems().removeAll(instructionsR);
	       			}
	               if (db.hasFiles()) {
	                   success = true;
	                   String fileName = null;
	                   String filePath = null;
	                   double fileCounter =0;
	                   for (File file:db.getFiles()) {
	                	   
	                       //fileName = file.getName();
	                       filePath = file.getAbsolutePath();
	                       /*
	                        * Add file names to the ListView here
	                        */                    
	                       serverlistview.getItems().addAll(filePath);
	                       serverlistview.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	                       statusbarlabel.setText("Files added...");
	                       fileCounter++;
	                       progressbar.setProgress(fileCounter/db.getFiles().size());
	                   }
	                  
	               }
	               event.setDropCompleted(success);
	               event.consume();
	           }
	           
	       });
		/*
		 * End of drag and drop feature
		 */		
		
		
		/*
	     * Remove - Remove All Local buttons
	     */
	    
	    	localremovebutton.setOnAction(new EventHandler<ActionEvent>(){
	    		@Override
	    		public void handle(ActionEvent event){
	    			/*
	    			 * delete all items the user selected
	    			 */
	    			try{
	    				List allfiles =  new ArrayList (locallistview.getSelectionModel().getSelectedItems());
		    			locallistview.getItems().removeAll(allfiles);
		    			
		    			locallistview.getSelectionModel().clearSelection();
		    			statusbarlabel.setText("Selected files removed...");
	    			}catch(Exception e){
	    				e.printStackTrace();
	    			}
	    			/*
	                 * Print instructions
	                 */
	                if(locallistview.getItems().isEmpty()){
	        				locallistview.getItems().add(instructionsR);
	        			}else{
	        				locallistview.getItems().removeAll(instructionsR);
	        			}
	    		}
	    		
	    	});
	    	
	    	serverremovebutton.setOnAction(new EventHandler<ActionEvent>(){
	    		@Override
	    		public void handle(ActionEvent event){
	    			/*
	    			 * delete all items the user selected
	    			 */
	    			try{
	    				List allfiles =  new ArrayList (serverlistview.getSelectionModel().getSelectedItems());
	    				serverlistview.getItems().removeAll(allfiles);
		    			
	    				serverlistview.getSelectionModel().clearSelection();
		    			statusbarlabel.setText("Selected files removed...");
	    			}catch(Exception e){
	    				e.printStackTrace();
	    			}
	    			/*
	                 * Print instructions
	                 */
	                if(serverlistview.getItems().isEmpty()){
	                	serverlistview.getItems().add(instructionsR);
	        			}else{
	        				serverlistview.getItems().removeAll(instructionsR);
	        			}
	    		}
	    		
	    	});
	    
	    	localremoveallbutton.setOnAction(new EventHandler<ActionEvent>(){
	    		@Override
	    		public void handle(ActionEvent event){
	    			progressbar.setProgress(0);
	    			/*
	    			 * delete all items the user selected
	    			 */
	    			locallistview.getItems().clear();
	    			/*
	                 * Print instructions
	                 */
	                if(locallistview.getItems().isEmpty()){
	        				locallistview.getItems().add(instructionsR);
	        			}else{
	        				locallistview.getItems().removeAll(instructionsR);
	        			}
	                progressbar.setProgress(1.0);
	    			statusbarlabel.setText("All files removed...");
	    		}
	    		
	    	});
	    	
	    	serverremoveallbutton.setOnAction(new EventHandler<ActionEvent>(){
	    		@Override
	    		public void handle(ActionEvent event){
	    			progressbar.setProgress(0);
	    			/*
	    			 * delete all items the user selected
	    			 */
	    			serverlistview.getItems().clear();
	    			/*
	                 * Print instructions
	                 */
	                if(serverlistview.getItems().isEmpty()){
	                	serverlistview.getItems().add(instructionsR);
	        			}else{
	        				serverlistview.getItems().removeAll(instructionsR);
	        			}
	                progressbar.setProgress(1.0);
	    			statusbarlabel.setText("All files removed...");
	    		}
	    		
	    	});
	    /*
	     * End of remove buttons
	     */
    	
	}

	/*
	 * ========================================================================
	 * Menu Items
	 *
	 * When user goes to File>Exit, it exits entire program
	 * 
	 * ========================================================================
	 */
	public void exitProgram(ActionEvent event){
		//close connection to database
				if(myConn != null){
					try{
						myConn.close();
					}catch(SQLException e){
						statusbarlabel.setText("Failed to disconnect from server...");
					}
					statusbarlabel.setText("Exiting program...");		
				}
				Platform.exit();
	}
	/*
	 * ========================================================================
	 * Help - show instructions window
	 * ========================================================================
	 */
	public void showHelp(ActionEvent event){
		try{
			
			Parent root = FXMLLoader.load(getClass().getResource("HelpScreen.fxml"));
			/*FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("HelpScreen.fxml"));
			Parent root = loader.load();*/ 
			
			Scene sceneH = new Scene(root);
			Stage windowH = new Stage();
			windowH.initModality(Modality.NONE);
			windowH.setTitle("Help");

			windowH.setScene(sceneH);
			windowH.showAndWait();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
    /*
     * =========================================================================
     * Methods to read/parse the text documents in the list
     * This method is for the Local tab "Scrap" button
     * =========================================================================
     */
    public void scrapFiles(){
    	progressbar.setProgress(0);
    	//Files imported counters
    	int fileimportedcounter = 0;
    	//Files exported counters
    	int listSize = locallistview.getItems().size();
    	
    	/*
		 * Save dialog box to export files to local database
		 */
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Export scrapping to...");
		fileChooser.getExtensionFilters().addAll((new FileChooser.ExtensionFilter("Text Files", "*.txt")),(new FileChooser.ExtensionFilter(".CSV Files", "*.csv")));
		File scrapReport = fileChooser.showSaveDialog(null);
		/*
		 *  End of save dialog box
		 */
				
		try{
			FileWriter writer = new FileWriter(scrapReport);
				try{
	    			
					/*
					 * Actually scrape files in list
					 */
		    		for(int i = 0; i< listSize;i++){
		    			/*
		    			 * Setup scrapping
		    			 */
		    			String filePath = locallistview.getItems().get(i).toString();
		    			int startIndex = locallistview.getItems().get(i).lastIndexOf("\\")+1;
		    			int endIndex = locallistview.getItems().get(i).length();
		    			String fileName =  filePath.substring(startIndex, endIndex);
		    			
		    				fileimportScanner = new Scanner(new File(filePath));//reads from file
		    				statusbarlabel.setText(fileName+" found...");
					    		/*
					    		 *Parse and store words inside the file 
					    		 */
				    			//****************************************
		    				
		    				
			    				/*
			    				 * POI Word doc to text file extraction
			    				 */
			    				FileInputStream filePathStream = new FileInputStream(filePath); 
			    				
			    				/*
			    				 * For .docx Word files
			    				 */
				    				if(fileName.contains(".docx")){
				    					XWPFDocument docx = new XWPFDocument(filePathStream);
					    				XWPFWordExtractor wordExt = new XWPFWordExtractor(docx);
			    						Scanner wordfilescanner = new Scanner(wordExt.getText());
					    				
				    					/*
							    		 *Write words to export file
							    		 */
				    					try{
				    						
				    						while(wordfilescanner.hasNext()){
											//****************************************
				    							writer.append(fileName+","+wordfilescanner.next());
				    							writer.append(System.lineSeparator());
											//****************************************
				    						}
				    						//close scanner
				    						filePathStream.close();
					    					statusbarlabel.setText(fileName+" imported...");
								    		fileimportedcounter++;
								    		progressbar.setProgress(fileimportedcounter/listSize);
				    					}catch(Exception e){
				    						statusbarlabel.setText("POI .docx import error");
				    						progressbar.setProgress(0.5);
				    					}
				    					//close POI Extractor
								    	//wordExt.close();
						    	/*
						    	 * For .doc Word files
						    	 */
							    	
				    				}else if(fileName.contains(".doc")){
				    					HWPFDocument wordDoc = new HWPFDocument(filePathStream);
			    						WordExtractor wordExt = new WordExtractor(wordDoc);
			    						String [] words = wordExt.getParagraphText();
			    						
			    						
				    					/*
							    		 *Write words to export file
							    		 */
				    					try{	    						
				    						
			    							for(int j =0;j<words.length;j++){
											//****************************************
											 if(words[j] != null){
			    								Scanner wordfilescanner = new Scanner(words[j]);
				    								while(wordfilescanner.hasNext()){
						    							writer.append(fileName+","+wordfilescanner.next());
						    							writer.append(System.lineSeparator());
						    						}
				    								//close scanner
				    						    	wordfilescanner.close();
			    								}
											//****************************************
				    						}
				    						
					    					statusbarlabel.setText(fileName+" imported...");
								    		fileimportedcounter++;
								    		progressbar.setProgress(fileimportedcounter/listSize);
				    					}catch(Exception e){
				    						System.out.println("POI .doc import error");
				    						progressbar.setProgress(0.5);
				    					}
				    					//close POI Extractor
								    	wordExt.close();
		    					/*
			    				 * End of POI
			    				 */
		    				}else{
		    					/*
		    					 * Handle all other file types
		    					 */
		    				
		    					while(fileimportScanner.hasNext()){
		    						//for testing: wordCounter++;
		    						
		    						String aWord = fileimportScanner.next();
		    						/*
						    		 *Write words to export file
						    		 */
									//****************************************	    							
		    							writer.append(fileName+","+aWord);
		    							writer.append(System.lineSeparator());
									//****************************************
					    		
					    		}
					    		//****************************************
					    		statusbarlabel.setText(fileName+" imported...");
					    		fileimportedcounter++;
					    		progressbar.setProgress(fileimportedcounter/listSize);
		    				}
		    				//close File Input Stream
		    		    	filePathStream.close();
		    			}
					//close file
			    	fileimportScanner.close();
			    	
			    	
	    			}
		    		catch(Exception e){
		    			System.out.print("Error scrapping files...");
		    			progressbar.setProgress(0.5);
		    		}
	    			statusbarlabel.setText("Files - ("+fileimportedcounter+") imported...");
	    			//Close writer
	        		writer.flush();
	    			writer.close();	
	    			
	    	}catch(Exception e2){
	    		System.out.print("Error closing writer...");
	    		progressbar.setProgress(0.75);
	    	}
    	}    		
    
    /*
     * =========================================================================
     * Server tab import and export buttons
     * =========================================================================
     */
    public void importFiles(){
    	progressbar.setProgress(0);
    	//Files imported counters
    	int fileimportedcounter = 0;
    	//Files exported counters
    	int listSize = serverlistview.getItems().size();
    	
    	
    	try{
    		//1. Get a connection to the database
    		address = "jdbc:mysql://"+hdbAddress.getText()+":"+dbPort.getText()+"/"+pathtoTable.getText();
    		Connection myConn = DriverManager.getConnection(address,userName.getText(),passWord.getText());
    		statusbarlabel.setText("Connected to server...");
		    	/*
				 * Actually scrape files in list
				 */
	    		for(int i = 0; i< listSize;i++){
	    			/*
	    			 * Setup scrapping
	    			 */
	    			String filePath = serverlistview.getItems().get(i).toString();
	    			int startIndex = serverlistview.getItems().get(i).lastIndexOf("\\")+1;
	    			int endIndex = serverlistview.getItems().get(i).length();
	    			String fileName =  filePath.substring(startIndex, endIndex);
	    			
	    				fileimportScanner = new Scanner(new File(filePath));//reads from file
	    				statusbarlabel.setText(fileName+" found...");
				    		/*
				    		 *Parse and store words inside the file 
				    		 */
			    			//****************************************
	    				
	    				
		    				/*
		    				 * POI Word doc to text file extraction
		    				 */
		    				FileInputStream filePathStream = new FileInputStream(filePath); 
	
		    				/*
		    				 * For .docx Word files
		    				 */
			    				if(fileName.contains(".docx")){
			    					XWPFDocument docx = new XWPFDocument(filePathStream);
				    				XWPFWordExtractor wordExt = new XWPFWordExtractor(docx);
		    						Scanner wordfilescanner = new Scanner(wordExt.getText());
			    					/*
						    		 *Write words to export file
						    		 */
		    						
			    					try{   
			    						statusbarlabel.setText("Importing "+fileName+" to server...");
			    						//2. Create a MySQL statement
		    		    				PreparedStatement myStmt = myConn.prepareStatement("insert into "+tableName.getText()+"(File, Word) values (?,?)");
			    						while(wordfilescanner.hasNext()){
										
			    							/*
			    					    	 * Actually import into MySQL table 
			    					    	 */
			    							//****************************************
			        							//3. Execute SQL query - we will use PreparedStatements
			        							myStmt.setString(1, fileName);
			        							myStmt.setString(2, wordfilescanner.next());
			    								
			        							myStmt.executeUpdate();
			    							//****************************************
			        						// For testing: to empty the DB table, Execute SQL command: TRUNCATE TABLE tablename
			    						}
			    						//close scanner
								    	wordfilescanner.close();
								    	//closeDocument
								    	docx.close();
				    					statusbarlabel.setText(fileName+" imported...");
							    		fileimportedcounter++;
							    		progressbar.setProgress(fileimportedcounter/listSize);
			    					}catch(SQLException e){
			    						statusbarlabel.setText("Database .docx Import error");
			    					}finally{
			    						if(myStmt != null){
				    						try{
				    		 					myStmt.close();
				    		 				}catch(SQLException e){
				    		 					statusbarlabel.setText("Error closing .docx statement...");
				    		 				}
			    						}
			    					}
			    					//close POI Extractor
							    	wordExt.close();
			    				/*
						    	 * For .doc Word files
						    	 */
							    	}else if(fileName.contains(".doc")){
				    					HWPFDocument wordDoc = new HWPFDocument(filePathStream);
			    						WordExtractor wordExt = new WordExtractor(wordDoc);
			    						String [] words = wordExt.getParagraphText();
				    					/*
							    		 *Write words to export file
							    		 */
				    					try{	    						
				    						PreparedStatement myStmt = myConn.prepareStatement("insert into "+tableName.getText()+"(File, Word) values (?,?)");
				    						statusbarlabel.setText("Importing "+fileName+" to server...");
			    							for(int j =0;j<words.length;j++){
											//****************************************
											 if(words[j] != null){
			    								Scanner wordfilescanner = new Scanner(words[j]);
				    								while(wordfilescanner.hasNext()){
						    							/*
						    					    	 * Actually import into MySQL table 
						    					    	 */
						    							//****************************************
					        							myStmt.setString(1, fileName);
					        							myStmt.setString(2, wordfilescanner.next());
						        						myStmt.executeUpdate();
						    							//****************************************
						    						}
			    								//close scanner
			    						    	wordfilescanner.close();
			    								}
											//****************************************
				    						}
			    							//closeDocument
			    							wordDoc.close();
					    					statusbarlabel.setText(fileName+" imported...");
								    		fileimportedcounter++;
								    		progressbar.setProgress(fileimportedcounter/listSize);
				    					}catch(Exception e){
				    						System.out.println("Database .doc Import error");
				    					}finally{
				    						if(myStmt != null){
					    						try{
					    		 					myStmt.close();
					    		 				}catch(SQLException e){
					    		 					statusbarlabel.setText("Error closing .doc statement...");
					    		 				}
				    						}
				    					}
				    					//close POI Extractor
								    	wordExt.close();
								    	
		    					/*
			    				 * End of POI
			    				 */
		    				}else{
		    					/*
		    					 * Handle all other file types
		    					 */
		    					PreparedStatement myStmt = myConn.prepareStatement("insert into "+tableName.getText()+"(File, Word) values (?,?)");
		    					try{
		    						statusbarlabel.setText("Importing "+fileName+" to server...");
		    						while(fileimportScanner.hasNext()){
		    						
		    						/*
							    	 * Actually import into MySQL table 
							    	 */
									//****************************************
		    							myStmt.setString(1, fileName);
		    							myStmt.setString(2, fileimportScanner.next());
		    							myStmt.executeUpdate();
		    						}	
									//****************************************
					    		}catch(Exception e){
	    							statusbarlabel.setText("Error importing to server...");
	    						}finally{
	    							if(myStmt != null){
			    						try{
			    		 					myStmt.close();
			    		 				}catch(SQLException e){
			    		 					statusbarlabel.setText("Error closing SQL statement...");
			    		 				}
	    							}
		    					}
					    		statusbarlabel.setText(fileName+" imported...");
					    		fileimportedcounter++;
					    		progressbar.setProgress(fileimportedcounter/listSize);
		    				}
		    				//close File Input Stream
		    		    	filePathStream.close();
		    			}
	    		//close file
		    	fileimportScanner.close();
		    	
    	}catch(Exception exc){
    		statusbarlabel.setText("Failed to connect to server...");
    		exc.printStackTrace();
    		System.out.print(exc);
    		progressbar.setProgress(0.5);
    	}finally{
    		/*
    		 * Close connection to server 
    		 */
    		if(myConn != null){
    			try{
    				myConn.close();
    			}catch(SQLException e){
    				statusbarlabel.setText("Failed to disconnect from server...");
    				progressbar.setProgress(0.75);
    			}		
    		}
    	}
    }
    /*
     * Method to export database
     */
    public void exportFiles(){
    	
    	/*
		 * Save dialog box to export files to local database
		 */
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Export scrap report to...");
		fileChooser.getExtensionFilters().addAll((new FileChooser.ExtensionFilter("Text Files", "*.txt")),(new FileChooser.ExtensionFilter(".CSV Files", "*.csv")));
		File scrapReport = fileChooser.showSaveDialog(null);
		/*
		 *  End of save dialog box
		 */
    	
    	/*
    	 * Actually export information to writer file
    	 */
    	 try{
    		 
    		//1. Get a connection to the database
     		address = "jdbc:mysql://"+hdbAddress.getText()+":"+dbPort.getText()+"/"+pathtoTable.getText();
     		Connection myConn = DriverManager.getConnection(address,userName.getText(),passWord.getText());
 			
 			//2. Create a statement
    		PreparedStatement myStmt = myConn.prepareStatement("select * from "+tableName.getText());
		
				
 			//3. Execute SQL query
 			ResultSet myRs = myStmt.executeQuery();
 			
 			/*
 			 * Export result
 			 */
 			FileWriter writer = new FileWriter(scrapReport);
 			try{
 				/*
 				 * Write to file
 				 */
	 			while(myRs.next()){
					writer.append(myRs.getString("File")+","+myRs.getString("Word"));
					writer.append(System.lineSeparator());
				}		 			
	 			statusbarlabel.setText("Database exported...");
		    	progressbar.setProgress(1.0);
 			}catch(Exception e){
    			System.out.print("Error exporting from database...");
    			progressbar.setProgress(0.5);
 			}finally{
    			//Close writer
        		writer.flush();
    			writer.close();	
 			}
 	    }catch(Exception exc){
 	    	statusbarlabel.setText("Error exporting from server...");
 	    }finally{
 	    	/*
 	    	 * Close everything
 	    	 */
 	    	if(myRs != null){
 				try{
 					myRs.close();
 				}catch(SQLException e){
 					statusbarlabel.setText("Error closing my result set...");
 					progressbar.setProgress(0.25);
 				}
 	    	}
 	    	if(myStmt != null){
 				try{
 					myStmt.close();
 				}catch(SQLException e){
 					statusbarlabel.setText("Error closing my statement...");
 					progressbar.setProgress(0.5);
 				}
 	    	}
 	    	if(myConn != null){
    			try{
    				myConn.close();
    			}catch(SQLException e){
    				statusbarlabel.setText("Failed to disconnect from server...");
    				progressbar.setProgress(0.75);
    			}		
    		}
 	    }
    }
}
