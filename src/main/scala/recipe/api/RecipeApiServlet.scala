package recipe.api

import recipe.{Recipe, Recipes, RecipelibStack}
import recipe.service.RecipeService
import recipe.auth.{User, AuthenticatedUser}
import org.json4s.native.Serialization
import org.json4s.NoTypeHints
import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write}
import org.json4s.jackson.Serialization._
import recipe.auth.AuthenticatedUser

class RecipeApiServlet(recipeService: RecipeService) extends RecipelibStack {
  implicit val formats = Serialization.formats(NoTypeHints)

  get("/api/") {
    """{"data": 5}"""
  }

  def getUser = {
    request.getSession.getAttribute("authenticated-user").asInstanceOf[AuthenticatedUser]
  }

  get("/") {
    val authUser = getUser
    val recipeList = recipeService.getRecipes(authUser)
    val recipes = Recipes(recipeList)
    val result = write(recipes)
    println("Sending result")
    println(result)
    result
    /*"""
      {"recipes": [{"id": "1", "name": "Maksalaatikko", "tags": ["loota", "perinneruoka"]},
                   {"id": "2", "name": "Lihapullat", "tags": ["perus"]},
                   {"id": "3", "name": "Kalapuikot", "tags": ["eines", "paha"]}
                   ]}
    """                      */
  }

  get("/:id") {
    val id = params("id")
    println("Got recipe retrieval request with id: " + id)
    val result = recipeService.getRecipe(getUser, id)
    if (result.isDefined) {
      val recipeJsonString = write(result.get)
      println("Sending recipe json string:")
      println(recipeJsonString)
      recipeJsonString
    } else {
      halt(404, """{"ok": false, "message": "recipe not found"}""")
    }

    /*"""
      {"id": "1", "name": "Maksalaatikko", "tags": ["loota", "perinneruoka"], "content":"maksat uuniin", "originalAddress": "http://huu.haa" }
    """*/
  }

  post("/") {
    println("Post got this:")
    println(request)
    println(request.body)
    parseAndSaveRecipe
    """{"ok": true}"""
  }


  put("/:id") {
    println("PUT got this:")
    val id = params("id")
    println("PUT request with id: " + id)
    println(request.body)
    parseAndSaveRecipe
    """{"ok": true}"""
  }

  delete("/:id") {
    val id = params("id")
    println("DELETE request with id: " + id)
    """{"ok": true}"""
  }

  private def parseAndSaveRecipe {
    try {
      val parsedRecipe = read[Recipe](request.body)
      println("Parsed recipe")
      println(parsedRecipe)
      recipeService.saveRecipe(getUser, parsedRecipe)
    } catch { case e: Exception => e.printStackTrace(); throw e; }
  }

}
