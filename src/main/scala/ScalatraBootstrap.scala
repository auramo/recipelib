import recipe._
import org.scalatra._
import javax.servlet.ServletContext
import recipe.api.RecipeApiServlet
import recipe.auth.AuthFilter
import recipe.dao.{RecipeDao, UserDao}
import recipe.service.RecipeService

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    context.mount(new AuthFilter, "/*")
    val recipeService: RecipeService = new RecipeService(new UserDao, new RecipeDao)
    context.mount(new RecipeApiServlet(recipeService), "/recipeapi/*")
  }
}
