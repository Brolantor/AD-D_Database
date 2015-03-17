package ad_d

import scala.xml.Text
import scala.util.matching.Regex
import java.io.File
import net.liftweb.common.Logger


package object snippet {
  val logger = Logger("kosi_db.snippet.package")
  
  def brString(s: String) = if (s != null)
    s.split("\n").toList.flatMap(x => Text(x) ++ <br/>).dropRight(1)
  else <span/>

  def recursiveListFiles(f: File, r: Regex): Array[File] = {
    val these = f.listFiles
    val good = these.filter(f => r.findPrefixOf(f.getName).isDefined)
    good ++ these.filter(_.isDirectory).flatMap(recursiveListFiles(_, r))
  }

//    def realContextPath = {
//    val file = if (new File(".").getCanonicalPath() == "/usr/share/tomcat6/bin") "/var/lib/tomcat6/." else "D:/apache-tomcat-7.0.53/webapps/kosi_db_lift"

  
  lazy val realContextPath = {
    val file = if (new File(".").getCanonicalPath() == "/usr/share/tomcat6/bin") "/var/lib/tomcat6/." 
    else if (new File(".").getCanonicalPath() == "D:\\xampp\\tomcat\\bin") "D:\\xampp\\tomcat\\webapps\\kosi_db_lift\\."
    else "."
    recursiveListFiles(new File(file), "base.txt".r)(0).getParentFile()
  }

}
