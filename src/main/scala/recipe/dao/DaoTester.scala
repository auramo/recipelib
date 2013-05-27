package recipe.dao

import recipe.{Recipe, RecipeLibrary}

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
    val id = dao.saveRecipe(Recipe(None, "51a261f80364536db853ebf1", "jeepapje", List("x", "y"), None, Some("sontenttiio")))
    println(id)

    println(dao.findRecipe(id))

    //val recipes = dao.listRecipes("51a261f80364536db853ebf1")
    //println(recipes)
  }

  def userStuff {
    val dao = new UserDao
    println(dao.find("1"))
    println(dao.find("2"))
  }
}
