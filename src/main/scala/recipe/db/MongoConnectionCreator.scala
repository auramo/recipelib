package recipe.db

/*
import com.mongodb.casbah.Imports._
*/
import com.mongodb.client.{MongoClient, MongoClients}

import scala.util.Properties

object MongoConnectionCreator {
  def createMongoConnection: MongoClient = {
    val url = Properties.envOrElse("MONGOHQ_URL" ,"mongodb://localhost:27017")
    println("Connecting to mongo URL", url)
    MongoClients.create(url)
  }

  def dbName: String = Properties.envOrElse("MONGO_DB_NAME" ,"recipelib")
}
