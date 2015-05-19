import javax.servlet.ServletContext

import org.scalatra._
import recipe.api.RecipeApiServlet
import recipe.auth.AuthFilter
import recipe.dao.{RecipeDao, UserDao}
import recipe.service.RecipeService

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    val userDao: UserDao = new UserDao
    context.mount(new AuthFilter(userDao), "/*")
    val recipeService: RecipeService = new RecipeService(userDao, new RecipeDao)
    context.mount(new RecipeApiServlet(recipeService), "/recipeapi/*")
  }
}
