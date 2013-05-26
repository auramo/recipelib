package recipe.api

import recipe.RecipelibStack

class RecipeApiServlet extends RecipelibStack {
  get("/api/") {
    """{"data": 5}"""
  }
  get("/") {
    """
      {"recipes": [{"id": "1", "name": "Maksalaatikko", "tags": ["loota", "perinneruoka"]},
                   {"id": "2", "name": "Lihapullat", "tags": ["perus"]},
                   {"id": "3", "name": "Kalapuikot", "tags": ["eines", "paha"]}
                   ]}
    """
  }

  get("/:id") {
    val id = params("id")
    println("Got request with id: " + id)
    """
      {"id": "1", "name": "Maksalaatikko", "tags": ["loota", "perinneruoka"], "content":"maksat uuniin", "originalAddress": "http://huu.haa" }
    """
  }

  post("/") {
    println("Post got this:")
    println(request)
    println(request.body)
    """{"ok": true}"""
  }

  put("/:id") {
    println("PUT got this:")
    val id = params("id")
    println("PUT request with id: " + id)
    println(request.body)
    """{"ok": true}"""
  }

  delete("/:id") {
    val id = params("id")
    println("DELETE request with id: " + id)
    """{"ok": true}"""
  }
}
