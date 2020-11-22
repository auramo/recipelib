package recipe.cli

import recipe.auth.User
import recipe.crypto.PasswordHash.createHash
import recipe.repository.UserRepository

object StoreUser {
  def main(args: Array[String]): Unit = {
    if (args.length < 3) {
      println("Usage: StoreUser <user> <password> <recipelib-id>")
      System.exit(1)
    }
    val repository = new UserRepository()
    val user = args(0)
    val password = args(1)
    val recipeListId = args(2)
    println(s"Storing user $user with password $password referring to recipelist id $recipeListId")
    repository.insert(User(user, createHash(password), null, List(recipeListId)))
  }
}
