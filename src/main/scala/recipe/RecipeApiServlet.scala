package recipe

class RecipeApiServlet extends RecipelibStack {
  get("/api/") {
    """{"data": 5}"""
  }
  get("/") {
    """
      {"recipes": [{"id": "1", "name": "Maksalaatikko", "tags": ["loota", "perinneruoka"], "content":"<p>maksat uuniin</p>"},
                   {"id": "2", "name": "Lihapullat", "tags": ["perus"], "content":"<p>jatketta, pyöritä ja laita vuokaan uuniin</p>"},
                   {"id": "3", "name": "Kalapuikot", "tags": ["eines", "paha"], "content":"<p>pellille vaan</p>"}
                   ]}
    """
  }

  post("/") {
    println("Got this:")
    println(request)
    println(request.body)
    """{"ok": true}"""
  }
}
