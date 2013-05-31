package recipe.service

import recipe.auth.{AuthenticatedUser, User}
import recipe.{RecipeLibrary, Recipe}
import recipe.dao.{RecipeDao, UserDao}
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RecipeService(val userDao: UserDao, val recipeDao: RecipeDao) {

  val logger: Logger = LoggerFactory.getLogger(getClass)

  def getRecipes(authUser: AuthenticatedUser): List[Recipe] = {
    getStoredUser(authUser) match {
      case Some(User(id, email, recipeLibraries)) => {
        logger.info(s"Found user for id $id, email $email. Listing recipes")
        recipeDao.listRecipes(recipeLibraries.head)
      } //No proper support for multiple libs yet
      case None => {
        logger.info(s"No user $authUser found, so no recipes yet")
        Nil
      }
    }
  }

  def getRecipe(authUser: AuthenticatedUser, id: String): Option[Recipe] = {
    //TODO check access from authUser to recipe
    recipeDao.findRecipe(id)
  }

  def saveRecipe(authUser: AuthenticatedUser, recipe: Recipe) {
    getStoredUser(authUser) match {
      case Some(User(id, email, recipeLibraries)) => {
        val recipeWithLibraryId: Recipe = recipe.copy(recipeLibraryId = Some(recipeLibraries.head))
        logger.info(s"Found user for id $id, email $email. Saving recipes to library ${recipeLibraries.head}")
        recipeDao.saveRecipe(recipeWithLibraryId)
      }
      case None => {
        val libraryId = createUserAndRecipeLibrary(authUser)
        val recipeWithLibraryId: Recipe = recipe.copy(recipeLibraryId = Some(libraryId))
        logger.info(s"No user $authUser found, so no recipe library yet. Storing user and new library $libraryId")
        recipeDao.saveRecipe(recipeWithLibraryId)
      }
    }
  }

  def deleteRecipe(authUser: AuthenticatedUser, id: String) {
    recipeDao.deleteRecipe(id)
  }

  def createUserAndRecipeLibrary(authUser: AuthenticatedUser): String = {
    val newLibrary: RecipeLibrary = RecipeLibrary(None, authUser.email.getOrElse(authUser.identifier.getIdentifier))
    val libraryId = recipeDao.saveRecipeLibrary(newLibrary)
    val newUser: User = User(authUser.identifier.getIdentifier, authUser.email.getOrElse("N/A"), List(libraryId))
    userDao.save(newUser)
    logger.info(s"Stored user $newUser")
    libraryId
  }

  private def getStoredUser(authUser: AuthenticatedUser): Option[User] = {
    val userId: String = authUser.identifier.getIdentifier
    userDao.find(userId)
  }

}
