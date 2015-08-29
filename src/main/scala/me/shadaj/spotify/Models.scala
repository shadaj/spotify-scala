package me.shadaj.spotify

import play.api.libs.json.Json

case class Error(`type`: String, message: String)

object Error {
  implicit val format = Json.format[Error]
}

case class ConnectResponse(error: Error, version: Int, client_version: String, running: Boolean)

object ConnectResponse {
  implicit val format = Json.format[ConnectResponse]
}

case class TokenResponse(t: String)

object TokenResponse {
  implicit val format = Json.format[TokenResponse]
}

case class CSRFResponse(token: String)

object CSRFResponse {
  implicit val format = Json.format[CSRFResponse]
}

case class Location(og: String)

object Location {
  implicit val format = Json.format[Location]
}

case class Resource(name: Option[String], uri: Option[String], location: Option[Location])

object Resource {
  implicit val format = Json.format[Resource]
}

case class Track(track_resource: Resource, artist_resource: Resource, album_resource: Resource, length: Int, track_type: String)

object Track {
  implicit val format = Json.format[Track]
}

case class OpenGraphState(private_session: Boolean, posting_disabled: Boolean)

object OpenGraphState {
  implicit val format = Json.format[OpenGraphState]
}

case class Status(version: Int, client_version: String, playing: Boolean, shuffle: Boolean, repeat: Boolean, play_enabled: Boolean, prev_enabled: Boolean, next_enabled: Boolean, track: Track, playing_position: Double, server_time: Long, volume: Double, online: Boolean, open_graph_state: OpenGraphState, running: Boolean)

object Status {
  implicit val format = Json.format[Status]
}