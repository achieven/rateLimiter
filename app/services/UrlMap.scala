package services

import javax.inject._
import scala.concurrent.ExecutionContext.Implicits.global

import java.util.concurrent.ConcurrentHashMap
import scala.concurrent.{Future}
import scala.util.{Success}


@Singleton
class UrlMap {
  private val urlMap:ConcurrentHashMap[String, Int] = new ConcurrentHashMap[String, Int]()
  private final val threshold: Int = 2
  private final val ttl: Int = 1500



  def upsertAndGetStatus(urlHash: String): Boolean = {
   val newCounter = (urlMap.compute(urlHash, (k: String, v: Int) => {
     if (0 == v) {
       resetCounter(urlHash)
       1
     } else if (threshold >= v) {
       v+1
     } else {
       v
     }
   }))
   if (threshold < newCounter) true else false
  }

  def resetCounter(urlHash: String): Unit = {
    Future{
      Thread.sleep(ttl)
      false
    }.onComplete{
      case Success(res) => {
        urlMap.compute(urlHash, (k:String, v:Int) => {
          0
        })
      }
    }

  }
}
