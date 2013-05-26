package recipe.db

object AutoCloseControl {
  def using[A <: {def close() : Unit}, B](closeable: A)(f: A => B): B =
    try {
      f(closeable)
    } finally {
      closeable.close()
    }
}
