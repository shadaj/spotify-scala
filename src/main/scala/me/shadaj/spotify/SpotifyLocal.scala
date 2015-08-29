package me.shadaj.spotify

import javax.net.ssl.{SSLSession, HostnameVerifier, HttpsURLConnection}

import dispatch._
import dispatch.Defaults._
import play.api.libs.json._

import scala.util.Random

import scala.language.experimental.macros

object SpotifyLocal {
  implicit def eitherReads[L: Reads, R: Reads] = {
    new Reads[Either[L, R]] {
      override def reads(json: JsValue): JsResult[Either[L, R]] = {
        val maybeLeft = json.asOpt[L]
        val maybeRight = json.asOpt[R]

        if (maybeLeft.isDefined) JsSuccess(Left(maybeLeft.get))
        else if (maybeRight.isDefined) JsSuccess(Right(maybeRight.get))
        else JsError("Neither types could parse the JSON")
      }
    }
  }

  val RETURN_ON = List("login", "logout", "play", "error", "ap")

  val original = HttpsURLConnection.getDefaultHostnameVerifier
  HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier {
    override def verify(s: String, sslSession: SSLSession): Boolean = {
      original.verify(s, sslSession)
    }
  })

  val subdomain = Random.alphanumeric.take(10).mkString
  val spotiLocal = url(s"https://$subdomain.spotilocal.com:4370") <:< List(
    "Origin" -> "https://open.spotify.com"
  )

  val simpleCsrf = spotiLocal / "simplecsrf"

  val remote = spotiLocal / "remote"

  def oauthToken: Future[TokenResponse] = {
    Http(url("https://open.spotify.com/token")).map(r => Json.parse(r.getResponseBody).as[TokenResponse])
  }

  def csrfToken: Future[CSRFResponse] = {
    Http(simpleCsrf / "token.json").map(r => Json.parse(r.getResponseBody).as[CSRFResponse])
  }

  def openApp = {
    Http(remote / "open.json").map(r => Json.parse(r.getResponseBody).as[ConnectResponse])
  }

  def status(oauth: TokenResponse, csrf: CSRFResponse): Future[Either[Status, ConnectResponse]] = {
    Http(remote / "status.json" <<? Map(
      "csrf" -> csrf.token,
      "oauth" -> oauth.t
    )).map { r =>
      Json.parse(r.getResponseBody).as[Either[Status, ConnectResponse]]
    }
  }

  def statusChange(oauth: TokenResponse, csrf: CSRFResponse, returnAfter: Int): Future[Either[Status, ConnectResponse]] = {
    Http(remote / "status.json" <<? Map(
      "csrf" -> csrf.token,
      "oauth" -> oauth.t,
      "returnafter" -> returnAfter.toString,
      "returnon" -> RETURN_ON.mkString(",")
    )).map { r =>
      Json.parse(r.getResponseBody).as[Either[Status, ConnectResponse]]
    }
  }

  def play(oauth: TokenResponse, csrf: CSRFResponse, uri: String) = {
    Http(remote / "play.json" <<? Map("oauth" -> oauth.t, "csrf" -> csrf.token, "spotify_uri" -> uri, "context" -> uri)).map(_.getResponseBody)
  }

  def setPaused(oauth: TokenResponse, csrf: CSRFResponse, paused: Boolean) = {
    Http(remote / "pause.json" <<? Map("oauth" -> oauth.t, "csrf" -> csrf.token, "pause" -> paused.toString)).map(_.getResponseBody)
  }

  def pause(oauth: TokenResponse, csrf: CSRFResponse) = {
    setPaused(oauth, csrf, true)
  }

  def unpause(oauth: TokenResponse, csrf: CSRFResponse) = {
    setPaused(oauth, csrf, false)
  }

  def waitForConnected(oauth: TokenResponse, csrf: CSRFResponse): Future[Status] = {
    status(oauth, csrf).flatMap { e =>
      if (e.isLeft) {
        Future.successful(e.left.get)
      } else {
        waitForConnected(oauth, csrf)
      }
    }
  }
}
