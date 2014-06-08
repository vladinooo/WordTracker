package com.vladinooo.wordtracker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Dialogs;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.thoughtworks.xstream.XStream;
import com.vladinooo.wordtracker.model.Word;
import com.vladinooo.wordtracker.util.FileUtil;

public class MainApp extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;

	private ObservableList<Word> wordData = FXCollections.observableArrayList();

	public MainApp() {
		// Add some sample data
		wordData.add(new Word("Love", "\u041b\u044e\u0431\u043e\u0432\u044c"));
		wordData.add(new Word("Peace", "\u043c\u0438\u0440"));
		wordData.add(new Word("Unity", "\u0435\u0434\u0438\u043d\u0441\u0442\u0432\u043e"));
	}

	public ObservableList<Word> getWordData() {
		return wordData;
	}

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Word Tracker");
		this.primaryStage.getIcons().add(
				new Image("file:resources/images/WordTracker-icon.png"));

		try {
			// Load the root layout from the fxml file
			FXMLLoader loader = new FXMLLoader(
					MainApp.class.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			
			// Give the controller access to the main app
			RootLayoutController controller = loader.getController();
			controller.setMainApp(this);

			primaryStage.show();
		} catch (IOException e) {
			// Exception gets thrown if the fxml file could not be loaded
			e.printStackTrace();
		}

		showWordOverview();

		// Try to load last opened word file
		File file = getWordFilePath();
		if (file != null) {
			loadWordDataFromFile(file);
		}
	}

	/**
	 * Returns the main stage.
	 * 
	 * @return
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	/**
	 * Shows the word overview scene.
	 */
	public void showWordOverview() {
		try {
			// Load the fxml file and set into the center of the main layout
			FXMLLoader loader = new FXMLLoader(
					MainApp.class.getResource("view/WordOverview.fxml"));
			AnchorPane overviewPage = (AnchorPane) loader.load();
			rootLayout.setCenter(overviewPage);

			// Give the controller access to the main app
			WordOverviewController controller = loader.getController();
			controller.setMainApp(this);

		} catch (IOException e) {
			// Exception gets thrown if the fxml file could not be loaded
			e.printStackTrace();
		}
	}

	/**
	 * Opens a dialog to edit details for the specified word. If the user
	 * clicks OK, the changes are saved into the provided word object and true
	 * is returned.
	 * 
	 * @param person
	 *            the word object to be edited
	 * @return true if the user clicked OK, false otherwise.
	 */
	public boolean showWordEditDialog(Word word) {
		try {
			// Load the fxml file and create a new stage for the popup
			FXMLLoader loader = new FXMLLoader(
					MainApp.class.getResource("view/WordEditDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Edit Word");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			// Set the word into the controller
			WordEditDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setWord(word);

			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();

			return controller.isOkClicked();

		} catch (IOException e) {
			// Exception gets thrown if the fxml file could not be loaded
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Returns the word file preference, i.e. the file that was last opened.
	 * The preference is read from the OS specific registry. If no such
	 * preference can be found, null is returned.
	 * 
	 * @return
	 */
	public File getWordFilePath() {
		Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
		String filePath = prefs.get("filePath", null);
		if (filePath != null) {
			return new File(filePath);
		} else {
			return null;
		}
	}

	/**
	 * Sets the file path of the currently loaded file. The path is persisted in
	 * the OS specific registry.
	 * 
	 * @param file
	 *            the file or null to remove the path
	 */
	public void setWordFilePath(File file) {
		Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
		if (file != null) {
			prefs.put("filePath", file.getPath());

			// Update the stage title
			primaryStage.setTitle("Word Tracker - " + file.getName());
		} else {
			prefs.remove("filePath");

			// Update the stage title
			primaryStage.setTitle("Word Tracker");
		}
	}

	/**
	 * Loads word data from the specified file. The current word data will
	 * be replaced.
	 * 
	 * @param file
	 */
	@SuppressWarnings("unchecked")
	public void loadWordDataFromFile(File file) {
		XStream xstream = new XStream();
		xstream.alias("word", Word.class);

		try {
			String xml = FileUtil.readFile(file);

			ArrayList<Word> wordList = (ArrayList<Word>) xstream.fromXML(xml);

			wordData.clear();
			wordData.addAll(wordList);

			setWordFilePath(file);
		} catch (Exception e) { // catches ANY exception
			Dialogs.showErrorDialog(primaryStage,
					"Could not load data from file:\n" + file.getPath(),
					"Could not load data", "Error", e);
		}
	}

	/**
	 * Saves the current word data to the specified file.
	 * 
	 * @param file
	 */
	public void saveWordDataToFile(File file) {
		XStream xstream = new XStream();
		xstream.alias("word", Word.class);

		// Convert ObservableList to a normal ArrayList
		ArrayList<Word> wordList = new ArrayList<>(wordData);

		String xml = xstream.toXML(wordList);
		try {
			FileUtil.saveFile(xml, file);

			setWordFilePath(file);
		} catch (Exception e) { // catches ANY exception
			Dialogs.showErrorDialog(primaryStage,
					"Could not save data to file:\n" + file.getPath(),
					"Could not save data", "Error", e);
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}