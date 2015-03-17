package ad_d.model.add

import net.liftweb.mapper._
import net.liftweb.util._
import ad_d.util.AD_D
import ad_d.model._

class Commonness extends LongKeyedMapper[Commonness] {
  def getSingleton = Commonness 
  object idcommonness extends MappedLongIndex[MapperType](this.asInstanceOf[MapperType])
  object name extends MappedString(this, 45)
  
  override def primaryKeyField = idcommonness
}

object Commonness extends Commonness with LongKeyedMetaMapper[Commonness] {
  override def dbDefaultConnectionIdentifier = AD_D
}

