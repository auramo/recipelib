package recipe.auth

import org.scalatra.ScalatraFilter
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class AuthFilter extends ScalatraFilter {
  val consumer = new OpenIdConsumer
  val logger: Logger = LoggerFactory.getLogger(getClass)

  before("/recipes/*") {
    if (request.getSession.getAttribute("authenticated-user") == null) {
      request.getSession.setAttribute("url-after-login", request.getRequestURL) // request.getQueryString also, but handle null and maybe add ? etc.
      consumer.authenticateGoogleUser(request, response)
      halt(401, "Checking authentication")
    }
  }

  before("/recipeapi/*") {
    logger.info("AuthFiltering recipeapi")
    if (request.getSession.getAttribute("authenticated-user") == null) {
      halt(401, "Not authencitated")
    }
  }

  get("/openid") {
    val retval = consumer.verifyResponse(request)
    logger.info("openid return value: " + retval)
    retval match {
      case Right(authUser) => {
        request.getSession.setAttribute("authenticated-user", authUser)
        val redirectTo = request.getSession.getAttribute("url-after-login").toString
        if (redirectTo != null) response.sendRedirect(redirectTo)
        "Login ok"
      }
      case Left(errMsg) => {
        logger.error(errMsg)
        "Login failed: " + errMsg
      }
    }
  }
}
