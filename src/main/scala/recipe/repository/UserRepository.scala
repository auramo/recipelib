package recipe.repository

import recipe.auth.User
import recipe.db.AutoCloseControl.using
import com.mongodb.client.model.Filters
import org.bson.Document

import scala.collection.JavaConverters._
import recipe.db.MongoConnectionCreator.createMongoConnection

class UserRepository {
  def convertToUser(found: Document) = {
    val id: String = found.get("_id").asInstanceOf[String]
    val passwordHash = found.get("passwordHash").asInstanceOf[String]
    val recipeLibraries: List[String] =
      found.get("recipeLibraries").asInstanceOf[java.util.List[String]].asScala.toList
    User(id, passwordHash, null, recipeLibraries)
  }

  def find(id: String): Option[User] = {
    using(createMongoConnection) { mongoClient =>
      val coll = mongoClient.getDatabase("recipelib").getCollection("users")
      val result = coll.find(new Document("_id", new Document("$eq", id)))
      val it = result.iterator()
      if (it.hasNext()) return Some(convertToUser(it.next()))
      return None
    }
  }

  def update(user: User): Unit = {
    using(createMongoConnection) { mongoClient =>
      val coll = mongoClient.getDatabase("recipelib").getCollection("users")
      val doc = new Document("passwordHash", user.passwordHash)
        .append("email", user.email)
        .append("recipeLibraries", user.recipeLibraries)
      val result = coll.replaceOne(Filters.eq("_id", user.id),doc)
    }
  }
  def insert(user: User): Unit = {
    using(createMongoConnection) { mongoClient =>
      val coll = mongoClient.getDatabase("recipelib").getCollection("users")
      val doc = new Document("_id", user.id)
        .append("passwordHash", user.passwordHash)
        .append("email", user.email)
        .append("recipeLibraries", user.recipeLibraries)
      coll.insertOne(doc)
    }
  }
}
