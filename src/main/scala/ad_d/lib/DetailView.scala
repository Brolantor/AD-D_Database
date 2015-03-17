package ad_d.lib

import net.liftweb.util.Helpers._
import net.liftweb.http.js.JE._
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.RedirectResponse
import scala.xml.NodeSeq
import net.liftweb.mapper._
import net.liftweb.http.{ S, SHtml }
import ad_d.snippet._
import ad_d.model._

abstract class DetailView {

  /*
   * Datenstruktur tableValues:
   * String => 1. Überschrift
   * Any => | MappedField => Anzuzeigender Wert
   * 		| Map[String, MappedField] => 2. Überschrift + Wert (nicht änderbar)
   *   		| Map[String, (MappedField, Boolean)] => 2. Überschrift + Wert (änderbar wenn wahr)
   */
  def tableValues: Map[String, Any]

  /*
   * Datenstruktur tabContents:
   * String => Überschrift des Tabs
   * NodeSeq => Inhalt des Tabs
   */
  def tabContents: Map[String, NodeSeq]
  //  def onSubmit(field: BaseMappedField): scala.xml.Elem
  def title: String
  def caption: String
  def timestamp: String = "01.01.1970"
  def allowEdit = false
  def editOnclick = ""
  def editButton = <input type="button" onclick={ editOnclick } class="SubmitButton" value="Daten ändern" style="font-size: 10px !important"/>
  def saveButton = <input type="submit" class="SubmitButton" value="speichern" style="font-size: 10px !important"/>

  def displayTabs = "#div_firstTab" #> tabContents.map {
    case (heading, content) => {
      "#div_firstTab [class]" #> "dhtmlgoodies_aTab" andThen
        "#div_firstTab [name]" #> heading andThen
        "#div_firstTab *" #> <div>{ content }</div>
    }
  }

  def contentRow(edit: Boolean) = {
    var even = true
    "#tbody_content *" #> tableValues.map {
      case (k, v) => {
        even = !even
        var first = true
        val rowclass = if (even) "even" else ""
        val nextRow = v match {
          case m: Map[_, _] => m.map {
            case (mk, mv) => {
              val value = mv match {
                case (m: BaseMappedField, b: Boolean) => if (b && edit) m._toForm.get else m.asHtml
                case (m: scala.xml.NodeSeq, xml: scala.xml.Elem) => (if (edit) xml else <span/>) ++ m
                case (value, xml: scala.xml.Elem) => (if (edit) xml else <span/>) ++ brString(value.toString)
                case _                                => brString(mv.toString)
              }
              if (first) {
                first = !first
                <tr><th class={ rowclass } rowspan={ m.size.toString }>{ k }</th><td style="font-weight: bold" class={ rowclass }>{ mk.toString }</td><td class={ rowclass }>{ value }</td></tr>
              }
              else
                <tr><td style="font-weight: bold" class={ rowclass }>{ mk.toString }</td><td class={ rowclass }>{ value }</td></tr>
            }
          }
          case s =>
            val value = s match {
              case (m: BaseMappedField, b: Boolean) => if (b && edit) m._toForm.get else m.asHtml
              case (m: scala.xml.NodeSeq, b: Boolean) => m
              case _                                => "test"
            }
            <tr><th class={ rowclass }>{ k }</th><td colspan="2" class={ rowclass }>{ value }</td></tr>
        }
        "#tr_content" #> nextRow
      }
    }
  }

  def display = {
    val editMode = S.param("editable").dmap(0)(_.toInt) == 1
    "#title" #> title &
      "#data_timestamp *" #> timestamp &
      "#caption" #> caption andThen
      contentRow(allowEdit & editMode) andThen
      displayTabs andThen (
        if (allowEdit)
          (if (!editMode) {
          "#span_save_button" #> "" &
            "#span_edit_button" #> editButton
        }
        else {
          "#span_edit_button" #> (saveButton  ++ SHtml.hidden(()=>net.liftweb.http.js.JsCmds.Run("history.back()")))
        })
        else {
          "#span_save_button" #> "" &
            "#span_edit_button" #> ""
        })
  }
}
