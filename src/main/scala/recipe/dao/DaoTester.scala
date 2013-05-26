package recipe.dao

import recipe.{Recipe, RecipeLibrary}

object DaoTester {
  def main(args: Array[String]) {
    val dao = new RecipeDao
//    case class RecipeLibrary(id: String, name: String, recipes: List[Recipe]) //Name can be e.g. email of the user who created the list
//    case class Recipe(id: String, name: String, tags: List[String], contentId: String)

    val recipeLib = RecipeLibrary("xx", "johanna@gmail", List(Recipe("11", "maksalaatikko", List("maksis", "loota"), "contentId1")))
    //dao.save(recipeLib)
    println(dao.find("xx"))
  }

  def userStuff {
    val dao = new UserDao
    println(dao.find("1"))
    println(dao.find("2"))
  }
}
