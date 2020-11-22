import javax.servlet.ServletContext
import org.scalatra._
import recipe.api.RecipeApiServlet
import recipe.auth.AuthFilter
import recipe.repository.{UserRepository, RecipeRepository}
import recipe.service.RecipeService

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    val userRepository : UserRepository = new UserRepository
    val recipeRepository: RecipeRepository = new RecipeRepository
    context.mount(new AuthFilter(userRepository), "/*")
    val recipeService: RecipeService = new RecipeService(
      userRepository,
      recipeRepository)
    context.mount(new RecipeApiServlet(recipeService), "/recipeapi/*")
  }
}
