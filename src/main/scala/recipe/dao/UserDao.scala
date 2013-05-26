package recipe.dao

import com.mongodb.casbah.Imports._
import recipe.db.MongoConnectionCreator
import recipe.auth.User
import recipe.db.AutoCloseControl._

class UserDao {
  def find(id: String): Option[User] = {
    using(MongoConnectionCreator.createMongoConnection()) { mongoClient =>
      val coll = getUserCollection(mongoClient)
      val query = MongoDBObject("_id" -> id)
      val res: Option[DBObject] = coll.findOne(query)
      res.flatMap { resultObject =>
        convertToUser(resultObject)
      }
    }
  }

  def save(user: User) = {
    using(MongoConnectionCreator.createMongoConnection()) { mongoClient =>
      val coll = getUserCollection(mongoClient)
      val newValues = MongoDBObject(
        "_id" -> user.id,
        "email" -> user.email,
        "recipeLibraries" -> user.recipeLibraries)
      coll.save(newValues)
    }
  }

  private def getUserCollection(mongoClient: MongoConnection) = {
    MongoConnectionCreator.openMongoDb(mongoClient)("users")
  }

  private def convertToUser(res: DBObject): Some[User] = {
    val id = res.getAs[String]("_id").get
    val email = res.getAs[String]("email").get
      val recipeLibrariesRaw: MongoDBList = res.getAs[MongoDBList]("recipeLibraries").get
      val recipeLibraries: List[String] = recipeLibrariesRaw.collect { case l: String => l }.toList
    Some(User(id, email, recipeLibraries))
  }
}
