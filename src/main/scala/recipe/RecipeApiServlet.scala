package recipe

class RecipeApiServlet extends RecipelibStack {
  get("/api/") {
    """{"data": 5}"""
  }
  get("/") {
    """
      {"recipes": [{"name": "Maksalaatikko", "tags": ["loota", "perinneruoka"], "content":"<p>maksat uuniin</p>"},
                   {"name": "Lihapullat", "tags": ["perus"], "content":"<p>jatketta, pyöritä ja laita vuokaan uuniin</p>"},
                   {"name": "Kalapuikot", "tags": ["eines", "paha"], "content":"<p>pellille vaan</p>"}
                   ]}
    """
  }
}
