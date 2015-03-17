package ad_d.snippet

import ad_d.lib.DependencyFactory

import net.liftweb.common.Box
import net.liftweb.http.S
import net.liftweb.sitemap.Loc
import net.liftweb.util.Helpers.strToCssBindPromoter

import java.util.{Date, Locale}
import java.text.DateFormat._

import scala.xml.{NodeSeq, Text}

class UtilSnippet {

  private val formatter = getDateTimeInstance(FULL, SHORT, Locale.GERMAN)

  lazy val date: Box[Date] = DependencyFactory.inject[Date] 
  def time = " *" #> formatter.format(date.get)
  
  /**   The intersperse-method below was written by Naftoli with the help of Gabriel Cardoso for making it tail-recursive
   *   Bootstrap example:
   *   USAGE:
   *   <ul class="breadcrumb">
   *      <div class="lift:utilSnippet.navPath"></div>
   *    </ul>
   *   RESULT:
   *   <ul class="breadcrumb">
   *      <li>
   *        <a href="/agent/basedata/">Base Data</a>
   *        <span class="divider">/</span>
   *      </li>
   *      <li class="active">Hotel</li>
   *    </ul>   
   */

  @scala.annotation.tailrec
  private def intersperse[T](list: List[T], co: T, acc: List[T] = Nil): List[T] = list match {
    case Nil => Nil
    case one :: Nil => (one :: acc).reverse
    case one :: two :: rest => intersperse(two :: rest, co, co :: one :: acc)
  }

  def navPath = "*" #> S.location.map(loc =>
    intersperse(
      loc.breadCrumbs.map { loc =>

        val href = loc.createDefaultLink.getOrElse(NodeSeq.Empty)
        val text = loc.linkText.openOr(NodeSeq.Empty)

        if (loc == S.location.openOr(NodeSeq.Empty))
          <li class="active">{ text }</li>
        else
          <li style="float:left"><a href={ href }>{ text }</a><span class="divider">&nbsp;>&nbsp;</span></li>
      },
      Text(""))).openOr(NodeSeq.Empty)

  def generic_breadcrumb = "*" #> S.location.map(loc =>
    intersperse(
      loc.breadCrumbs.map { loc =>

        val href = loc.createDefaultLink.getOrElse(NodeSeq.Empty)
        val text = loc.linkText.openOr(NodeSeq.Empty)

        if (loc == S.location.openOr(NodeSeq.Empty)) 
          { text }
        else

          <a href={ href }>{ text }</a>
      },
      Text(" / "))).openOr(NodeSeq.Empty)
}
