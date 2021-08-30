package app.view

import app.model._
import app.MainApp
import scalafx.scene.control.{TextField, TableColumn, Label, Alert}
import scalafxml.core.macros.sfxml
import scalafx.stage.Stage
import scalafx.Includes._
import scalafx.event.ActionEvent

@sfxml
class PersonEditDialogController (

    private val nameField: TextField,
    private val ageField: TextField,
    private val occField: TextField

) {
    var dialogStage: Stage = null
    private var _person: Person = null
    var okClicked = false

    def person = _person
    def person_=(x: Person) {
        _person = x

        nameField.text = _person.name.value
        ageField.text = _person.age.value.toString
        occField.text = _person.occupation.value
    }
    
    def handleOk (action: ActionEvent) {
        if (isInputValid()) {
            _person.name <== nameField.text
            _person.age.value = ageField.getText().toInt
            _person.occupation <== occField.text

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
        if (nullChecking(occField.text.value))
         errorMessage += "No valid occupation!\n"
        if (nullChecking(ageField.text.value))
            errorMessage += "No valid age!\n"
        else {
            try {
                Integer.parseInt(ageField.getText())
            } catch {
                case e: NumberFormatException =>
                    errorMessage += "No valid age (must be an integer)!\n"
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