package ad_d.model.add

import net.liftweb.mapper._
import net.liftweb.util._
import ad_d.util.AD_D

class Spell_has_material_components extends LongKeyedMapper[Spell_has_material_components]{
  def getSingleton = Spell_has_material_components 
  object id  extends MappedLongIndex[MapperType](this.asInstanceOf[MapperType])
  object spell_idspell extends MappedLongForeignKey(this, Spell)
  object material_components_idmaterial_components extends MappedLongForeignKey(this, Material_components)
  object anzahl extends MappedInt(this)
  
  override def primaryKeyField = id
}

object Spell_has_material_components extends Spell_has_material_components with LongKeyedMetaMapper[Spell_has_material_components] {
  override def dbDefaultConnectionIdentifier = AD_D
}

