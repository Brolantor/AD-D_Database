package ad_d

import net.liftweb.sitemap.Menu
import net.liftweb.sitemap.Loc._
import ad_d.model.User
import net.liftweb.http.S
import net.liftweb.common.Box.box2Option
import net.liftweb.sitemap.LocPath.stringToLocPath

object Menus {
  val serviceLocGroup = LocGroup("servicenav")
  val footerLocGroup = LocGroup("footernav")
  val serviceMenu = List(Menu("Startseite") / "index",
    Menu("Impressum") / "about" / "index" >> Hidden >> serviceLocGroup)

  //  def checkPermission(roleId:Int) = If(()=>User.currentUser.get.roleCheck_?(roleId), S.?("not allowed"))

  val kosiMenu = List(
    Menu("Magie") / "100_magie" / "100_magie_main" submenus (
      Menu("Magier") / "100_magie" / "110_magier" submenus (
          Menu("Zaubersprüche") / "100_magie" / "111_mage_all" submenus (
              Menu("Abjuration") / "100_magie" / "112_mage_abj",
              Menu("Alteration") / "100_magie" / "112_mage_alt",
              Menu("Conjuration/Summoning") / "100_magie" / "112_mage_conj",
              Menu("Enchantment/Charm") / "100_magie" / "112_mage_enc",
              Menu("Illusion/Phantasm") / "100_magie" / "112_mage_ill",
              Menu("Invocation/Evocation") / "100_magie" / "112_mage_inv",
              Menu("Necromancy") / "100_magie" / "112_mage_nec",
              Menu("Elemental Air") / "100_magie" / "112_mage_air",
              Menu("Elemental Earth") / "100_magie" / "112_mage_ear",
              Menu("Elemental Fire") / "100_magie" / "112_mage_fir",
              Menu("Elemental Water") / "100_magie" / "112_mage_wat",
              Menu("Dimension") / "100_magie" / "112_mage_dim",
              Menu("Force") / "100_magie" / "112_mage_for",
              Menu("Mentalism") / "100_magie" / "112_mage_men",
              Menu("Schulen zuweisen") / "100_magie" / "160_spell_school_change" >> Hidden
              )
          ),
      Menu("Priester") / "100_magie" / "120_priester"  submenus (
          Menu("Priesterprüche") / "100_magie" / "121_priest_all"
          ),
      Menu("Spruch Detail") / "100_magie" / "140_spell_detail" >> Hidden
    ),

    Menu("Fehler") / "error" >> Hidden)

  val siteMap = serviceMenu ::: kosiMenu ::: User.sitemap
}
