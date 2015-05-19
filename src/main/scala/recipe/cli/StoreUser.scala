package recipe.cli

import recipe.auth.User
import recipe.dao.UserDao
import recipe.crypto.PasswordHash.createHash

object StoreUser {
  def main(args: Array[String]): Unit = {
    if (args.length < 3) {
      println("Usage: StoreUser <user> <password> <recipelib-id>")
      System.exit(1)
    }
    val dao = new UserDao()
    val user = args(0)
    val password = args(1)
    val recipeListId = args(2)
    println(s"Storing user $user with password $password referring to recipelist id $recipeListId")
    dao.save(User(user, createHash(password), null, List(recipeListId)))
  }
}
