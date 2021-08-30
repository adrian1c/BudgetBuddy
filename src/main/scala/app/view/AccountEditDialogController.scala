package app.view

import app.model.Account
import app.MainApp
import scalafx.scene.control.{TextField, TableColumn, Label, Alert}
import scalafxml.core.macros.sfxml
import scalafx.stage.Stage
import scalafx.Includes._
import scalafx.event.ActionEvent

@sfxml
class AccountEditDialogController (

    private val nameField: TextField,
    private val balField: TextField

) {
    var dialogStage: Stage = null
    private var _account: Account = null
    var okClicked = false

    def account = _account
    def account_=(x: Account) {
        _account = x

        nameField.text = _account.name.value
        balField.text = ""
    }

    def handleOk (action: ActionEvent) {
        if (isInputValid()) {
            _account.name <== nameField.text
            _account.balance.value = balField.getText().toDouble

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

        if (nullChecking(nameField.text.value))
            errorMessage += "No valid name!\n"
        if (nullChecking(balField.text.value))
            errorMessage += "No valid balance!\n"
        else {
            try {
                balField.getText().toDouble
            } catch {
                case e: NumberFormatException =>
                    errorMessage += "Not a valid balance. Must contain only numbers and decimals"
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