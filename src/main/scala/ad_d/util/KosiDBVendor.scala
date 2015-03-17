package ad_d.util

import net.liftweb.db.ConnectionManager
import net.liftweb.util.Props
import java.sql.Connection
import net.liftweb.db.ConnectionIdentifier
import net.liftweb.common.Empty
import net.liftweb.common.Full
import java.sql.DriverManager
import net.liftweb.mapper.Schemifier

object AD_D extends ConnectionIdentifier {
  override def jndiName = Props.get("add.jndi").get
}

object KosiDBVendor extends ConnectionManager {
  def initialize() = {
	Class.forName(Props.get("db.driver").get)
  }

  override def newConnection(name: ConnectionIdentifier) = {
    try {
      name match {
        case AD_D      => Full(DriverManager.getConnection(Props.get("add.url").get, Props.get("db.user").get, Props.get("db.password").get))
      }
    }
    catch {
      case e: Exception =>
        e.printStackTrace()
        Empty
    }
  }

  override def releaseConnection(conn: Connection) { conn.close }
  
  def schemify() = {
    Schemifier.schemify(true, Schemifier.infoF _, AD_D)
  }
}
