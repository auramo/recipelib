package recipe.api

import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write}
import org.scalatra._
import org.slf4j.{Logger, LoggerFactory}
import recipe.auth.User
import recipe.service.RecipeService
import recipe.{Recipe, Recipes}

class RecipeApiServlet(recipeService: RecipeService) extends ScalatraServlet {
  implicit val formats = Serialization.formats(NoTypeHints)
  val logger: Logger = LoggerFactory.getLogger(getClass)

  get("/") {
    val authUser = getUser
    val recipeList = recipeService.getRecipes(authUser)
    val recipes = Recipes(recipeList)
    val result = write(recipes)
    logger.info(s"GET request to root, sending list of ${recipes.recipes.length} recipes")
    result
  }

  get("/:id") {
    val id = params("id")
    logger.info("GET request with id: " + id)
    val result = recipeService.getRecipe(getUser, id)
    if (result.isDefined) {
      val recipeJsonString = write(result.get)
      logger.info("Recipe found")
      recipeJsonString
    } else {
      logger.error(s"Recipe $id not found")
      halt(404, write(Response(ok = false, Some(s"Recipe $id not found"))))
    }
  }

  post("/") {
    logger.info("POST request")
    logger.info(request.body)
    parseAndSaveRecipe
  }


  put("/:id") {
    val id = params("id")
    logger.info("PUT request with id: " + id)
    logger.info(request.body)
    parseAndSaveRecipe
  }

  delete("/:id") {
    val id = params("id")
    logger.info("DELETE request with id: " + id)
    recipeService.deleteRecipe(getUser, id)
    ok
  }

  private def parseAndSaveRecipe : String = {
    val parsedRecipe = read[Recipe](request.body)
    val id = recipeService.saveRecipe(getUser, parsedRecipe)
    write(ResponseWithId(true, id))
  }

  private def getUser = {
    request.getSession.getAttribute("authenticated-user").asInstanceOf[User]
  }

  private val errorLogger: PartialFunction[Throwable, Throwable] = {
    case e => logger.error(e.getMessage, e)
      e
  }

  errorHandler = errorLogger andThen errorHandler

  private def ok = write(Response())

}
case class ResponseWithId(ok: Boolean = true, id: String)
case class Response(ok: Boolean = true, message: Option[String] = None)

