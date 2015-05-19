package recipe.auth

import org.scalatra.ScalatraFilter
import org.scalatra.auth.strategy.BasicAuthStrategy.BasicAuthRequest
import org.slf4j.{Logger, LoggerFactory}
import recipe.crypto.PasswordHash
import recipe.dao.UserDao


class AuthFilter(userDao: UserDao) extends ScalatraFilter {
  val logger: Logger = LoggerFactory.getLogger(getClass)

  before("/recipes/*") {
    basicAuth
  }

  before("/recipeapi/*") {
    logger.info("AuthFiltering recipeapi")
    if (request.getSession.getAttribute("authenticated-user") == null) {
      logger.info("halting")
      halt(401, "Not authencitated")
    } else {
      logger.info("not halting")
    }
  }

  protected def basicAuth() = {
    val req = new BasicAuthRequest(request)

    def notAuthenticated() {
      response.setHeader("WWW-Authenticate", "Basic realm=\"%s\"" format "mc-nulty")
      halt(401, "Unauthenticated")
    }

    if (!req.providesAuth) {
      notAuthenticated
    }
    if (!req.isBasicAuth) {
      halt(400, "Bad Request")
    }
    if (request.getSession.getAttribute("authenticated-user") == null) {
      val user = validateLogin(req.username, req.password)
      user match {
        case Some(user) => request.getSession.setAttribute("authenticated-user", user)
        case None => notAuthenticated
      }
    }
  }

  protected def validateLogin(userId: String, password: String): Option[User] = {
    val userOpt: Option[User] = userDao.find(userId)
    userOpt.flatMap { user =>
      if (PasswordHash.validatePassword(password, user.passwordHash))
        Some(user)
      else
        None
    }
  }

}
