package recipe

case class RecipeLibrary(id: String, name: String, recipes: List[Recipe]) //Name can be e.g. email of the user who created the list
case class Recipe(id: String, name: String, tags: List[String], contentId: String)
case class RecipeContent(id: String, content: String)
