package recipe.service

import org.slf4j.{Logger, LoggerFactory}
import recipe.auth.User
import recipe.dao.{RecipeDao, UserDao}
import recipe.{Recipe, RecipeLibrary};

class RecipeService(val userDao: UserDao, val recipeDao: RecipeDao) {

  val logger: Logger = LoggerFactory.getLogger(getClass)

  def getRecipes(user: User): List[Recipe] = {
    if (user.recipeLibraries.isEmpty) Nil
    else recipeDao.listRecipes(user.recipeLibraries.head)
  }

  def getRecipe(user: User, id: String): Option[Recipe] = {
    //We could also check access here, but nah, let's see. Current access check would then fetch it twice, so something smarter should be there
    recipeDao.findRecipe(id)
  }

  def saveRecipe(user: User, recipe: Recipe): String = {
    if (recipe.id.isDefined) checkAccess(user, recipe.id.get) //Storing existing recipe
    if (user.recipeLibraries.isEmpty) {
      val libraryId = createRecipeLibraryForUser(user)
      val recipeWithLibraryId: Recipe = recipe.copy(recipeLibraryId = Some(libraryId))
      recipeDao.saveRecipe(recipeWithLibraryId)
    } else {
      val recipeWithLibraryId: Recipe = recipe.copy(recipeLibraryId = Some(user.recipeLibraries.head))
      recipeDao.saveRecipe(recipeWithLibraryId)
    }
  }

  def deleteRecipe(user: User, id: String) {
    checkAccess(user, id)
    recipeDao.deleteRecipe(id)
  }

  def createRecipeLibraryForUser(user: User): String = {
    val newLibrary: RecipeLibrary = RecipeLibrary(None, user.id)
    val libraryId = recipeDao.saveRecipeLibrary(newLibrary)
    val changedUser: User = user.copy(recipeLibraries = List(libraryId))
    userDao.save(changedUser)
    logger.info(s"Stored changed user $changedUser")
    libraryId
  }

  private def checkAccess(user: User, id: String) {
    if (user.recipeLibraries.isEmpty) accessError(user, id)
    else {
      val libraryId = user.recipeLibraries.head
      if (!recipeDao.recipeBelongsToLibrary(id, libraryId)) accessError(user, id)
    }
  }
  private def accessError(user: User, id: String): Nothing = throw new IllegalAccessException(s"User $user doesn't have access to $id")

}
