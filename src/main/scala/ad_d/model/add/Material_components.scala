package ad_d.model.add

import net.liftweb.mapper._
import net.liftweb.util._
import ad_d.util.AD_D
import ad_d.model._

class Material_components extends LongKeyedMapper[Material_components] with ManyToMany {
  def getSingleton = Material_components 
  object idmaterial_components extends MappedLongIndex[MapperType](this.asInstanceOf[MapperType])
  object name extends MappedString(this, 45)
  object price extends MappedDecimal(this,BigDecimal(10,2))
  object commonness_idcommonness extends MyMappedLongForeignKey(this, Commonness) {
    override def fieldList = (this.get, (Commonness.findAll.map(c => (c.idcommonness.get.toString -> c.name.get))))
    override def dbIncludeInForm_? = false
  }
  
  object spell extends MappedManyToMany(Spell_has_material_components, Spell_has_material_components.spell_idspell, Spell_has_material_components.material_components_idmaterial_components, Material_components)
  
  override def primaryKeyField = idmaterial_components
}

object Material_components extends Material_components with LongKeyedMetaMapper[Material_components] {
  override def dbDefaultConnectionIdentifier = AD_D
}

