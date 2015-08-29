package me.shadaj.spotify

import dispatch._

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.io.StdIn

import SpotifyLocal._

import scala.concurrent.ExecutionContext.Implicits.global

object Main extends App {
  val oauth = Await.result(oauthToken, Duration.Inf)
  Await.result(openApp.map(r => println(r)), Duration.Inf)
  val csrf = Await.result(csrfToken, Duration.Inf)

  Await.result(waitForConnected(oauth, csrf).map(r => println(r)), Duration.Inf)
  Await.result(play(oauth, csrf, "spotify:track:0qOnSQQF0yzuPWsXrQ9paz"), Duration.Inf)

  def waitStatusChanges: Future[Unit] = {
    val ret = statusChange(oauth, csrf, 5).flatMap { r =>
      println(s"status: $r")
      waitStatusChanges
    }
    ret.onFailure {
      case e => println(e)
    }
    ret
  }

  waitStatusChanges
}

/* TODO: write tests to check that ads are parsed right; example data follows:
  {
    "version": 9,
    "client_version": "1.0.11.134.ga37df67b",
    "playing": false,
    "shuffle": false,
    "repeat": false,
    "play_enabled": true,
    "prev_enabled": false,
    "next_enabled": false,
    "track": {
      "track_resource": {
      },
      "artist_resource": {
        "name": "Universal Music Group"
      },
      "album_resource": {
        "name": "spotify:user:digster.fm:playlist:0Gy1TwCxPFTMCiwxGLVkc1"
      },
      "length": 30,
      "track_type": "ad"
    },
    "playing_position": 9.568,
    "server_time": 1439077390,
    "volume": 0.4799878,
    "online": true,
    "open_graph_state": {
      "private_session": false,
      "posting_disabled": true
    },
    "running": true
  }
 */
