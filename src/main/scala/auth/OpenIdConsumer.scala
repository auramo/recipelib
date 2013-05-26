package auth

import org.openid4java.discovery.Identifier
import org.openid4java.discovery.DiscoveryInformation
import org.openid4java.message.ax.FetchRequest
import org.openid4java.message.ax.FetchResponse
import org.openid4java.message.ax.AxMessage
import org.openid4java.message._
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.util.List
import org.openid4java.consumer.{VerificationResult, ConsumerManager}

case class AuthenticatedUser(identifier: Identifier, email: Option[String])

class OpenIdConsumer {
  val manager: ConsumerManager = new ConsumerManager

  def authenticateGoogleUser(request: HttpServletRequest, response: HttpServletResponse) {
    val googleOpenIdRequestString = "https://www.google.com/accounts/o8/id"
    val portPart = if (request.getServerPort != 80) ":" + request.getServerPort() else ""
    val returnToUrl = request.getScheme() + "://" + request.getServerName() + portPart + "/openid"

    println("Return to URL: " + returnToUrl)

    authRequest(googleOpenIdRequestString, returnToUrl, request, response)
  }

  def authRequest(userSuppliedString: String, returnToUrl: String, httpReq: HttpServletRequest, httpResp: HttpServletResponse) {
    val discoveries = manager.discover(userSuppliedString)
    val discovered = manager.associate(discoveries)
    httpReq.getSession.setAttribute("openid-disc", discovered)
    val authReq = manager.authenticate(discovered, returnToUrl)
    val fetch = FetchRequest.createFetchRequest
    fetch.addAttribute("email", "http://schema.openid.net/contact/email", true)
    authReq.addExtension(fetch)
    httpResp.sendRedirect(authReq.getDestinationUrl(true))
  }

  def verifyResponse(httpReq: HttpServletRequest): Either[String, AuthenticatedUser] = {
    try {
      doVerifyResponse(httpReq)
    }
    catch {
      case e: Exception => {
        e.printStackTrace
        Left(e.getMessage)
      }
    }
  }

  private def doVerifyResponse(httpReq: HttpServletRequest): Either[String, AuthenticatedUser] = {
    val response: ParameterList = new ParameterList(httpReq.getParameterMap)
    val discovered: DiscoveryInformation = httpReq.getSession.getAttribute("openid-disc").asInstanceOf[DiscoveryInformation]
    val receivingURL: StringBuffer = httpReq.getRequestURL
    val queryString: String = httpReq.getQueryString
    if (queryString != null && queryString.length > 0) receivingURL.append("?").append(httpReq.getQueryString)
    val verification: VerificationResult = manager.verify(receivingURL.toString, response, discovered)
    val verified: Identifier = verification.getVerifiedId
    if (verified != null) {
      val authSuccess: AuthSuccess = verification.getAuthResponse.asInstanceOf[AuthSuccess]
      if (authSuccess.hasExtension(AxMessage.OPENID_NS_AX)) {
        val fetchResp: FetchResponse = authSuccess.getExtension(AxMessage.OPENID_NS_AX).asInstanceOf[FetchResponse]
        val emails: List[_] = fetchResp.getAttributeValues("email")
        val email: String = emails.get(0).asInstanceOf[String]
        return Right(AuthenticatedUser(verified, Some(email)))
      }
      return Right(AuthenticatedUser(verified, None))
    }
    Left("Not authenticated")
  }
}