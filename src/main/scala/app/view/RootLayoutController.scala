package app.view
import app.MainApp
import app.model._
import scalafx.event.ActionEvent
import scalafx.application.Platform
import scalafxml.core.macros.sfxml
import scalafx.scene.control._
import scalafx.scene.control.Alert.AlertType
import java.io._

@sfxml
class RootLayoutController {

    //Function to export all data from the database into a .txt file
    def handleExportAll (action: ActionEvent) = {

        val pw = new PrintWriter("exportedData.txt")

        val personLabel = Array("Name", "Age", "Occupation")
        val accountLabel = Array("Name", "Balance")
        val historyLabel = Array("Type", "Description", "Value")
        val personList = Person.getAllPersons

        for (i <- personList) {

            pw.println("PERSON")

            for(i <- personLabel) {
                pw.print(String.format("%-20s", i))
            }
            pw.println()
            pw.println("------------------------------------------------------------------")

            pw.println(String.format("%-20s %-20s %-20s", i.name.value, i.age.value.toString, i.occupation.value))
            pw.println()
            pw.println()

            val id = i.personId.value
            val accList = Account.getAllAccounts(id)

            for (j <- accList) {

                pw.println("\t\tACCOUNT")
                pw.print("\t\t")
                for(i <- accountLabel) {
                    pw.print(String.format("%-20s", i))
                }
                pw.println()
                pw.println("\t\t----------------------------------------------------------")

                pw.println(String.format("\t\t%-20s %-20s", j.name.value, j.balance.value.toString))
                pw.println()
                pw.println()

                val accountId = j.accountId.value
                val histList = History.getAllHistory(accountId)

                pw.println("\t\t\t\tHISTORY")
                pw.print("\t\t\t\t")

                for(k <- historyLabel) {
                    pw.print(String.format("%-20s", k))
                }
                pw.println()
                pw.println("\t\t\t\t--------------------------------------------------")

                for (v <- histList) {
                    pw.println(String.format("\t\t\t\t%-20s %-20s %-20s", v.typeH.value, v.desc.value, v.value.value))
                }

                pw.println()
                pw.println()
            }

            pw.println()
            pw.println()
            pw.println()

        }

        pw.close()

        new Alert(AlertType.Information) {
            initOwner(MainApp.stage)
            title = "Export Data"
            headerText = "Success!"
            contentText = "The data has been successfully exported"
        }.showAndWait()

    }

    //Function to close the application
    def handleCloseApplication (action: ActionEvent) = {
        Platform.exit();
        System.exit(0);
    }


}