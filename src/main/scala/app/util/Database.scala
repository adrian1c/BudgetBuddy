package app.util

import scalikejdbc._
import app.model._

trait Database {
    val derbyDriverClassname = "org.apache.derby.jdbc.EmbeddedDriver"


    val dbURL = "jdbc:derby:budgetApp;create=true;"
    Class.forName(derbyDriverClassname)

    ConnectionPool.singleton(dbURL, "user", "pass")

    implicit val session = AutoSession

}

object Database extends Database {
    def setupDB() = {
        if (!hasDBInitializePerson)
            Person.initializeTable()
        if (!hasDBInitializeAccount)
            Account.initializeTable()
        if (!hasDBInitializeHistory)
            History.initializeTable()
    }
    
    def hasDBInitializePerson: Boolean = {
        DB getTable "Person" match {
            case Some(x) => true
            case None => false
        }
    }

    def hasDBInitializeAccount: Boolean = {
        DB getTable "Account" match {
            case Some(x) => true
            case None => false
        }
    }

    def hasDBInitializeHistory: Boolean = {
        DB getTable "History" match {
            case Some(x) =>  true
            case None => false
        }
    }

}