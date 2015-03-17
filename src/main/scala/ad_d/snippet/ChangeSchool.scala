package ad_d.snippet

import scala.collection.immutable.ListMap
import scala.xml._
import net.liftweb.common.{ Empty, Full, Logger }
import net.liftweb.http.{ S, SHtml }
import net.liftweb.mapper._
import net.liftweb.util.Helpers._
import ad_d.lib._
import ad_d.model.add._

class ChangeSchool {
  //  val queryResult = Projektteil_has_nutzungsleitung.findAll(By(Projektteil_has_nutzungsleitung.projektteil_idprojektteil, idprojektteil), By(Projektteil_has_nutzungsleitung.status, "Nutzungsleiter")).head

  val listOfSchools = (Magic_school.findAll(By(Magic_school.school_type, "Philosophy"),OrderBy(Magic_school.idmagic_school,Ascending)) map (_.name), Magic_school.findAll(By(Magic_school.school_type, "Effect"),OrderBy(Magic_school.idmagic_school,Ascending)) map (_.name), Magic_school.findAll(By(Magic_school.school_type, "Thaumaturgy"),OrderBy(Magic_school.idmagic_school,Ascending)) map (_.name))
  val idspell: Int = S.param("idspell").dmap(1)(_.toInt)

  def title = Spell.find(idspell).get.name.get
  def fieldnames = List("Schule", "Typ")

  def createNewAssignment(idspell: Int) = {
    Spell_has_magic_school.create
      .spell_idspell(idspell)
      .saveMe
  }

  def deleteNl(assignId: Long) = {
    Spell_has_magic_school.find(assignId).get.delete_!
  }
  
  def listOfAssignemnts = Spell_has_magic_school.findAll(By(Spell_has_magic_school.spell_idspell, idspell)) map (_.magic_school_idmagic_school.get)

  def detailName = "changeNlDetail"

  def contentRow(list: (List[Any], List[Any], List[Any])) = {
    "#tr_content_philosophy *" #> list._1.zipWithIndex.map {
      case (row, i) => {
        val idAttribute = i + 1  
        val rowExt = SHtml.checkbox_id(listOfAssignemnts.contains(idAttribute), _=>"", Full(idAttribute.toString)) :: row :: Nil
        "td [class]" #> (if (i % 2 == 0) "even" else "") &
          "#td_content *" #> rowExt.map(row => row match {
            case row: scala.xml.Elem    => "#span_content" #> row
            case row: scala.xml.NodeSeq => "#span_content" #> row
            case _                      => "#span_content" #> row.toString
          })
      }
    }
  } & {
    "#tr_content_effect *" #> list._2.zipWithIndex.map {
      case (row, i) => {
        val idAttribute = i + 10
        val rowExt = SHtml.checkbox_id(listOfAssignemnts.contains(idAttribute), _=>"", Full(idAttribute.toString)) :: row :: Nil
        "td [class]" #> (if (i % 2 == 0) "even" else "") &
          "#td_content *" #> rowExt.map(row => row match {
            case row: scala.xml.Elem    => "#span_content" #> row
            case row: scala.xml.NodeSeq => "#span_content" #> row
            case _                      => "#span_content" #> row.toString
          })
      }

    }
  } & {
    "#tr_content_thaumaturgy *" #> list._3.zipWithIndex.map {
      case (row, i) => {
        val idAttribute = i + 18
        val rowExt = SHtml.checkbox_id(listOfAssignemnts.contains(idAttribute), _=>"", Full(idAttribute.toString)) :: row :: Nil
        "td [class]" #> (if (i % 2 == 0) "even" else "") &
          "#td_content *" #> rowExt.map(row => row match {
            case row: scala.xml.Elem    => "#span_content" #> row
            case row: scala.xml.NodeSeq => "#span_content" #> row
            case _                      => "#span_content" #> row.toString
          })
      }

    }
  }

  def display = "#span_title" #> title andThen "#div_checkboxlist *" #> contentRow(listOfSchools)

}
