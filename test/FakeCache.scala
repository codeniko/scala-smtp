package test

import play.api.cache.SyncCacheApi
import scala.collection.mutable
import scala.concurrent.duration._
import scala.reflect.ClassTag

class FakeCache extends SyncCacheApi {
  private val cache: mutable.HashMap[String, Any] = new mutable.HashMap()

  override def get[T](key: String)(implicit ct: ClassTag[T]) = cache.get(key).asInstanceOf[Option[T]]
  override def set(key: String, value: Any, expiration: Duration = Duration.Inf) { val _ = cache.put(key, value) }
  override def remove(key: String) { val _ = cache.remove(key) }
  override def getOrElseUpdate[A: ClassTag](key: String, expiration: Duration)(orElse: => A) = get[A](key) match {
    case Some(v) => v
    case _ =>
      val v: A = orElse
      set(key, v, expiration)
      v
  }
}
