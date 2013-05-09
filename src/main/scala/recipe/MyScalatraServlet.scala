package recipe

import org.scalatra._
import scalate.ScalateSupport
import auth.SampleConsumer

class MyScalatraServlet extends RecipelibStack {
  val consumer = new SampleConsumer

  get("/") {
    println("pow")
    println(request.getScheme() + "://" +
    request.getServerName() + ":" +
    request.getServerPort())

    <html>
      <body>
        <h1>Hello, world!</h1>
        Say <a href="hello-scalate">hello to Scalate</a>.
      </body>
    </html>
  }

  get("/openid") {
    println("to return page")
    println("consumer instance: " + consumer)
    val retval = consumer.verifyResponse(request)
    println("retval: " + retval)

    <html>
      <body>
        <h1>Openid return page</h1>
      </body>
    </html>
  }

  get("/authenticate") {
    println("before authenticate")
    authenticate
    println("after authenticate")
  }

  def authenticate {
    println("consumer instance in authenticate: ")
    println(consumer)
    val googleOpenIdRequestString = "https://www.google.com/accounts/o8/id"
    val returnToUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/openid"
    consumer.authRequest(googleOpenIdRequestString, returnToUrl, request, response)
  }


}
