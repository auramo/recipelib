import recipe._
import org.scalatra._
import javax.servlet.ServletContext
import recipe.api.RecipeApiServlet
import recipe.auth.AuthFilter

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    context.mount(new AuthFilter, "/*")
    context.mount(new RecipeApiServlet, "/recipeapi/*")
  }
}
