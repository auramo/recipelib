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
      def getIdentifier = "123"
    }, Some("mail"))
    service.getRecipes(authUser)
    service.saveRecipe(authUser, Recipe(None, None, "maksaloota", List("x"), Some("http://kuu"), Some("jee kontent")))

  }

  def userStuff {
    val dao = new UserDao
    println(dao.find("1"))
    println(dao.find("2"))
  }
}
