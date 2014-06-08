package com.vladinooo.wordtracker;

import com.vladinooo.wordtracker.model.Word;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Dialogs;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class WordOverviewController {

	@FXML
	private TableView<Word> wordTable;
	@FXML
	private TableColumn<Word, String> wordColumn;

	@FXML
	private Label wordEnLabel;
	@FXML
	private Label wordRuLabel;
	@FXML
	private Label wordMeaningLabel;

	// Reference to the main application
	private MainApp mainApp;

	/**
	 * The constructor. The constructor is called before the initialize()
	 * method.
	 */
	public WordOverviewController() {

	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		
		// Initialize the word table
		wordColumn.setCellValueFactory(new PropertyValueFactory<Word, String>(
				"wordEn"));

		// Auto resize columns
		wordTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		// clear word
		showWordDetails(null);

		// Listen for selection changes
		wordTable.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<Word>() {

					@Override
					public void changed(
							ObservableValue<? extends Word> observable,
							Word oldValue, Word newValue) {
						showWordDetails(newValue);
					}
				});
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param mainApp
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;

		// Add observable list data to the table
		wordTable.setItems(mainApp.getWordData());
	}

	/**
	 * Fills all text fields to show details about the word. If the specified
	 * word is null, all text fields are cleared.
	 * 
	 * @param word
	 *            the word or null
	 */
	private void showWordDetails(Word word) {
		if (word != null) {
			wordEnLabel.setText(word.getWordEn());
			wordRuLabel.setText(word.getWordRu());
			wordMeaningLabel.setText(word.getWordMeaning());
		} else {
			wordEnLabel.setText("");
			wordRuLabel.setText("");
			wordMeaningLabel.setText("");
		}
	}

	/**
	 * Called when the user clicks on the delete button.
	 */
	@FXML
	private void handleDeleteWord() {
		int selectedIndex = wordTable.getSelectionModel().getSelectedIndex();
		if (selectedIndex >= 0) {
			wordTable.getItems().remove(selectedIndex);
		} else {
			// Nothing selected
			Dialogs.showWarningDialog(mainApp.getPrimaryStage(),
					"Please select a word in the table.", "No Word Selected",
					"No Selection");
		}
	}

	/**
	 * Called when the user clicks the new button. Opens a dialog to edit
	 * details for a new word.
	 */
	@FXML
	private void handleNewWord() {
		Word tempWord = new Word();
		boolean okClicked = mainApp.showWordEditDialog(tempWord);
		if (okClicked) {
			mainApp.getWordData().add(tempWord);
		}
	}

	/**
	 * Called when the user clicks the edit button. Opens a dialog to edit
	 * details for the selected word.
	 */
	@FXML
	private void handleEditWord() {
		Word selectedWord = wordTable.getSelectionModel().getSelectedItem();
		if (selectedWord != null) {
			boolean okClicked = mainApp.showWordEditDialog(selectedWord);
			if (okClicked) {
				refreshWordTable();
				showWordDetails(selectedWord);
			}

		} else {
			// Nothing selected
			Dialogs.showWarningDialog(mainApp.getPrimaryStage(),
					"Please select a word in the table.",
					"No Word Selected", "No Selection");
		}
	}

	/**
	 * Refreshes the table. This is only necessary if an item that is already in
	 * the table is changed. New and deleted items are refreshed automatically.
	 * 
	 * This is a workaround because otherwise we would need to use property
	 * bindings in the model class and add a *property() method for each
	 * property. Maybe this will not be necessary in future versions of JavaFX
	 * (see http://javafx-jira.kenai.com/browse/RT-22599)
	 */
	private void refreshWordTable() {
		int selectedIndex = wordTable.getSelectionModel().getSelectedIndex();
		wordTable.setItems(null);
		wordTable.layout();
		wordTable.setItems(mainApp.getWordData());
		// Must set the selected index again (see
		// http://javafx-jira.kenai.com/browse/RT-26291)
		wordTable.getSelectionModel().select(selectedIndex);
	}
}
