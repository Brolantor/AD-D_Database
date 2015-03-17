package ad_d.model.add

import net.liftweb.mapper._
import net.liftweb.util._
import ad_d.util.AD_D

class Spell_has_magic_school extends LongKeyedMapper[Spell_has_magic_school] with IdPK {
  def getSingleton = Spell_has_magic_school 
  object spell_idspell extends MappedLongForeignKey(this, Spell)
  object magic_school_idmagic_school extends MappedLongForeignKey(this, Magic_school)
}

object Spell_has_magic_school extends Spell_has_magic_school with LongKeyedMetaMapper[Spell_has_magic_school] {
  override def dbDefaultConnectionIdentifier = AD_D
}

