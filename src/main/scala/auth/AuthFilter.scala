package auth

import org.scalatra.ScalatraFilter

class AuthFilter extends ScalatraFilter {
  val consumer = new OpenIdConsumer

  before("/recipes/*") {
    if (request.getSession.getAttribute("authenticated-user") == null) {
      request.getSession.setAttribute("url-after-login", request.getRequestURL) // request.getQueryString also, but handle null and maybe add ? etc.
      consumer.authenticateGoogleUser(request, response)
    }
  }

  get("/openid") {
    println("to return page")
    println("consumer instance: " + consumer)
    val retval = consumer.verifyResponse(request)
    println("retval: " + retval)
    retval match {
      case Right(authUser) => {
        request.getSession.setAttribute("authenticated-user", authUser)
        val redirectTo = request.getSession.getAttribute("url-after-login").toString
        if (redirectTo != null) response.sendRedirect(redirectTo)
        "Login ok"
      }
      case Left(errMsg) => {
        println(errMsg)
        "Login failed"
      }
    }
  }
}
