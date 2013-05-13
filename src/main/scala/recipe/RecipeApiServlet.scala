package recipe

class RecipeApiServlet extends RecipelibStack {
  get("/api/") {
    """{"data": 5}"""
  }
  get("/") {
    """{"recipes": ["a", "b"]}"""
  }
}
