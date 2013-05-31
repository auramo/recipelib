package recipe.dao

import recipe.Recipe
import recipe.service.RecipeService
import recipe.auth.AuthenticatedUser
import org.openid4java.discovery.Identifier

object DaoTester {
  def main(args: Array[String]) {
    val dao = new RecipeDao
//    case class RecipeLibrary(id: String, name: String, recipes: List[Recipe]) //Name can be e.g. email of the user who created the list
//    case class Recipe(id: String, name: String, tags: List[String], contentId: String)

    //, List(Recipe("11", "maksalaatikko", List("maksis", "loota"), "contentId1"))

    //val recipeLib = RecipeLibrary(Some("51a261f80364536db853ebf1"), "johanna@gmail")
    //val id = dao.saveRecipeLibrary(recipeLib)
    //println(id)

    //println(dao.findRecipeLibrary("51a261f80364536db853ebf1"))
    //println(dao.findRecipeLibrary("xx"))

    //case class Recipe(id: Option[String], recipeLibraryId: String, name: String, tags: List[String], content: Option[String])
    //val id = dao.saveRecipe(Recipe(None, Some("51a261f80364536db853ebf1"), "jeepapje", List("x", "y"), None, Some("sontenttiio")))
    //println(id)

    //println(dao.findRecipe(id))

    //val recipes = dao.listRecipes("51a261f80364536db853ebf1")
    //println(recipes)


    val service = new RecipeService(new UserDao, new RecipeDao)
    val authUser: AuthenticatedUser = AuthenticatedUser(new Identifier() {
      def getIdentifier = "9123"
    }, Some("mail"))

    //service.saveRecipe(authUser, Recipe(Some("51a8fe5303641878331d48f4"), None, "kukkalaatikko", List("x"), Some("http://kuu"), Some("jee kontent")))
    //service.saveRecipe(authUser, Recipe(None, None, "kukkalaatikko", List("x"), Some("http://kuu"), Some("jee kontent")))
    //println("saved!")

    //service.deleteRecipe(authUser, "51a6535b03649cb78f4dc479")

    //Let's try this after
    service.deleteRecipe(authUser, "51a8fe5303641878331d48f4")

    //The stored one:
    //51a8fe5303641878331d48f4
    val recipes = service.getRecipes(authUser)
    println(recipes)

  }

  def userStuff {
    val dao = new UserDao
    println(dao.find("1"))
    println(dao.find("2"))
  }
}
