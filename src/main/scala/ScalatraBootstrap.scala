import auth.AuthFilter
import recipe._
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    context.mount(new AuthFilter, "/*")

    //Let's try without the whole templating thing
    //context.mount(new RecipeUiServlet, "/recipes/*")

    context.mount(new RecipeApiServlet, "/recipeapi/*")
  }
}
