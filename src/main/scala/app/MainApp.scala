package app
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.stage.Stage
import scalafx.scene.Scene
import scalafx.Includes._
import scalafx.scene.control.{Alert, ButtonType, Label}

import scalafxml.core.{NoDependencyResolver, FXMLView, FXMLLoader}
import javafx.{scene => jfxs}
import scalafx.collections.ObservableBuffer
import app.model._
import app.view._
import scalafx.beans.property.StringProperty
import scalafx.stage.Modality
import app.util.Database

object MainApp extends JFXApp {

  Database.setupDB()

  val rootResource = getClass.getResource("view/RootLayout.fxml")
  val loader = new FXMLLoader(rootResource, NoDependencyResolver)
  loader.load();
  val roots = loader.getRoot[jfxs.layout.BorderPane]
  stage = new PrimaryStage {
    title = "Budget Buddy"
    scene = new Scene {
      root = roots
    }
  }

  def showPersonOverview() = {
    val resource = getClass.getResource("view/PersonOverview.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load();
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(roots)
  }
  showPersonOverview()

  def showPersonEditDialog(person: Person): Boolean = {
    val resource = getClass.getResource("view/PersonEditDialog.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load();
    val roots2  = loader.getRoot[jfxs.Parent]
    val control = loader.getController[PersonEditDialogController#Controller]

    val dialog = new Stage() {
      initModality(Modality.APPLICATION_MODAL)
      initOwner(stage)
      scene = new Scene {
        root = roots2
      }
    }
    control.dialogStage = dialog
    control.person = person
    dialog.showAndWait()
    control.okClicked
  } 

  def showAccountEditDialog(account: Account): Boolean = {
    val resource = getClass.getResource("view/AccountEditDialog.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load();
    val roots2  = loader.getRoot[jfxs.Parent]
    val control = loader.getController[AccountEditDialogController#Controller]

    val dialog = new Stage() {
      initModality(Modality.APPLICATION_MODAL)
      initOwner(stage)
      scene = new Scene {
        root = roots2
      }
    }
    control.dialogStage = dialog
    control.account = account
    dialog.showAndWait()
    control.okClicked
  } 

  def showHistoryEditDialog(history: History): Boolean = {
    val resource = getClass.getResource("view/HistoryEditDialog.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load();
    val roots2  = loader.getRoot[jfxs.Parent]
    val control = loader.getController[HistoryEditDialogController#Controller]

    val dialog = new Stage() {
      initModality(Modality.APPLICATION_MODAL)
      initOwner(stage)
      scene = new Scene {
        root = roots2
      }
    }
    control.dialogStage = dialog
    control.history = history
    dialog.showAndWait()
    control.okClicked
  } 

}
