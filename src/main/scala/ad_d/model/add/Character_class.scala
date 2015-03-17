package ad_d.model.add

import net.liftweb.mapper._
import net.liftweb.util._
import net.liftweb.common._
import ad_d.util._

class Character_class extends LongKeyedMapper[Character_class]{
  def getSingleton = Character_class
  object idcharacter_class extends MappedLongIndex[MapperType](this.asInstanceOf[MapperType])
  object name extends MappedString(this,45)
  
  override def primaryKeyField = idcharacter_class
}

object Character_class extends Character_class with LongKeyedMetaMapper[Character_class] {
  override def dbDefaultConnectionIdentifier = AD_D
}
