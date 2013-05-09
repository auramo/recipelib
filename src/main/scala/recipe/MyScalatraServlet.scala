package recipe

import org.scalatra._
import scalate.ScalateSupport
import auth.OpenIdConsumer
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

class MyScalatraServlet extends RecipelibStack {

  get("/") {
    <html>
      <body>
        <h1>Hello, world!</h1>
        Say <a href="hello-scalate">hello to Scalate</a>.
      </body>
    </html>
  }

}
