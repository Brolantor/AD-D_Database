package ad_d.lib

import net.liftweb.common.Full
import net.liftweb.http.SHtml._
import net.liftweb.http.{ S, WiringUI }
import net.liftweb.http.js.{ JsCmd, JsCmds }
import net.liftweb.http.js.JE.JsRaw
import net.liftweb.http.js.jquery._
import net.liftweb.util.Helpers._
import net.liftweb.util.ValueCell
import scala.collection.mutable.Map
import scala.xml.{ Elem, NodeSeq, Node }
import scala.util.Random

/**
 * @author mdraeger,tbrohl
 *
 * <pre>
 * <!-- table template for all listings -->
 * <div id="listview">
 * <div id="selectorbox">
 * <table class="selectorbox">
 * <tbody>
 * <tr>
 * <td><img src="/images/listview/sel_1.gif"></td>
 * <td style="cursor:pointer" id="td_button_start"><img src="/images/listview/sel_2.gif"	title="Anfang"></td>
 * <td style="cursor:pointer" id="td_button_back"><img src="/images/listview/sel_3.gif" title="Zurück"></td>
 * <td><img src="/images/listview/sel_4.gif"></td>
 * <td class="selectornumber"><span style="text-align: center;" id="span_selectorbox_start">1</span> bis <span style="text-align: center;" id="span_selectorbox_end">1</span> von <span id="span_selectorbox_total">Gesamt</span></td>
 * <td><img src="/images/listview/sel_6.gif"></td>
 * <td style="cursor:pointer" id="td_button_next"><img src="/images/listview/sel_7.gif" title="Weiter"></td>
 * <td style="cursor:pointer" id="td_button_end"><img src="/images/listview/sel_8.gif" title="Zum Ende"></td>
 * <td><img src="/images/listview/sel_9.gif"></td>
 * <td style="vertical-align: middle; padding-left: 1em"><lift:embed what="listview/site_selection_dropdown" eager="true" /></td>
 * </tr>
 * </tbody>
 * </table>
 * </div>
 * <table class="colored">
 * <thead>
 * <tr>
 * <td style="text-align: center;" id="searchfieldcell">
 * <input id="searchinput" style="padding: 3px 3px; text-align: left; width: 90%" />
 * </td>
 * </tr>
 * </thead>
 * <tbody id="tbody_list_view">
 * <tr>
 * <th id="th_header" class="px120 rightalign"><div id="div_header"><span id="span_header"></span></div></th>
 * </tr>
 * <tr id="tr_content">
 * <td id="td_content"><span id="span_content"><span>Actual content</span></span></td>
 * </tr>
 * </tbody>
 * </table>
 * </div>
 * </pre>
 *
 * Abstract class that has to be implemented by snippets that display a list of values in a table.
 * The implementing class needs to override the fieldnames (table captions) and the actual values.
 */
abstract class ListViewSnippet {
  val LIST_LENGTH = 25

  def fieldnames: List[String]
  def values: List[(String, List[Any])]
  def title: String
  def detailName: String

  val sourceValues = ValueCell[List[(String, List[Any])]](values)
  val filteredContents = ValueCell[List[(String, List[Any])]](values)
  val page = ValueCell[Int](0)
  val orderedAsc = ValueCell[(Int, Boolean)](0, false)
  val currentContents = ValueCell[List[(String, List[Any])]](values take (LIST_LENGTH))
  val num_elems = filteredContents.lift(_.size)
  val num_pages = num_elems.lift(_ / LIST_LENGTH)
  val start_elem = page.lift(_ * LIST_LENGTH + 1)
  val end_elem = page.lift(p => math.min(((page.get + 1) * LIST_LENGTH), filteredContents.size))

  filteredContents.addDependent(currentContents)

  val searchValues = Map() ++ (0 until fieldnames.size).map(_ -> "")

  def ajaxLiveText(value: String, func: String => JsCmd, attrs: (String, Int)): Elem = {
    S.fmapFunc(S.SFuncHolder(func)) {
      funcName =>
        (<input type="text" value={ value }/> /: attrs)(_ % _) %
          ("onkeyup" -> makeAjaxCall(JsRaw("'" + funcName + "=' + encodeURIComponent(this.value)")))
    }
  }

