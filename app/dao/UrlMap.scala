package dao

import javax.inject._
import java.util.concurrent.ConcurrentHashMap

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

import services.EnvironmentService


@Singleton
class UrlMap @Inject()(environmentService: EnvironmentService) {
  private val urlMap:ConcurrentHashMap[Int, Int] = new ConcurrentHashMap[Int, Int]()
  private final val retryLimit: Int = 1

  private final val threshold: Int = environmentService.getThreshold
  private final val ttl: Long = environmentService.getTtl

  def upsertAndGetStatus(urlHash: Int, retries: Int): Boolean = {
    if (retryLimit < retries) {
      throw new Exception("Max retries limit reached - upsertAndGetStatus")
    }
    try {
      val newCounter = urlMap.compute(urlHash, (url: Int, counter: Int) => {
        if (0 == counter) {
          resetCounterAfterTtl(urlHash, 0)
          1
        } else if (threshold >= counter) {
          counter + 1
        } else {
          counter
        }
      })
      threshold < newCounter
    } catch {
      case e: NullPointerException => throw e
      case e: IllegalStateException => throw e
      case e: RuntimeException => upsertAndGetStatus(urlHash, retries+1)
    }
  }

  private def resetCounterAfterTtl(urlHash: Int, retries: Int): Unit = {
    if (retryLimit < retries) {
      throw new Exception("Max retries limit reached - resetCounterAfterTtl")
    }
    Future{
      Thread.sleep(ttl)
    }.onComplete {
      case Success(res) => resetCounter(urlHash, 0)
      case Failure(e) => resetCounterAfterTtl(urlHash, retries+1)
    }
  }

  private def resetCounter(urlHash: Int, retries: Int): Int = {
    if (retryLimit < retries) {
      throw new Exception("Max retries limit reached - resetCounter")
    }
    try {
      urlMap.compute(urlHash, (url:Int, counter:Int) => {
        0
      })
    } catch {
      case e: NullPointerException => throw e
      case e: IllegalStateException => throw e
      case e: RuntimeException => resetCounter(urlHash, retries+1)
    }
  }
}
