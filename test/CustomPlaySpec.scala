package test

import com.google.inject.binder.{AnnotatedBindingBuilder, LinkedBindingBuilder}
import com.google.inject.{Binder, Key, Module, TypeLiteral}
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.cache.SyncCacheApi
import org.scalatestplus.play.PlaySpec

import org.mockito.Mockito._
import org.mockito.Matchers.any
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar

import scala.reflect.ClassTag

abstract class CustomPlaySpec extends PlaySpec with Module {
  type Binder = com.google.inject.Binder

  lazy val app: Application = new GuiceApplicationBuilder().overrides(this).configure(playConfig).build

  override def configure(binder: Binder): Unit = {
    bind(classOf[SyncCacheApi])(binder).toInstance(new FakeCache())

    configureDependencies(binder)
  }

  // set play configuration
  def playConfig: Map[String, Any] = Map()

  def configureDependencies(implicit binder: Binder): Unit = {}

  /**
    * @see Binder#bind(Key)
    */
  def bind[T](key: Key[T])(implicit binder: Binder): LinkedBindingBuilder[T] = binder.bind(key)

  /**
    * @see Binder#bind(TypeLiteral)
    */
  def bind[T](typeLiteral: TypeLiteral[T])(implicit binder: Binder): AnnotatedBindingBuilder[T] = binder.bind(typeLiteral)

  /**
    * @see Binder#bind(Class)
    */
  def bind[T](clazz: Class[T])(implicit binder: Binder): AnnotatedBindingBuilder[T] = binder.bind(clazz)

  def instanceOf[T](implicit evidence: ClassTag[T]): T = app.injector.instanceOf[T]
}

abstract class MockitoPlaySpec extends CustomPlaySpec with MockitoSugar with BeforeAndAfterEach {
  lazy override val app: Application = new GuiceApplicationBuilder().overrides(this).configure(playConfig).build

  override def afterEach: Unit = {}

  def afterEachTest: Unit = {
    afterEach()
  }
}

abstract class MockitoAppPlaySpec extends MockitoPlaySpec with GuiceOneAppPerSuite {
  lazy override val app: Application = new GuiceApplicationBuilder().overrides(this).configure(playConfig).build
}
