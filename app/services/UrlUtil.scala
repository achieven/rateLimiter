package services

import javax.inject._
import java.net.URL
import java.util.UUID

@Singleton
class UrlUtil {
  def validateUrl (url: String): Boolean = {
    try {
      new URL(url)
      true
    } catch {
      case e: Exception => false
    }
  }
  def hashUrl(url: String): String = {
    UUID.nameUUIDFromBytes(url.getBytes).toString
  }
}
