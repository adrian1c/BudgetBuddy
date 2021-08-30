package app.view
import app.model._
import app.MainApp
import scalafx.scene.control._
import scalafx.scene.control.Alert.AlertType
import scalafxml.core.macros.sfxml
import scalafx.beans.property.{StringProperty, ObjectProperty}
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.application.Platform
import scalafx.scene.layout.GridPane
import scalafx.geometry.Insets
import scala.util.{Try, Success, Failure}
import scalafx.collections.ObservableBuffer

@sfxml
class PersonOverviewController(

    private val personTable: TableView[Person],
    private val nameColumn: TableColumn[Person, String],
    private val nameLabel: Label,
    private val ageLabel: Label,
    private val occupationLabel: Label,
    private val personIdLabel: Label,

    private val balanceLabel: Label,

    private val accTable: TableView[Account],
    private val accNameColumn: TableColumn[Account, String],
    private val accBalColumn: TableColumn[Account, Double],

    private val historyTable: TableView[History],
    private val histTypeColumn: TableColumn[History, String],
    private val histDescColumn: TableColumn[History, String],
    private val histValColumn: TableColumn[History, String]

) {

    //ChangeListener code
    showPerson()

    showPersonDetails(None);
    personTable.selectionModel().selectedItem.onChange(
        (_,_,newValue) => {
            showPersonDetails(Option(newValue))
        }
    )

    showAccHistory(None);
    accTable.selectionModel().selectedItem.onChange(
        (_,_,newValue) => {
            showAccHistory(Option(newValue))
        }
    )





/**************************************************************************************
**************************** DISPLAY DATA FUNCTIONS ***********************************
***************************************************************************************/

    private def showPerson() = {
        val personBuffer = new ObservableBuffer[Person]()
        personBuffer ++= Person.getAllPersons
        personTable.items = personBuffer
        nameColumn.cellValueFactory = {_.value.name}
    }

    private def showPersonDetails (person: Option[Person]) = {
        person match {
            case Some(person) =>

                nameLabel.text <== person.name
                ageLabel.text = person.age.value.toString
                occupationLabel.text <== person.occupation
                personIdLabel.text = person.personId.value.toString

                val accBuffer = new ObservableBuffer[Account]()
                accBuffer ++= Account.getAllAccounts(person.personId.value)
                if (accBuffer != null){
                accTable.items = accBuffer
                }

                accNameColumn.cellValueFactory = {_.value.name}
                accBalColumn.cellValueFactory = {_.value.balance}

                showAccHistory(None)
            case None =>
                nameLabel.text.unbind()
                occupationLabel.text.unbind()
                nameLabel.text = " "
                ageLabel.text = " "
                occupationLabel.text = " "
                personIdLabel.text = " "
  
        }
    }

    private def showAccHistory (acc: Option[Account]) = {
        acc match {
            case Some(acc) =>
                val newBuffer = new ObservableBuffer[History]()
                newBuffer ++= History.getAllHistory(acc.accountId.value)
                if (newBuffer != null) {
                historyTable.items = newBuffer
                }

                balanceLabel.text = acc.balance.value.toString

                histTypeColumn.cellValueFactory = {_.value.typeH}
                histDescColumn.cellValueFactory = {_.value.desc}
                histValColumn.cellValueFactory = {_.value.value}

            case None => 
                historyTable.getItems().clear()
                balanceLabel.text = " "
        }
    }





/**************************************************************************************
******************************* PERSON FUNCTIONS **************************************
***************************************************************************************/

    def handleCreatePerson (action: ActionEvent) = {
        val person = new Person("")
        val okClicked = MainApp.showPersonEditDialog(person)
            if (okClicked) {
                person.save() match {
                    case Success(value) =>
                        showPerson()
                    case Failure(exception) =>
                        displayFailedDatabaseWarning()
                }
            }
    }

    def handleEditPerson (action: ActionEvent) = {
        val selectedPerson = personTable.selectionModel().selectedItem.value
        if (selectedPerson != null) {
            val okClicked = MainApp.showPersonEditDialog(selectedPerson)
            if (okClicked)
                selectedPerson.save() match {
                    case Success(value) =>
                        showPersonDetails(Option(selectedPerson))
                    case Failure(exception) =>
                        displayFailedDatabaseWarning()
                }
        } else {
            displayNoPersonSelectionWarning()
        }
    }

    def handleRemovePerson(action: ActionEvent) = {
        val selectedPerson = Option(personTable.selectionModel().selectedItem.value)
        selectedPerson match {
            case Some(selectedPerson) => {
                selectedPerson.delete() match {
                    case Success(value) =>
                        val alert = new Alert(AlertType.Confirmation) {
                            initOwner(MainApp.stage)
                            title = "Confirmation"
                            headerText = "Are you sure?"
                            contentText = "Do you really want to remove this person?"
                        }
                        val result = alert.showAndWait()
                        result match {
                            case Some(ButtonType.OK) => showPerson()
                            case _                   => None
                        }
                    case Failure(exception) =>
                        displayFailedDatabaseWarning()
                    }
            }
            case None => displayNoPersonSelectionWarning()
        }
    }





/**************************************************************************************
******************************* ACCOUNT FUNCTIONS *************************************
***************************************************************************************/

    def handleCreateAccount (action: ActionEvent) = {
        val selectedPerson = Option(personTable.selectionModel().selectedItem.value)
        selectedPerson match {
            case Some(selectedPerson) => {
                val account = new Account("", 0.0, selectedPerson.personId.value)
                val okClicked = MainApp.showAccountEditDialog(account)
                    if (okClicked) {
                        account.save() match {
                            case Success(value) =>
                                showPersonDetails(Option(selectedPerson))
                            case Failure(exception) =>
                                displayFailedDatabaseWarning()
                        }
                    }
            }
            case None => displayNoPersonSelectionWarning()        
        } 
    }

    def handleRemoveAccount(action: ActionEvent) = {
        val selectedPerson = Option(personTable.selectionModel().selectedItem.value)
        selectedPerson match {
            case Some(selectedPerson) => {
                val account = Option(accTable.selectionModel().selectedItem.value)
                account match {
                    case Some(account) => {
                            account.delete() match {
                            case Success(x) =>
                                val alert = new Alert(AlertType.Confirmation) {
                                    initOwner(MainApp.stage)
                                    title = "Confirmation"
                                    headerText = "Are you sure?"
                                    contentText = "Do you really want to delete this account?"
                                }
                                val result = alert.showAndWait()
                                result match {
                                    case Some(ButtonType.OK) => showPersonDetails(Option(selectedPerson))
                                    case _                   => None
                                }
                            case Failure(e) =>
                                displayFailedDatabaseWarning()
                            }
                        }
                    case None => displayNoAccountSelectionWarning()
                }
            } case None => displayNoPersonSelectionWarning()
        }
    }




/**************************************************************************************
******************************* HISTORY FUNCTIONS *************************************
***************************************************************************************/    

    def handleAddExpenses(action: ActionEvent) = {
        val selectedAcc = Option(accTable.selectionModel().selectedItem.value)
        selectedAcc match {
            case Some(selectedAcc) => {
                val accountId = selectedAcc.accountId.value
                val history = new History("Expenses", "", 0.0, accountId)
                val okClicked = MainApp.showHistoryEditDialog(history)
                if (okClicked) {
                    minusAccBal(selectedAcc, history.valDouble.value)
                    history.save() match {
                        case Success(value) =>
                            showAccHistory(Option(selectedAcc))
                        case Failure(exception) =>
                            displayFailedDatabaseWarning()
                    }
                }
            }
            case None => displayNoAccountSelectionWarning()
        }
    }


    def handleAddIncome(action: ActionEvent) = {
        val selectedAcc = Option(accTable.selectionModel().selectedItem.value)
        selectedAcc match {
            case Some(selectedAcc) => {
                val accountId = selectedAcc.accountId.value
                val history = new History("Income", "", 0.0, accountId)
                val okClicked = MainApp.showHistoryEditDialog(history)
                if (okClicked) {
                    addAccBal(selectedAcc, history.valDouble.value)
                    history.save() match {
                        case Success(value) =>
                            showAccHistory(Option(selectedAcc))
                        case Failure(exception) =>
                            displayFailedDatabaseWarning()
                    }
                }
            }
            case None => displayNoAccountSelectionWarning()
        }
    }


    def handleUndo(action: ActionEvent) = {
        val selectedAcc = Option(accTable.selectionModel().selectedItem.value)
        selectedAcc match {
            case Some(selectedAcc) => {
                
                try {
                    val histBuffer = new ObservableBuffer[History]()
                    histBuffer ++= History.getAllHistory(selectedAcc.accountId.value)
                    if (histBuffer.last.typeH.value == "Expenses") {
                        val amount = histBuffer.last.valDouble.value
                        addAccBal(selectedAcc, amount)
                        histBuffer.last.delete() match {
                            case Success(x) =>
                                showAccHistory(Option(selectedAcc))
                            case Failure(exception) =>
                                displayFailedDatabaseWarning()
                    }
                    } else {
                        val amount = histBuffer.last.valDouble.value
                        minusAccBal(selectedAcc, amount)
                        histBuffer.last.delete() match {
                            case Success(x) =>
                                showAccHistory(Option(selectedAcc))
                            case Failure(exception) =>
                                displayFailedDatabaseWarning()
                        }
                    }
                } catch {
                    case _: Throwable => displayUndoErrorWarning()
                }
            }
            case None => displayNoAccountSelectionWarning()
        }
    
    }




/**************************************************************************************
***************************** CALCULATION FUNCTIONS ***********************************
***************************************************************************************/

    def addAccBal(acc: Account, newVal: Double) = {
        acc.balance.value += newVal
        acc.save() match {
            case Success(value) => None
            case Failure(exception) =>
            println(exception)
        }
    }

    def minusAccBal(acc: Account, newVal: Double) = {
        acc.balance.value -= newVal
        acc.save() match {
            case Success(value) => None
            case Failure(exception) =>
            println(exception)
        }
    }




/**************************************************************************************
***************************** WARNING ALERT FUNCTIONS *********************************
***************************************************************************************/

    def displayNoPersonSelectionWarning() = {
        val alert = new Alert(AlertType.Warning) {
            initOwner(MainApp.stage)
            title = "No Selection"
            headerText = "No Person Selected"
            contentText = "Please select a person in the table."
        }.showAndWait()
    }

    def displayNoAccountSelectionWarning() = {
        val alert = new Alert(AlertType.Warning) {
            initOwner(MainApp.stage)
            title = "No Selection"
            headerText = "No Account Selected"
            contentText = "Please select an account in the table."
        }.showAndWait()
    }

    def displayFailedDatabaseWarning() = {
        val alert = new Alert(AlertType.Warning) {
            initOwner(MainApp.stage)
            title = "Failed"
            headerText = "Database Error"
            contentText = "Failed to save to database."
        }.showAndWait()
    }

    def displayUndoErrorWarning() = {
        val alert = new Alert(AlertType.Warning) {
            initOwner(MainApp.stage)
            title = "Error"
            headerText = "Undo Failed"
            contentText = "There is nothing to Undo."
        }.showAndWait()
    }

    
    
}