package com.vladinooo.wordtracker;

import javafx.fxml.FXML;
import javafx.scene.control.Dialogs;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import com.vladinooo.wordtracker.model.Word;

public class WordEditDialogController {

	@FXML
	private TextField wordEnField;
	@FXML
	private TextField wordRuField;
	@FXML
	private TextArea wordMeaningTextArea;

	private Stage dialogStage;
	private Word word;
	private boolean okClicked = false;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {

	}

	/**
	 * Sets the stage of this dialog.
	 * 
	 * @param dialogStage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	/**
	 * Sets the word to be edited in the dialog.
	 * 
	 * @param word
	 */
	public void setWord(Word word) {
		this.word = word;

		wordEnField.setText(word.getWordEn());
		wordRuField.setText(word.getWordRu());
		wordMeaningTextArea.setText(word.getWordMeaning());
	}

	/**
	 * Returns true if the user clicked OK, false otherwise.
	 * 
	 * @return
	 */
	public boolean isOkClicked() {
		return okClicked;
	}

	/**
	 * Called when the user clicks ok.
	 */
	@FXML
	private void handleOk() {
		if (isInputValid()) {
			word.setWordEn(wordEnField.getText());
			word.setWordRu(wordRuField.getText());
			word.setWordMeaning(wordMeaningTextArea.getText());

			okClicked = true;
			dialogStage.close();
		}
	}

	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

	/**
	 * Validates the user input in the text fields.
	 * 
	 * @return true if the input is valid
	 */
	private boolean isInputValid() {
		String errorMessage = "";

		if (wordEnField.getText() == null
				|| wordEnField.getText().length() == 0) {
			errorMessage += "No valid word (en)!\n";
		}
		if (wordRuField.getText() == null
				|| wordRuField.getText().length() == 0) {
			errorMessage += "No valid word (ru)!\n";
		}

		if (errorMessage.length() == 0) {
			return true;
		} else {
			// Show the error message
			Dialogs.showErrorDialog(dialogStage, errorMessage,
					"Please correct invalid fields", "Invalid Fields");
			return false;
		}
	}
}