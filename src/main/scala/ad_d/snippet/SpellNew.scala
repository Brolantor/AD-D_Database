package ad_d.snippet

import ad_d.lib._
import ad_d.model.add._
import net.liftweb.util.Helpers._
import net.liftweb.http.S
import net.liftweb.mapper._
import net.liftweb.common.Full
import scala.collection.immutable.ListMap

class SpellNew extends DetailView {
  
  override def allowEdit = true
  
  override def tabContents = ListMap()
  
  override def title = "Zaubersprüche"
    
  override def caption = "neuer Zauberspruch"
    
  override def tableValues = ListMap()

  override def display = {
    val newSpell = Spell.create
    "#span_save_button" #> newSpell.toForm(Full("save"), _.save)
  }
}
