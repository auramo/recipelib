/*
 * Copyright 2006-2007 Sxip Identity Corporation
 */
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

/**
 * Sample Consumer (Relying Party) implementation.
 */
class SampleConsumer {
  val manager: ConsumerManager = new ConsumerManager

  def authRequest(userSuppliedString: String, returnToUrl: String, httpReq: HttpServletRequest, httpResp: HttpServletResponse): String = {
    try {
      val returnToUrl = "http://localhost:8080/openid"
      val discoveries = manager.discover(userSuppliedString)
      val discovered = manager.associate(discoveries)
      httpReq.getSession.setAttribute("openid-disc", discovered)
      val authReq = manager.authenticate(discovered, returnToUrl)
      val fetch = FetchRequest.createFetchRequest
      fetch.addAttribute("email", "http://schema.openid.net/contact/email", true)
      authReq.addExtension(fetch)
      httpResp.sendRedirect(authReq.getDestinationUrl(true))
      return null
    }
    catch {
      case e: Exception => {
        e.printStackTrace
      }
    }
    return null
  }

  def verifyResponse(httpReq: HttpServletRequest): Identifier = {
    try {
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
          println(s"Got email: $email")
        }
        return verified
      }
    }
    catch {
      case e: Exception => {
        e.printStackTrace
      }
    }
    return null
  }
}