package ad_d.snippet

import ad_d.util._
import ad_d.lib._
import ad_d.model.add._
import net.liftweb.http.js._
import net.liftweb.http.js.JsCmds._
import net.liftweb.util.Helpers._
import net.liftweb.http.SHtml
import net.liftweb.common.Logger
import net.liftweb.mapper.{ By, OrderBy, Ascending, Like }
import net.liftweb.common.{ Empty, Full }
import net.liftweb.http.S
import scala.xml.NodeSeq

class SpellList extends ListViewSnippet {
  override def title = "Liste Sprüche"
  override def detailName = "spell"

  def level = S.attr("level") match {
    case Full(i) => i.toInt
    case _       => 0
  }

  override def fieldnames = List("Level", "Name", "Casting Time", "Range", "Area of Effect", "Duration", "Comp.", "School")

  override def values = {
    val magie = S.attr("typ").get.toInt
    val dataList = Spell.findAll(By(Spell.character_class_idcharacter_class, magie), OrderBy(Spell.spell_level, Ascending), OrderBy(Spell.name, Ascending))

    dataList.map(
      spell => {
        val compon_list = (if (spell.verbal_component.get) "V" else null) :: (if (spell.somatic_component.get) "S" else null) :: (if (spell.hasMaterialComponents_?) "M" else null) :: Nil
        val comp_string = compon_list.filter(_.isInstanceOf[String]) mkString ("", ";", "")
        val nodeSeq = spell.schools.foldLeft(NodeSeq.fromSeq(<span />))((x,sch) => x ++ NodeSeq.fromSeq(<span>{ sch }</span> ++ NodeSeq.fromSeq(<br />)))
        
        (spell.idspell.toString, List(spell.spell_level, spell.name, spell.casting_time, spell.ranges, spell.area_of_effect, spell.duration, comp_string, nodeSeq))
      })
  }
}
