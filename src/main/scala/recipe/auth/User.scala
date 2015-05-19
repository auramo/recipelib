package recipe.auth

case class User(id: String, passwordHash: String = null, email: String, recipeLibraries: List[String])
