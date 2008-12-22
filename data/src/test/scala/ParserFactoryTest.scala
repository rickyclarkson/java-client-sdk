package uk.org.netvu.data

import _root_.org.{scalacheck, specs}
import specs.runner.JUnit4
import specs.Specification

class ParserFactoryTest extends JUnit4(new Specification {
 "Getting a parser for a supported content type" should {
  "succeed" in {
   List("multipart/x-mixed-replace", "video/adhbinary") foreach (ParserFactory parserFor _)
   true mustBe true
  }
 }

 "Getting a parser for an unsupported content type" should {
  "cause an IllegalArgumentException" in {
   for (t <- List("pultimart/x-mixed-replace", "diveo/adhbinary", "text/plain"))
    ParserFactory parserFor t must throwAn[IllegalArgumentException]
  }
 }
})
