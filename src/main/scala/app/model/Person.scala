package app.model

import scalafx.beans.property.{StringProperty, IntegerProperty, ObjectProperty}
import scalafx.collections.ObservableBuffer
import app.util.Database
import app.model._
import scalikejdbc._
import scala.util.{Try, Success, Failure}

class Person(nameS: String) extends Database {
    
    var name = new StringProperty(nameS)
    var age = ObjectProperty[Int](0)
    var occupation = new StringProperty("")
    val personId = ObjectProperty[Int](0)


    def save(): Try[Int] = {
        if (!(isExist)) {
            Try (DB autoCommit { implicit session =>
                sql"""
                    insert into person (name, age, occupation) values (${name.value}, ${age.value}, ${occupation.value})
                    """.update.apply()
            })
        } else {
            Try (DB autoCommit { implicit session =>
                sql"""
                    update person
                    set
                    name = ${name.value},
                    age = ${age.value},
                    occupation = ${occupation.value}
                    where id = ${personId.value}
                    """.update.apply()
            })
        }
    }


    def delete(): Try[Int] = {
        if (isExist) {
            Try (DB autoCommit { implicit session =>
                sql"""
                    delete from person where
                        id = ${personId.value}
                        """.update.apply()
                        })
        } else {
            throw new Exception("Person does not exist in Database.")
        }
    }

    def isExist: Boolean = {
        DB readOnly { implicit session =>
            sql"""
                select * from person where
                id = ${personId.value}
            """.map(rs => rs.int("id")).single.apply()
            } match {
                case Some(x) => true
                case None => false
            }
    }

}

object Person extends Database {
    def apply (
        nameS: String,
        ageI: Int,
        occS: String,
        pId: Int
    ): Person = {
        new Person(nameS) {
            name.value = nameS
            age.value = ageI
            occupation.value = occS
            personId.value = pId
        }
    }

    def initializeTable() = {
        DB autoCommit { implicit session =>
            sql"""
            create table person (
            id int primary key GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
            name varchar(64),
            age int,
            occupation varchar(64)
            )
            """.execute.apply()}
    }

    //Function to get all existing Person in the database
    def getAllPersons: List[Person] = {
        DB readOnly { implicit session =>
            sql"""
            select * from person
            """.map(rs => Person(rs.string("name"),
            rs.int("age"),rs.string("occupation"), rs.int("id"))).list.apply()
            }
    }

    //Function to get one Person using the personId of Person
    def getSpecificPerson(personId: Int): List[Person] = {
        DB readOnly { implicit session =>
            sql"""
            select * from person
            where id = ${personId}
            """.map(rs => Person(rs.string("name"), rs.int("age"), rs.string("occupation"), rs.int("id"))).list.apply()
            }
    }
}