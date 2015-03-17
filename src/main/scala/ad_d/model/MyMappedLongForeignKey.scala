package ad_d.model

import scala.xml.{ Elem, Attribute, Text, Null }
import net.liftweb.common._
import net.liftweb.http.{ S, SHtml }
import net.liftweb.mapper._
import S._
import scala.collection.immutable.ListMap

abstract class MyMappedLongForeignKey[T <: Mapper[T], O <: KeyedMapper[Long, O]](theOwner: T, _foreignMeta: => KeyedMetaMapper[Long, O]) extends MappedLongForeignKey(theOwner, _foreignMeta) {
  def fieldList: (Any, List[(String, String)])
  
  override def dbIncludeInForm_? = false

  override def _toForm: Box[Elem] = {
    val id = this.name + "_field"
    val nullField = if (dbNotNull_?) Null else <option value="NULL"></option>
    fmapFunc({ s: List[String] => this.setFromAny(s) }) {
      name =>
        Full(appendFieldId(<select id={ id } name={ name }> { nullField } {
          fieldList._2 map ({
            case (k, v) => {
              val selectedNode = if (fieldList._1.toString == k) Attribute(None, "selected", Text("selected"), Null) else Null
              <option value={ k }>{ v }</option> % selectedNode
            }
          })
        }</select>))
    }
  }

  override def asHtml = {
      def value = if (this.setFromAny(fieldList._1) > 0) fieldList._2.toMap apply (fieldList._1.toString) else ""
    <span>{ value }</span>
  }
}
