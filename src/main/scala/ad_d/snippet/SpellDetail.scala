package ad_d.snippet

import scala.collection.immutable.ListMap
import scala.xml.Text
import net.liftweb.common.{ Empty, Full }
import net.liftweb.http.S
import net.liftweb.mapper._
import net.liftweb.util.Helpers._
import ad_d.lib._
import ad_d.model.add._

class SpellDetail extends DetailView {
  val idSpell = S.param("idspell").dmap(1)(_.toInt)
  val queryResult = Spell.find(idSpell).get
  val schollQuery = Spell_has_magic_school.findAll(By(Spell_has_magic_school.spell_idspell, idSpell))

  override def allowEdit = true

  override def tabContents = ListMap(
    "Beschreibung" -> brString(queryResult.spell_description.get),
    "Materialien" -> <span/>
  )

  override def title = "Zauberspruch Details"

  override def caption = queryResult.name.get

  override def editOnclick = "window.location.pathname='" + net.liftweb.http.LiftRules.context.path + "/spell/edit_detail/" + idSpell + "'"

  override def tableValues = {
    val changeMatLink = (<a href={ "/spell/change_mat/" + idSpell }><img style="margin-right: 10px; border: 0; vertical-align: top;" src="/images/web/edit_general.png"/></a>)
    val changeSchoolLink = (<a href={ "/spell/change_school/" + idSpell }><img style="margin-right: 10px; border: 0; vertical-align: top;" src="/images/web/edit_general.png"/></a>)
    ListMap(
      "Klasse" -> (queryResult.character_class_idcharacter_class, true),
      "Stufe" -> (queryResult.spell_level, true),
      "Attribute" -> (Map("Duration" -> (queryResult.duration, true),
        "Range" -> (queryResult.ranges, true),
        "Area of Effect" -> (queryResult.area_of_effect, true),
        "Casting Time" -> (queryResult.casting_time, true),
        "Saving Throw" -> (queryResult.saving_throw, true))),
      "Häufigkeit" -> (queryResult.commonness_idcommonness, true),
      "Komponenten" -> Map("Verbal" -> (queryResult.verbal_component, true),
        "Somatic" -> (queryResult.somatic_component, true),
        "Materialien" -> (queryResult.material_component.asHtml, changeMatLink)),
      "Schulen" -> Map("Philosophy" -> (queryResult.magic_school.asHtml("Philosophy"), changeSchoolLink),
        "Effect" -> (queryResult.magic_school.asHtml("Effect"), changeSchoolLink),
        "Thaumaturgy" -> (queryResult.magic_school.asHtml("Thaumaturgy"), changeSchoolLink))
    )
  }

  override def display = super.display andThen "#span_save_button" #> queryResult.toForm(Empty, _.save)
}
