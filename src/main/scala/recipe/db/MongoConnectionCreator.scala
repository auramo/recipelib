package recipe.db

import com.mongodb.casbah.Imports._
import scala.util.Properties

object MongoConnectionCreator {
  case class ConnectionParams(host: String, port: Int, dbName: String, user: String = "", password: String = "")
  lazy val connectionParams: ConnectionParams = getConnectionParams

  private def getConnectionParams: ConnectionParams = {
    val url = Properties.envOrNone("MONGOHQ_URL")
    val regex = """mongodb://(\w+):(\w+)@([\w|\.]+):(\d+)/(\w+)""".r
    url match {
      case Some(regex(u, p, host, port, dbName)) =>
        ConnectionParams(host, port.toInt, dbName, u, p)
      case None => {
        ConnectionParams("127.0.0.1", 27017, "recipelib")
      }
    }
  }
  def dbName = connectionParams.dbName
  def createMongoConnection(): MongoConnection = {
    println("connectionParams")
    println(connectionParams)
    MongoConnection(connectionParams.host, connectionParams.port)
  }

  def openMongoDb(conn: MongoConnection): MongoDB = {
    val db = conn(connectionParams.dbName)
    if (!connectionParams.user.isEmpty) {
      db.authenticate(connectionParams.user, connectionParams.password)
    }
    db
  }

  def main(args: Array[String]) {
    println("Trying to open mongo connection")
    val mongoClient = createMongoConnection()
    val collName = "test"
    println("DB name " + dbName)
    val coll: MongoCollection = openMongoDb(mongoClient)(collName)
    println(coll)
    mongoClient.close
  }
}
