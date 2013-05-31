package recipe.dao

import recipe.{RecipeLibrary, Recipe}
import recipe.db.AutoCloseControl._
import recipe.db.MongoConnectionCreator
import com.mongodb.casbah.Imports._
import org.bson.types.ObjectId
import com.mongodb.casbah

class RecipeDao {
  def findRecipeLibrary(id: String): Option[RecipeLibrary] = {
    using(MongoConnectionCreator.createMongoConnection()) { mongoClient =>
      val coll = getRecipeLibraryCollection(mongoClient)
      val res: Option[DBObject] = coll.findOneByID(ObjectId.massageToObjectId(id))
      res.flatMap { resultObject => convert(resultObject) }
    }
  }

  def saveRecipeLibrary(recipeLibrary: RecipeLibrary): String = {
    using(MongoConnectionCreator.createMongoConnection()) { mongoClient =>
      val coll = getRecipeLibraryCollection(mongoClient)
      val attributesTail = if (recipeLibrary.id.isDefined) ("_id" -> ObjectId.massageToObjectId(recipeLibrary.id.get)) :: Nil else Nil
      val attributes = "name" -> recipeLibrary.name :: attributesTail
      val newValues = MongoDBObject(attributes)
      coll.save(newValues)
      newValues.getAs[ObjectId]("_id").get.toString
    }
  }

  def findRecipe(id: String): Option[Recipe] = {
    using(MongoConnectionCreator.createMongoConnection()) { mongoClient =>
      val coll = getRecipeCollection(mongoClient)
      val res: Option[DBObject] = coll.findOneByID(ObjectId.massageToObjectId(id))
      res.map { rawObject: DBObject =>
        val id = rawObject.getAs[ObjectId]("_id").get.toString
        val recipeLibraryId = rawObject.getAs[String]("recipeLibraryId")
        val name = rawObject.getAs[String]("name").get
        val originalAddress = rawObject.getAs[String]("originalAddress")
        val content = rawObject.getAs[String]("content")
        val tags = getTags(rawObject)
        Recipe(Some(id), recipeLibraryId, name, tags, originalAddress, content)
      }
    }
  }

  def listRecipes(recipeLibraryId: String): List[Recipe] = {
    using(MongoConnectionCreator.createMongoConnection()) { mongoClient =>
      val coll: casbah.MongoCollection = getRecipeCollection(mongoClient)
      val query = MongoDBObject("recipeLibraryId" -> recipeLibraryId)
      val keys = MongoDBObject("name" -> 1, "tags" -> 1, "recipeLibraryId" -> 1)
      val res = coll.find(query, keys)
      val convertedResults = for {rawObject <- res
           id = rawObject.getAs[ObjectId]("_id").get.toString
           recipeLibraryId = rawObject.getAs[String]("recipeLibraryId")
           name = rawObject.getAs[String]("name").get
           tagsRaw: MongoDBList = rawObject.getAs[MongoDBList]("tags").get
           tags = getTags(rawObject)
        } yield (Recipe(Some(id), recipeLibraryId, name, tags, None, None))
      convertedResults.toList
    }
  }

  def recipeBelongsToLibrary(recipeId: String, libraryId: String): Boolean = {
    findRecipe(recipeId) match {
      case Some(recipe) => recipe.recipeLibraryId.get == libraryId
      case None => false
    }
  }

  def saveRecipe(recipe: Recipe): String = {
    using(MongoConnectionCreator.createMongoConnection()) { mongoClient =>
      val coll = getRecipeCollection(mongoClient)
      val attributesTail = if (recipe.id.isDefined) ("_id" -> ObjectId.massageToObjectId(recipe.id.get)) :: Nil else Nil
      val attributes =
        "name" -> recipe.name ::
        "tags"-> recipe.tags ::
        "content" -> recipe.content.get ::
        "recipeLibraryId" -> recipe.recipeLibraryId ::
        "originalAddress" -> recipe.originalAddress ::
        attributesTail
      val newValues = MongoDBObject(attributes)
      coll.save(newValues)
      newValues.getAs[ObjectId]("_id").get.toString
    }
  }

  def deleteRecipe (id: String) = {
    using(MongoConnectionCreator.createMongoConnection()) { mongoClient =>
      val coll = getRecipeCollection(mongoClient)
      coll.findAndRemove(MongoDBObject("_id" -> ObjectId.massageToObjectId(id)))
    }
  }

  private def convert(res: DBObject): Option[RecipeLibrary] = {
    val id = res.getAs[ObjectId]("_id").get.toString
    val name = res.getAs[String]("name").get
    Some(RecipeLibrary(Some(id), name))
  }

  private def getTags(res: DBObject): List[String] = {
    val tagsRaw: MongoDBList = res.getAs[MongoDBList]("tags").get
    tagsRaw.collect { case l: String => l }.toList
  }

  private def getRecipeLibraryCollection(mongoClient: MongoConnection) = {
    MongoConnectionCreator.openMongoDb(mongoClient)("recipeLibraries")
  }
  private def getRecipeCollection(mongoClient: MongoConnection) = {
    MongoConnectionCreator.openMongoDb(mongoClient)("recipes")
  }
}
