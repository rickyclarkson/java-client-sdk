package uk.org.netvu.util

import _root_.org.{specs, scalacheck}
import specs.Specification
import specs.runner.JUnit4
import java.nio.ByteBuffer

class ValidationTest extends JUnit4(new Specification {
 "Validation.isValidJPEG" should {
  "pass for FFD80010FFD9" in {
   Validation.isValidJPEG(ByteBuffer.wrap(Array(0xFF, 0xD8, 0x00, 0x10, 0xFF, 0xD9) map (_ toByte))) mustBe true
  }
  "fail for FFD80010" in {
   Validation.isValidJPEG(ByteBuffer.wrap(Array(0xFF, 0xD8, 0x00, 0x10) map (_ toByte))) mustBe false
  }
  "fail for 0010FFD9" in {
   Validation.isValidJPEG(ByteBuffer.wrap(Array(0x00, 0x10, 0xFF, 0xD9) map (_ toByte))) mustBe false
  }
 }
})
                         
