import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.HandlerCollection
import org.eclipse.jetty.webapp.WebAppContext

object JettyLauncher {
  def main(args: Array[String]) {
    val port = if(System.getenv("PORT") != null) System.getenv("PORT").toInt else 8080

    val server = new Server(port)
    val handlerCollection = new HandlerCollection()
    val context = new WebAppContext()
    context setContextPath "/"
    context.setWar("src/main/webapp")
    handlerCollection.addHandler(context)
    server.setHandler(handlerCollection)

    server.start
    server.join
  }

}