  def pageUpdate(f: (Int => Int)) = {
    page atomicUpdate (f)
    currentContents atomicUpdate (list => filteredContents.get drop (page.get * LIST_LENGTH) take (LIST_LENGTH))
    JsCmds._Noop
  }

  def pageSort(i: Int) = {
    if (orderedAsc.get._1 == i && orderedAsc.get._2) {
      filteredContents.atomicUpdate(f => filteredContents.get.sortBy(v => (v._2(i))).reverse)
    }
    else {
      filteredContents.atomicUpdate(f => filteredContents.get.sortBy(v => (v._2(i))))
    }
    orderedAsc.atomicUpdate(a => (i, !(orderedAsc.get._2)))
    pageUpdate(n => 0)
  }

  def siteSelectDropDown(currentPage: Int) = "#select_site_selection_dropdown" #> ajaxSelect((0 to num_pages.get) map (n => (n.toString, (n + 1).toString)),
    Full(currentPage.toString),
    s => pageUpdate(page => s.toInt))

  def searchRow = "#searchfieldcell *" #> fieldnames.zipWithIndex.map(t =>
    "#searchinput" #> ajaxLiveText("", s => {
      searchValues(t._2) = s.toLowerCase
      filteredContents.atomicUpdate(list => sourceValues.filter(l =>
        (true /: (0 until l._2.size).map(n => l._2(n).toString.toLowerCase.contains(searchValues(n))))(_ && _)))
      pageUpdate(n => 0)
    }, t))

  def headerRow = "#th_header *" #> fieldnames.zipWithIndex.map(h => {
    "#span_header *" #> h._1 andThen
      "#div_header [onclick]" #> ajaxInvoke(() => pageSort(h._2))
  })

  def contentRow(list: List[(String, List[Any])]) = "#tr_content *" #> list.zipWithIndex.map {
    case (row, i) => {
      "td [class]" #> (if (i % 2 == 0) "even" else "") &
        "td [onclick]" #> (("window.location.pathname='%s/detail/" + row._1 + "'").format(net.liftweb.http.LiftRules.context.path + "/" + detailName)) &
        "#td_content *" #> row._2.map(elem => elem match {
          case elem: scala.xml.Elem    => "#span_content" #> elem
          case elem: scala.xml.NodeSeq => "#span_content" #> elem
          case _                       => "#span_content" #> elem.toString
        })
    }
  }

  def button(s: String, f: Int => Int) = s #> ajaxInvoke(() => pageUpdate(f))

  def display = {
    val ranId = (new java.util.Date).hashCode() - (new Random().nextInt)
    "#span_title" #> title andThen
      button("#td_button_start [onclick]", n => 0) &
      button("#td_button_back [onclick]", n => math.max(0, n - 1)) &
      button("#td_button_next [onclick]", n => math.min(num_pages.get, n + 1)) &
      button("#td_button_end [onclick]", n => num_pages.get) andThen
      searchRow & headerRow andThen
      "#div_site_selection_dropdown [id]" #> ("div_site_selection_dropdown" + ranId) andThen ("#div_site_selection_dropdown" + ranId) #> WiringUI.apply(page)(siteSelectDropDown) &
      "#tbody_list_view [id]" #> ("tbody_list_view" + ranId) andThen ("#tbody_list_view" + ranId) #> WiringUI.apply(currentContents)(contentRow) andThen
      "#span_selectorbox_start [id]" #> ("span_selectorbox_start" + ranId) andThen ("#span_selectorbox_start" + ranId) #> WiringUI.asText(start_elem) &
      "#span_selectorbox_end [id]" #> ("span_selectorbox_end" + ranId) andThen ("#span_selectorbox_end" + ranId) #> WiringUI.asText(end_elem) &
      "#span_selectorbox_total [id]" #> ("span_selectorbox_total" + ranId) andThen ("#span_selectorbox_total" + ranId) #> WiringUI.asText(num_elems)
  }

  private implicit val anyOrdering = new Ordering[Any] {
    def compare(a: Any, b: Any) = (a, b) match {
      case (a: Long, b: Long) => a compare b
      case (a: Int, b: Int)   => a compare b
      case _                  => a.toString compare b.toString
    }
  }
}
