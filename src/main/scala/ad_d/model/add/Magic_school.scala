package ad_d.model.add

import net.liftweb.mapper._
import net.liftweb.util._
import ad_d.util.AD_D
import ad_d.model._

class Magic_school extends LongKeyedMapper[Magic_school] with ManyToMany{
  def getSingleton = Magic_school 
  object idmagic_school extends MappedLongIndex[MapperType](this.asInstanceOf[MapperType])
  object name extends MappedString(this, 45)
  object school_type extends MappedString(this, 45)
  
  object spell extends MappedManyToMany(Spell_has_magic_school, Spell_has_magic_school.spell_idspell, Spell_has_magic_school.magic_school_idmagic_school, Magic_school)
  
  override def primaryKeyField = idmagic_school
}

object Magic_school extends Magic_school with LongKeyedMetaMapper[Magic_school] {
  override def dbDefaultConnectionIdentifier = AD_D
}

