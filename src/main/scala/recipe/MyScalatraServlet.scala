package recipe

import org.scalatra._
import scalate.ScalateSupport
import auth.SampleConsumer

class MyScalatraServlet extends RecipelibStack {
  val consumer = new SampleConsumer

  get("/") {
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
                            /*
  get("/formredirect") {
      <html xmlns="http://www.w3.org/1999/xhtml">
        <head>
          <title>OpenID HTML FORM Redirection</title>
        </head>
        <body onload="document.forms['openid-form-redirection'].submit();">
          <form name="openid-form-redirection" action="${message.OPEndpoint}" method="post" accept-charset="utf-8">
            <c:forEach var="parameter" items="${message.parameterMap}">
              <input type="hidden" name="${parameter.key}" value="${parameter.value}"/>
            </c:forEach>
            <button type="submit">Continue...</button>
          </form>
        </body>
      </html>
  }
         */
  def authenticate {
    println("consumer instance in authenticate: ")
    println(consumer)
    val googleOpenIdRequestString = "https://www.google.com/accounts/o8/id"
    consumer.authRequest(googleOpenIdRequestString, request, response)
  }

/*
  if (userSuppliedString.startsWith(GOOGLE_ENDPOINT)) {
    fetch.addAttribute("email",
      "http://axschema.org/contact/email", true);
    fetch.addAttribute("firstName",
      "http://axschema.org/namePerson/first", true);
    fetch.addAttribute("lastName",
      "http://axschema.org/namePerson/last", true);
*/

}
