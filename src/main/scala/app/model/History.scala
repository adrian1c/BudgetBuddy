package app.model

import scalafx.beans.property.{StringProperty, IntegerProperty, ObjectProperty}
import scalafx.collections.ObservableBuffer
import app.util.Database
import app.model._
import scalikejdbc._
import scala.util.{Try, Success, Failure}

class History(typeS: String, descS: String, valS: Double, aId: Int) extends Database {
        
        var typeH = new StringProperty(typeS)
        var desc = new StringProperty(descS)
        var accountId = aId
        val histId = ObjectProperty[Int](0)

        var valDouble = ObjectProperty[Double](valS)
        var valueToString = valDouble.value.toString
        if (typeH.value == "Expenses") {
                valueToString = "- " + valueToString
        } else {
                valueToString = "+ " + valueToString
        }
        var value = new StringProperty(valueToString)
        
        
        def save(): Try[Int] = {
                if (!(isExist)) {
                        Try (DB autoCommit { implicit session =>
                                sql"""
                                        insert into history (histType, histDesc, histVal, FKAccountId) values
                                        (${typeH.value}, ${desc.value}, ${valDouble.value}, ${accountId})
                                """.update.apply()
                        })
                } else {
                        Try (DB autoCommit { implicit session =>
                                sql"""
                                        update history
                                        set 
                                        histTpye = ${typeH.value},
                                        histDesc = ${desc.value},
                                        histVal = ${valDouble.value}
                                        where histId = ${histId.value}
                                """.update.apply()
                        })
                }
        }

        def delete(): Try[Int] = {
                if (isExist) {
                Try (DB autoCommit { implicit session =>
                        sql"""
                        delete from history where
                                histId = ${histId.value}
                                """.update.apply()
                                })
                } else {
                throw new Exception("History does not exist in Database.")
                }
        }

        def isExist: Boolean = {
                DB readOnly { implicit session =>
                sql"""
                        select * from history where
                        histId = ${histId.value}
                """.map(rs => rs.int("histId")).single.apply()
                } match {
                        case Some(x) => true
                        case None => false
                }
        }

}

object History extends Database {
        def apply (
                typeS: String,
                descS: String,
                valS: Double,
                aId: Int,
                histIdI: Int
        ): History = {
                new History(typeS, descS, valS, aId) {
                        typeH.value = typeS
                        desc.value = descS
                        valDouble.value = valS
                        accountId = aId
                        histId.value = histIdI
                }
        }

        def initializeTable() = {
                DB autoCommit { implicit session =>
                        sql"""
                        create table history (
                        histId int primary key GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
                        histType varchar(64),
                        histDesc varchar(128),
                        histVal double,
                        FKAccountId int,
                        foreign key (FKAccountId) REFERENCES account(accId) ON DELETE CASCADE
                        )
                        """.execute.apply()}
        }

        //Function to get all History under an account using accountId of Account
        def getAllHistory(index: Int): List[History] = {
                DB readOnly { implicit session =>
                        sql"""
                                select *
                                from history as h
                                inner join account as a
                                on a.accId = h.FKAccountId
                                where a.accId = ${index}
                        """.map(rs => History(rs.string("histType"),
                        rs.string("histDesc"), rs.double("histVal"), rs.int("FKAccountId"), rs.int("histId"))).list.apply()
                        }
        }
}