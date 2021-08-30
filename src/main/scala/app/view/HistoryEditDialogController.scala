package app.view

import app.model._
import app.MainApp
import scalafx.scene.control.{TextField, TableColumn, Label, Alert}
import scalafxml.core.macros.sfxml
import scalafx.stage.Stage
import scalafx.Includes._
import scalafx.event.ActionEvent

@sfxml
class HistoryEditDialogController (

    private val descField: TextField,
    private val valField: TextField

) {
    var dialogStage: Stage = null
    private var _history: History = null
    var okClicked = false

    def history = _history
    def history_=(x: History) {
        _history = x

        descField.text = _history.desc.value
        valField.text = ""
    }

    def handleOk (action: ActionEvent) {
        if (isInputValid()) {
            _history.desc <== descField.text
            _history.valDouble.value = valField.getText().toDouble
            

            okClicked = true
            dialogStage.close()
        }
    }

    def handleCancel (action: ActionEvent) {
        dialogStage.close()
    }

    def nullChecking (x: String) = x == null || x.length == 0

    def isInputValid(): Boolean = {
        var errorMessage = ""

        if (nullChecking(descField.text.value))
            errorMessage += "No valid description!\n"
        if (nullChecking(valField.text.value))
         errorMessage += "No valid value!\n"
        else {
            try {
                valField.getText().toDouble
            } catch {
                case e: NumberFormatException =>
                    errorMessage += "Not a valid value. Must contain only numbers and decimals!\n"
            }
        }

        if (errorMessage.length() == 0) {
            return true
        } else {
            val alert = new Alert(Alert.AlertType.Error){
            initOwner(dialogStage)
            title = "Invalid Fields"
            headerText = "Please correct invalid fields"
            contentText = errorMessage
            }.showAndWait()
            return false
        }
    }

}