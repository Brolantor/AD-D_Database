package bootstrap.liftweb

import net.liftweb._
import common._
import http._
import js.jquery.JQueryArtifacts
import sitemap._
import Loc._
import mapper._
import util._
import ad_d._
import Helpers._
import ad_d.model._
import ad_d.util._
//import ev_db.model._
//import ev_db.util.{ Ev => EvConnector,_ }

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {

  val logger = Logger(classOf[Boot])

  def boot {
    logger.info("aktueller run-mode: " + Props.mode)

    // Verbindung zur Datenbank. Das erste Statement soll nur prüfen, ob eine relationale Datenbank
    // über den Container (z.B. Tomcat) bereit gestellt wird.
    DefaultConnectionIdentifier.jndiName = Props.get("kosi_db.jndi") openOr "jdbc/kosi_db"
    logger.info("jndiJdbcConnAvailable_ : " + DB.jndiJdbcConnAvailable_?)
    if (!DB.jndiJdbcConnAvailable_?) {
      KosiDBVendor.initialize()
      DB.defineConnectionManager(AD_D, KosiDBVendor)
    }

    KosiDBVendor.schemify()

    // Basispaket, wo nach Snippets gesucht wird
    LiftRules.addToPackages("ad_d")

    //    LiftRules.responseTransformers.append {
    //      case x: XhtmlResponse if (x.toResponse.code != 200) =>
    //        val message = x.out.child.filter(_.label == "body").head.text
    //        S.setSessionAttribute("errorType", x.toResponse.code.toString)
    //        S.setSessionAttribute("message", message)
    //        RedirectResponse("/error")
    //      case r => r.toResponse
    //    }

//    LiftRules.dispatch.append {
//      case Req("loginService" :: Nil, _, _) => () => ev_db.service.LoginService.serve
//      case Req("logoutService" :: Nil, _, _) => () => ev_db.service.LoginService.logOut
//    }
//
    LiftRules.statefulRewrite.append {
      case RewriteRequest(
        ParsePath((category :: "detail" :: id :: Nil), _, _, _), _, _) => {
        category match {
          case "spell" => RewriteResponse("100_magie" :: "140_spell_detail" :: Nil, Map("idspell" -> id))
          case _       => RewriteResponse("index" :: Nil)
        }
      }

      case RewriteRequest(
        ParsePath((category :: "edit_detail" :: id :: Nil), _, _, _), _, _) => {
        val par = "editable" -> "1"
        category match {
          case "spell" => RewriteResponse("100_magie" :: "140_spell_detail" :: Nil, Map("idspell" -> id, par))
          case _            => RewriteResponse("index" :: Nil)
        }
      }

      case RewriteRequest(
        ParsePath((category :: "change_mat" :: id :: Nil), _, _, _), _, _) => {
        category match {
          case "spell" => RewriteResponse("100_magie" :: "150_spell_mat_change" :: Nil, Map("idspell" -> id))
          case _            => RewriteResponse("index" :: Nil)
        }
      }

      case RewriteRequest(
        ParsePath((category :: "change_school" :: id :: Nil), _, _, _), _, _) => {
        category match {
          case "spell" => RewriteResponse("100_magie" :: "160_spell_school_change" :: Nil, Map("idspell" -> id))
          case _            => RewriteResponse("index" :: Nil)
        }
      }

      case RewriteRequest(
        ParsePath((category :: "list" :: Nil), _, _, _), _, _) =>
        category match {
          case _          => RewriteResponse("index" :: Nil)
        }
    }

    // set the sitemap.  Note if you don't want access control for each page, just comment this line out.
    LiftRules.setSiteMap(SiteMap(Menus.siteMap: _*))

    //Init the jQuery module, see http://liftweb.net/jquery for more information.
    //    LiftRules.jsArtifacts = JQueryArtifacts
    //    JQueryModule.InitParam.JQuery = JQueryModule.JQuery172
    //    JQueryModule.init()

    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart = Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd = Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    // What is the function to test if a user is logged in?
    LiftRules.loggedInTest = Full(() => User.loggedIn_?)

    // Use HTML5 for rendering
    LiftRules.htmlProperties.default.set((r: Req) => new Html5Properties(r.userAgent))
    
    // Hochgeladene Dateien nicht im Speicher behandeln sondern direkt speichern
    LiftRules.handleMimeFile = OnDiskFileParamHolder.apply

    // Make a transaction span the whole HTTP request
//    S.addAround(DB.buildLoanWrapper(List(EvConnector, Rahmenplan)))
  }
}
