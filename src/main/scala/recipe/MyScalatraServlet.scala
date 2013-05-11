package recipe

import org.scalatra._
import scalate.ScalateSupport
import auth.OpenIdConsumer
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

class MyScalatraServlet extends RecipelibStack {

  get("/ssptest") {
    contentType="text/html"
    ssp("hello-ssp.ssp", "foo" -> "uno", "bar" -> "dos")
  }

  get("/") {
    <html>
      <body>
        <h1>Hello, world!</h1>
        Say <a href="/recipes/hello-scalate">hello to Scalate</a>.
      </body>
    </html>
  }

}
