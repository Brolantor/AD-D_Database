package ad_d.model.add

import net.liftweb.mapper._
import net.liftweb.util._
import ad_d.util.AD_D
import ad_d.model._
import scala.xml._
import java.util.logging.Logger
import net.liftweb.common._

class Spell extends LongKeyedMapper[Spell] with ManyToMany{
  def getSingleton = Spell
  
//  override def save = super.asInstanceOf[Mapper[Spell]].save
  
  object idspell extends MappedLongIndex[MapperType](this.asInstanceOf[MapperType]) {
    override def writePermission_? = true
    override def dirty_? = true
  }
  object name extends MappedString(this, 45) {
    override def dbIncludeInForm_? = false
  }

  object character_class_idcharacter_class extends MyMappedLongForeignKey(this, Character_class) {
    override def fieldList = (this.get, (Character_class.findAll map (char => (char.idcharacter_class.get.toString -> char.name.get))))
    override def dbNotNull_? = true
  }

  object spell_level extends MappedInt(this) {
    override def dbIncludeInForm_? = false
  }
  object area_of_effect extends MappedString(this, 45) {
    override def dbIncludeInForm_? = false
  }
  object duration extends MappedString(this, 45) {
    override def dbIncludeInForm_? = false
  }
  object ranges extends MappedString(this, 45) {
    override def dbIncludeInForm_? = false
  }
  object casting_time extends MappedString(this, 45) {
    override def dbIncludeInForm_? = false
  }
  object saving_throw extends MappedString(this, 45) {
    override def dbIncludeInForm_? = false
  }
  object verbal_component extends MappedBoolean(this) {
    override def dbIncludeInForm_? = false
    override def asHtml = {
      val disabled = Attribute(None, "disabled", Text("disabled"), Null)
      val value = if (get) Attribute(None, "checked", Text("checked"), Null) else Null
      <input type="checkbox"/> % value % disabled
    }
  }
  object somatic_component extends MappedBoolean(this) {
    override def dbIncludeInForm_? = false
    override def asHtml = {
      val disabled = Attribute(None, "disabled", Text("disabled"), Null)
      val value = if (get) Attribute(None, "checked", Text("checked"), Null) else Null
      <input type="checkbox"/> % value % disabled
    }
  }
  object spell_description extends MappedTextarea(this, -1)
  object commonness_idcommonness extends MyMappedLongForeignKey(this, Commonness) {
    override def fieldList = (this.get, (Commonness.findAll.map(c => (c.idcommonness.get.toString -> c.name.get))))
    override def dbNotNull_? = true
  }
  object magic_school extends MappedManyToMany(Spell_has_magic_school, Spell_has_magic_school.spell_idspell, Spell_has_magic_school.magic_school_idmagic_school, Magic_school) {
    def getFilteredSchool(filter: String) = (this filter (typ => typ.school_type.get == filter)).toList
    def asHtml(typ: String) = this.getFilteredSchool(typ).foldLeft(NodeSeq.fromSeq(<span/>))((start, school) => start ++ NodeSeq.fromSeq(<span> { school.name.get } </span><br/>))
  }
  object material_component extends MappedManyToMany(Spell_has_material_components, Spell_has_material_components.spell_idspell, Spell_has_material_components.material_components_idmaterial_components, Material_components) {
	def asHtml = this.toList.foldLeft(NodeSeq.fromSeq(<span/>))((start, mat) => start ++ NodeSeq.fromSeq(<span> { mat.name.get } </span><br/>))
  }

  override def primaryKeyField = idspell

  def hasMaterialComponents_? = Spell_has_material_components.findAll(By(Spell_has_material_components.spell_idspell, this.idspell.get)).size > 0

  def schools = this.magic_school map (f => f.name.get)

  def school_nodeseq = { schools.foldLeft(NodeSeq.fromSeq(<span/>))((start, x) => start ++ NodeSeq.fromSeq(<span> { x } </span><br/>)) }

}

object Spell extends Spell with LongKeyedMetaMapper[Spell] {
  override def formatFormLine(displayName: NodeSeq, form: NodeSeq) = formatFormElement(<span/>, form)
  override def dbDefaultConnectionIdentifier = AD_D
}

