package recipe.repository

import com.mongodb.client.{MongoClient, MongoCollection}
import recipe.db.AutoCloseControl.using
import org.bson.Document
import org.bson.types.ObjectId
import com.mongodb.client.model.Filters

import scala.collection.JavaConverters._
import recipe.{Recipe, RecipeLibrary}
import recipe.db.MongoConnectionCreator.{createMongoConnection, dbName}

class RecipeRepository {
  def convertToRecipes(docs: List[Document]): List[Recipe] =
    docs.map { (doc: Document) => {
      val id = doc.get("_id").asInstanceOf[ObjectId].toString
      val recipeLibraryId = doc.get("recipeLibraryId").asInstanceOf[String]
      val name = doc.get("name").asInstanceOf[String]
      val tags: List[String] = doc.getList("tags", classOf[String]).asScala.toList
      Recipe(Some(id), Some(recipeLibraryId), name, tags, None, None)
    }
    }

  def listRecipes(recipeLibraryId: String): List[Recipe] = {
    using(createMongoConnection) { mongoClient =>
      val coll = getRecipeCollection(mongoClient)
      val result = coll.find(new Document("recipeLibraryId", new Document("$eq", recipeLibraryId)))
      return convertToRecipes(result.asScala.toList)
    }
  }

  def convertToRecipe(doc: Document) = {
    val id = doc.get("_id").asInstanceOf[ObjectId].toString
    val recipeLibraryId = doc.get("recipeLibraryId").asInstanceOf[String]
    val name = doc.get("name").asInstanceOf[String]
    val content = doc.get("content").asInstanceOf[String]
    val originalAddress = doc.get("originalAddress").asInstanceOf[String]
    val tags: List[String] = doc.getList("tags", classOf[String]).asScala.toList
    Recipe(Some(id), Some(recipeLibraryId), name, List(), Some(originalAddress), Some(content))
  }

  def findRecipe(id: String): Option[Recipe] = {
    using(createMongoConnection) { mongoClient =>
      val coll = getRecipeCollection(mongoClient)
      val result = coll.find(new Document("_id", new Document("$eq", new ObjectId(id))))
      val resultList = result.asScala.toList
      if (resultList.length > 0)
        Some(convertToRecipe(resultList.head))
      else
        None
    }
  }

  def saveRecipe(recipe: Recipe): String = {
    using(createMongoConnection) { mongoClient =>
      val coll = getRecipeCollection(mongoClient)
      val doc = new Document("name", recipe.name)
        .append("tags", recipe.tags.asJava)
        .append("content", recipe.content.get)
        .append("recipeLibraryId", recipe.recipeLibraryId.get)
        .append("originalAddress", recipe.originalAddress.get)
      if (recipe.id.isDefined) {
        coll.replaceOne(Filters.eq("_id", new ObjectId(recipe.id.get)),doc)
        recipe.id.get
      } else {
        val result = coll.insertOne(doc)
        result.getInsertedId.asObjectId().getValue().toString
      }
    }
  }

  def deleteRecipe (id: String) = {
    using(createMongoConnection) { mongoClient =>
      val coll = getRecipeCollection(mongoClient)
      coll.deleteOne(Filters.eq("_id", new ObjectId(id)))
    }
  }

  def saveRecipeLibrary(recipeLibrary: RecipeLibrary): String = {
    using(createMongoConnection) { mongoClient =>
      val coll = getRecipeLibraryCollection(mongoClient)
      val doc = new Document("name", recipeLibrary.name)
      if (recipeLibrary.id.isDefined) {
        coll.replaceOne(
          Filters.eq("_id",
          new ObjectId(recipeLibrary.id.get)),doc)
        recipeLibrary.id.get
      } else {
        val result = coll.insertOne(doc)
        result.getInsertedId.asObjectId().getValue().toString
      }
    }
  }

  def recipeBelongsToLibrary(recipeId: String, libraryId: String): Boolean = {
    findRecipe(recipeId) match {
      case Some(recipe) => recipe.recipeLibraryId.get == libraryId
      case None => false
    }
  }

  private def getRecipeCollection (mongoClient: MongoClient): MongoCollection[Document] = {
    mongoClient.getDatabase(dbName).getCollection("recipes")
  }

  private def getRecipeLibraryCollection (mongoClient: MongoClient): MongoCollection[Document] = {
    mongoClient.getDatabase(dbName).getCollection("recipeLibraries")
  }

}
