package recipe

import org.scalatra._
import scalate.ScalateSupport
import auth.OpenIdConsumer
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

class RecipeUiServlet extends RecipelibStack {

  get("/") {
    contentType="text/html"
    ssp("recipeui.ssp")
  }

}
