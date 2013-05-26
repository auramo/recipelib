package recipe.dao

object DaoTester {
  def main(args: Array[String]) {
    val dao = new UserDao
    println(dao.find("1"))
    println(dao.find("2"))
  }
}
