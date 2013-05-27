package recipe

case class RecipeLibrary(id: Option[String], name: String) //Name can be e.g. email of the user who created the list
case class Recipe(id: Option[String], recipeLibraryId: Option[String], name: String, tags: List[String], originalAddress: Option[String], content: Option[String])
