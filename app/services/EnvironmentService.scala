package services

import java.net.URL

import javax.inject._

import scala.util.hashing.MurmurHash3

@Singleton
class EnvironmentService {
  private final val threshold: Int = System.getProperty("threshold").toInt
  private final val ttl: Long = System.getProperty("ttl").toLong

  verifyCmdParams

  private def verifyCmdParams: Unit = {
    if (0 > threshold) {
      throw new Error("threshold must be a positive integer")
    }
    if (0 > ttl) {
      throw new Error("ttl must be a positive long")
    }
  }

  def getThreshold: Int = threshold
  def getTtl: Long= ttl
}
