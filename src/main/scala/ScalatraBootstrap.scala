import auth.AuthFilter
import recipe._
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    context.mount(new AuthFilter, "/*")
    context.mount(new MyScalatraServlet, "/recipes/*")
    context.mount(new RecipeApiServlet, "/recipeapi/*")
  }
}
