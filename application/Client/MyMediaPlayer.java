package application.Client;

/**
 * @author Killian Nolan - R00129172 - DWEB4
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import application.ListObserver;
import application.Server.MonitorFolder;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;


public class MyMediaPlayer extends Application implements Observer{

	Button button;
	private static  Button     quit;
	Media media;
	MediaPlayer mediaPlayer;
	ListView<String> lvList, lvList2;
	ObservableList<String> items,items2,items22, itemsDifference, serverSongs;
	Socket clientSocket = null;
	DataInputStream is = null;
	PrintStream os = null;
	String responseLine= null;
	ObjectOutputStream oos = null;
	ObjectInputStream ois = null;


	@Override // Override the start method in the Application class
	public void start(Stage primaryStage) throws IOException {

		try {
			clientSocket = new Socket("localhost", 3333);
			oos = new ObjectOutputStream(clientSocket.getOutputStream());
			ois = new ObjectInputStream(clientSocket.getInputStream());

		} catch (UnknownHostException e) {
			System.err.println("Don't know about host");
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to host");
		}

		if (clientSocket != null && oos != null && ois != null) {

			System.out.println("The client is connected with the server.");

			MonitorLocal localInstance = MonitorLocal.getInstance();
			lvList = new ListView<String>();
			lvList2 = new ListView<String>();
			items = FXCollections.observableArrayList (localInstance.getNames());
			lvList.setItems(items);
			lvList.getSelectionModel().select(0);

			String option1 = "1";
			oos.writeObject(option1);  
			oos.flush();

			try {
				@SuppressWarnings("unchecked")
				ArrayList<String> itemsS = (ArrayList<String>) ois.readObject();
				ArrayList<String> itemsServer = localInstance.differenceItems(itemsS);
				lvList2.getItems().clear();
				lvList2.getItems().addAll(itemsServer);
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}

			media = new Media(new File("/Users/killiannolan/Documents/FinalYear/DistributedSysProgramming/Lab1-Sys/Folder1/" + items.get(0)).toURI().toString());
			mediaPlayer = new MediaPlayer(media);

			Button playButton = new Button("Play");
			playButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override public void handle(ActionEvent actionEvent) {
					if (playButton.getText().equals("Play")) {
						mediaPlayer.play();
						playButton.setText("Pause");
					} else {
						mediaPlayer.pause();
						playButton.setText("Play");
					}
				}
			});

			Button rewindButton = new Button("<<");
			rewindButton.setOnAction(e -> mediaPlayer.seek(Duration.ZERO));
			Button btnAdd = new Button("Download");

			lvList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
					mediaPlayer.stop();
					mediaPlayer.dispose();
					playButton.setText("Play");
					playButton.setDisable(false);
					btnAdd.setDisable(true);
					String string = ("/Users/killiannolan/Documents/FinalYear/DistributedSysProgramming/Lab1-Sys/Folder1/" + newValue);
					try {
						media = new Media(new File(string).toURI().toString());
						mediaPlayer = new MediaPlayer(media);
					}
					catch (MediaException e) {
					}
					playButton.setOnAction(new EventHandler<ActionEvent>() {
						@Override public void handle(ActionEvent actionEvent) {
							if (playButton.getText().equals("Play")) {
								mediaPlayer.play();
								playButton.setText("Pause");
							} else {
								mediaPlayer.pause();
								playButton.setText("Play");
							}
						}
					});
					rewindButton.setOnAction(e -> mediaPlayer.seek(Duration.ZERO));
				}
			});

			lvList2.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
					mediaPlayer.stop();
					mediaPlayer.dispose();
					playButton.setText("Play");
					playButton.setDisable(true);
					btnAdd.setDisable(false);
				}
			});

			Label mediaListLabel = new Label("Home Directory");
			Label differenceLabel = new Label("Songs on Server");		

			btnAdd.setDisable(true);
			btnAdd.setOnAction(new EventHandler<ActionEvent>() {
				@Override public void handle(ActionEvent actionEvent) {
					String option2= "2" + lvList2.getSelectionModel().getSelectedItem();
					try {
						oos.writeObject(option2);
						oos.flush();
					} catch (IOException e1) {
						e1.printStackTrace();
					}  

					try {
						byte [] bytes = (byte []) ois.readObject();
						localInstance.copyFile(bytes, lvList2.getSelectionModel().getSelectedItem());
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}

					try {
						oos.writeObject(option1);
						oos.flush();
					} catch (IOException e1) {
						e1.printStackTrace();
					}  

					try {
						@SuppressWarnings("unchecked")
						ArrayList<String> itemsS = (ArrayList<String>) ois.readObject();
						ArrayList<String> itemsServer = localInstance.differenceItems(itemsS);
						lvList2.getItems().clear();
						lvList2.getItems().addAll(itemsServer);
					} catch (IOException | ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			});

			Button upload = new Button ("Upload");
			upload.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					FileChooser chooser = new FileChooser();
					FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("select your media(*.mp3)", "*.MP3");
					chooser.getExtensionFilters().add(filter);
					File file = chooser.showOpenDialog(primaryStage);
					String file2 = file.toString();
					String songName = file.getName(); 
					File fileSong = new File(file2);
					String option2= "3" + songName;
					byte [] bytesUpload = localInstance.getB(fileSong);
					if ( file !=null){
						try {
							oos.writeObject(option2);
							oos.writeObject(bytesUpload);
							oos.flush();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			});

			Slider slVolume = new Slider();
			slVolume.setPrefWidth(150);
			slVolume.setMaxWidth(Region.USE_PREF_SIZE);
			slVolume.setMinWidth(30);
			slVolume.setValue(50);
			mediaPlayer.volumeProperty().bind(
					slVolume.valueProperty().divide(100));


			quit = new Button ("Quit");
			quit.setOnAction(e->System.exit(0));

			BorderPane border = new BorderPane();
			border.setPadding(new Insets(20, 20, 20, 20));

			playButton.setMaxWidth(Double.MAX_VALUE);

			VBox row1 = new VBox(20);
			row1.getChildren().addAll(mediaListLabel, lvList,differenceLabel,lvList2,btnAdd,quit);

			HBox layout = new HBox(30);
			layout.setPadding(new Insets(20, 20, 20, 20));
			layout.getChildren().addAll(row1);

			VBox col2 = new VBox(50);
			col2.getChildren().addAll(playButton,rewindButton,slVolume, upload);
			col2.setSpacing(40);
			col2.setPadding(new Insets(0, 20, 10, 20)); 
			layout.getChildren().addAll(col2);

			Scene scene = new Scene(layout, 500, 600);
			primaryStage.setTitle("Media Player"); 
			primaryStage.setScene(scene); 
			primaryStage.show();

			Task<Integer> task = new Task<Integer>() {
				@Override protected Integer call() throws Exception {
					int iterations;
					for (iterations = 0; iterations < 10000000; iterations++) {
						Platform.runLater(() -> {
							Boolean bool = localInstance.checkForChange(items);
							if (bool == true) {
								items = FXCollections.observableArrayList (localInstance.getNames());
								lvList.getItems().clear();
								lvList.getItems().addAll(items);
								lvList.getSelectionModel().select(0);
							}
						});	
						Thread.sleep(1000);
					}
					return iterations;
				}
			};

			Task<Integer> task2 = new Task<Integer>() {
				@Override protected Integer call() throws Exception {
					int iterations;
					String option4 = "4";
					for (iterations = 0; iterations < 10000000; iterations++) {
						oos.writeObject(option4);
						oos.flush();
						Boolean serverChange = (Boolean) ois.readObject();

						Platform.runLater(() -> {
							if (serverChange == true) {
								try {
									@SuppressWarnings("unchecked")
									ArrayList <String> newList= (ArrayList <String>) ois.readObject();
									ArrayList<String> itemsServer = localInstance.differenceItems(newList);
									lvList2.getItems().clear();
									lvList2.getItems().addAll(itemsServer);
									lvList2.getSelectionModel().select(0);
									String serverList = "Server";
									ListObserver ListObserver =new ListObserver(serverList);
									MyMediaPlayer media =new MyMediaPlayer();
									ListObserver.register(media);
									ListObserver.notifyObserver();
								} catch (ClassNotFoundException | IOException e) {
									e.printStackTrace();
								}
							}
						});	
						Thread.sleep(3000);
					}
					return iterations;
				}
			};


			Thread th = new Thread(task); 
			th.setDaemon(true); 
			th.start();

			Thread th2 = new Thread(task2); 
			th2.setDaemon(true); 
			th2.start();
		}

	}

	@Override
	public void update(Observable o, Object list) {
		MonitorFolder monitorInstance = MonitorFolder.getInstance();
		items = FXCollections.observableArrayList (monitorInstance.getNames());
		Platform.runLater(new Runnable() {
			@Override public void run() {
				System.out.println("There's been a change detected on your " + list + " Songs, the list has now been updated!");
			}
		});
	}

	public static void main(String[] args) {
		launch(args);
	}
}
