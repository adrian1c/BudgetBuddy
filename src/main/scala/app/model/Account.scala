package app.model

import scalafx.beans.property.{StringProperty, IntegerProperty, ObjectProperty}
import scalafx.collections.ObservableBuffer
import app.util.Database
import app.model._
import scalikejdbc._
import scala.util.{Try, Success, Failure}

class Account(nameS: String, balanceS: Double, pId: Int) extends Database {

    var name = new StringProperty(nameS)
    var balanceDouble = balanceS
    var balance = ObjectProperty[Double](balanceDouble)
    val accountId = ObjectProperty[Int](0)
    var personId = pId


    def save(): Try[Int] = {
        if (!(isExist)) {
            Try (DB autoCommit { implicit session =>
                sql"""
                    insert into account (accName, accBalance, FKPersonId) values (${name.value}, ${balance.value}, ${personId})
                    """.update.apply()
            })
        } else {
            Try (DB autoCommit { implicit session =>
                sql"""
                    update account
                    set
                    accBalance = ${balance.value}
                    where accId = ${accountId.value}
                    """.update.apply()
            })
        }
    }

    def delete(): Try[Int] = {
        if (isExist) {
            Try (DB autoCommit { implicit session =>
                sql"""
                    delete from account where
                    accId = ${accountId.value}
                    """.update.apply()
                        })
        } else {
            throw new Exception("Account does not exist in Database.")
        }
    }

    def isExist: Boolean = {
        DB readOnly { implicit session =>
            sql"""
                select * from account where
                accId = ${accountId.value}
            """.map(rs => rs.int("accId")).single.apply()
            } match {
                case Some(x) => true
                case None => false
            }
    }

}

object Account extends Database {
    def apply (
        nameS: String,
        balanceS: Double,
        pId: Int,
        accId: Int
    ): Account = {
        new Account(nameS, balanceS, pId) {
            name.value = nameS
            balance.value = balanceS
            personId = pId
            accountId.value = accId
        }
    }

    def initializeTable() = {
        DB autoCommit { implicit session =>
            sql"""
            create table account (
            accId int primary key GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
            accName varchar(64),
            accBalance double,
            FKPersonId int,
            foreign key (FKPersonId) REFERENCES person(id) ON DELETE CASCADE
            )
            """.execute.apply()}
    }

    //Function to get all accounts under a person using personId of Person
    def getAllAccounts(index: Int): List[Account] = {
        DB readOnly { implicit session =>
            sql"""
                select * 
                from account as a
                inner join person as p
                on p.id = a.FKPersonId
                where p.id = ${index}
                """.map(rs => Account(rs.string("accName"),
            rs.double("accBalance"), rs.int("FKPersonId"), rs.int("accId"))).list.apply()
            }
    }

    //Function to get one account using accountId of Account
    def getSpecificAccount(index: Int): List[Account] = {
        DB readOnly { implicit session =>
            sql"""
            select * from account
            where accid = ${index}
            """.map(rs => Account(rs.string("accName"), rs.double("accBalance"), rs.int("FKPersonId"), rs.int("accId"))).list.apply()
            }
    }

}