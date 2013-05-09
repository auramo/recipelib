package auth

import org.scalatra.ScalatraFilter

class AuthFilter extends ScalatraFilter {
  val consumer = new OpenIdConsumer

  before() {
    if (request.getRequestURL.toString.contains("/recipes") && request.getSession.getAttribute("authenticated-user") == null) {
      consumer.authenticateGoogleUser(request, response)
    }
  }

  get("/openid") {
    println("to return page")
    println("consumer instance: " + consumer)
    val retval = consumer.verifyResponse(request)
    println("retval: " + retval)
    val message = retval match {
      case Right(authUser) => request.getSession.setAttribute("authenticated-user", authUser); "Login ok"
      case Left(errMsg) => println(errMsg); errMsg
    }

    println("message which I shuold put to the template if I knew how " + message)

    <html>
      <body>
        <h1>openid login landing page</h1>
      </body>
    </html>
  }
}
