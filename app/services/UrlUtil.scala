package services

import javax.inject._
import scala.util.hashing.MurmurHash3
import java.net.URL

@Singleton
class UrlUtil {
  def validateUrl(url: String): Boolean = {
    try {
      new URL(url)
      true
    } catch {
      case e: Exception => false
    }
  }
  def hashUrl(url: String): Int = {
    MurmurHash3.stringHash(url)
  }
}
