package recipe

import org.scalatra._
import scalate.ScalateSupport
import auth.OpenIdConsumer
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

class MyScalatraServlet extends RecipelibStack {
  val consumer = new OpenIdConsumer

  get("/") {
    println("pow")
    println(request.getScheme() + "://" +
    request.getServerName() + ":" +
    request.getServerPort())

    <html>
      <body>
        <h1>Hello, world!</h1>
        Say <a href="hello-scalate">hello to Scalate</a>.
      </body>
    </html>
  }

  get("/openid") {
    println("to return page")
    println("consumer instance: " + consumer)
    val retval = consumer.verifyResponse(request)
    println("retval: " + retval)

    <html>
      <body>
        <h1>Openid return page</h1>
      </body>
    </html>
  }

  get("/authenticate") {
    println("before authenticateGoogleUser")
    consumer.authenticateGoogleUser(request, response)
    println("after authenticateGoogleUser")
  }

}
