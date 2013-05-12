package recipe

class SspTestServlet  extends RecipelibStack {

  get("/ssptest") {
    contentType="text/html"
    ssp("hello-ssp.ssp", "foo" -> "uno", "bar" -> "dos")
  }

}
