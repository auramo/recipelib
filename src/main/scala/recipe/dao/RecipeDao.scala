package recipe.dao

import recipe.{RecipeLibrary, Recipe}
import recipe.db.AutoCloseControl._
import recipe.db.MongoConnectionCreator
import com.mongodb.casbah.Imports._
import recipe.auth.User

class RecipeDao {
  def find(id: String): Option[RecipeLibrary] = {
    using(MongoConnectionCreator.createMongoConnection()) { mongoClient =>
      val coll = getRecipeCollection(mongoClient)
      val res: Option[DBObject] = coll.findOneByID(id)
      res.flatMap { resultObject => convert(resultObject) }
    }
  }

  def save(recipeLibrary: RecipeLibrary) {
    using(MongoConnectionCreator.createMongoConnection()) { mongoClient =>
      val coll = getRecipeCollection(mongoClient)
      val newValues = MongoDBObject(
        "_id" -> recipeLibrary.id,
        "name" -> recipeLibrary.name,
        "recipes" -> convertRecipesToMongoObjects(recipeLibrary.recipes)
      )
      coll.save(newValues)
    }
  }

  private def convertRecipesToMongoObjects(recipes: List[Recipe]) = {
    recipes.map { recipe =>
      MongoDBObject(
        "_id" -> recipe.id,
        "name" -> recipe.name,
        "tags" -> recipe.tags,
        "contentId" -> recipe.contentId
      )
    }
  }

  private def convert(res: DBObject): Option[RecipeLibrary] = {
    val id = res.getAs[String]("_id").get
    val name = res.getAs[String]("name").get
    val recipesRaw: MongoDBList = res.getAs[MongoDBList]("recipes").get
    val recipes = recipesRaw.map { recipeRaw =>
      val rawObject = recipeRaw.asInstanceOf[DBObject]
      val id = rawObject.getAs[String]("_id").get
      val contentId = rawObject.getAs[String]("contentId").get
      val name = rawObject.getAs[String]("name").get
      val tagsRaw: MongoDBList = rawObject.getAs[MongoDBList]("tags").get
      val tags: List[String] = tagsRaw.collect { case l: String => l }.toList
      Recipe(id, name, tags, contentId)
    }
    Some(RecipeLibrary(id, name, recipes.toList))
  }

  private def getRecipeCollection(mongoClient: MongoConnection) = {
    MongoConnectionCreator.openMongoDb(mongoClient)("recipes")
  }
}
