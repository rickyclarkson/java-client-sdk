package uk.org.netvu.data

import _root_.org.{specs, scalacheck}
import specs.runner.JUnit4
import specs.{Specification, ScalaCheck => Scalacheck}
import scalacheck.{Arbitrary, Gen}
import Arbitrary.arbitrary
import java.nio.ByteOrder

class ImageDataStructBuilderTest extends JUnit4(new Specification with Scalacheck {

 def intToBytes(i: Int) = {
  val buffer = java.nio.ByteBuffer allocate 4 putInt i
  buffer position 0
  val array: Array[Byte] = new Array(4)
  buffer get array
  array
 }

 implicit val videoFormats: Arbitrary[VideoFormat] = Arbitrary { Gen.elements(VideoFormat.values() : _*) }
 implicit val theresNoDamnShort: Arbitrary[Short] = Arbitrary { arbitrary[Int] map (_.toShort) }
 implicit val pictureStructs: Arbitrary[PictureStruct] = Arbitrary {
  for { srcPixels <- arbitrary[Short]
        srcLines <- arbitrary[Short]
        targetPixels <- arbitrary[Short]
        targetLines <- arbitrary[Short]
        pixelOffset <- arbitrary[Short]
        lineOffset <- arbitrary[Short] } yield (new PictureStructBuilder(ByteOrder.BIG_ENDIAN) srcPixels srcPixels
                                                srcLines srcLines targetPixels targetPixels targetLines targetLines
                                                pixelOffset pixelOffset lineOffset lineOffset build)
 }

 implicit val imageDataStructBuilders: Arbitrary[ImageDataStructBuilder] = 
  Arbitrary {
   for { version <- Gen.choose(0xDECADE10, 0xDECADE11)
//         mode <- arbitrary[Int]
//         camera <- arbitrary[Int]
         videoFormat <- arbitrary[VideoFormat]
//         startOffset <- arbitrary[Int]
//         size <- arbitrary[Int]
//         maxSize <- arbitrary[Int]
//         targetSize <- arbitrary[Int]
//         qFactor <- arbitrary[Int]
//         alarmBitmaskHigh <- arbitrary[Int]
//         status <- arbitrary[Int]
//         sessionTime <- arbitrary[Int]
         milliseconds <- Gen.choose(0, 999)
         res <- arbitrary[Int] map intToBytes
         title <- Gen.identifier map (s => if (s.length > ImageDataStruct.TITLE_LENGTH) s.substring(0, ImageDataStruct.TITLE_LENGTH) else s)
         alarm <- Gen.identifier map (s => if (s.length > ImageDataStruct.TITLE_LENGTH) s.substring(0, ImageDataStruct.TITLE_LENGTH) else s)
         format <- arbitrary[PictureStruct]
         locale <- Gen.identifier map (s => if (s.length > ImageDataStruct.MAX_NAME_LENGTH) s.substring(0, ImageDataStruct.MAX_NAME_LENGTH) else s)
//         utcOffset <- arbitrary[Int]
//         alarmBitmask <- arbitrary[Int]
       } yield {
        val builder = new ImageDataStructBuilder()
        val mode = 4
        val camera = 4
        val startOffset = 4
        val size = 4
        val maxSize = 4
        val targetSize = 4
        val qFactor = 4
        val alarmBitmaskHigh = 4
        val status = 4
        val sessionTime = 4
        val utcOffset = 4
        val alarmBitmask = 4

        builder.version(version)
        builder.mode(mode)
        builder.camera(camera)
        builder.videoFormat(videoFormat)
        builder.startOffset(startOffset)
        builder.size(size)
        builder.maxSize(maxSize)
        builder.targetSize(targetSize)
        builder.qFactor(qFactor)
        builder.alarmBitmaskHigh(alarmBitmaskHigh)
        builder.status(status)
        builder.sessionTime(sessionTime)
        builder.milliseconds(milliseconds)
        builder.res(res)
        builder.title(title)
        builder.alarm(alarm)
        builder.format(format)
        builder.locale(locale)
        builder.utcOffset(utcOffset)
        builder.alarmBitmask(alarmBitmask)
        builder
       }
  }
 def complete = arbitrary[ImageDataStructBuilder].sample.get
 val setters = List[ImageDataStructBuilder => ImageDataStructBuilder](_ version 0xDECADE10, _ mode 4, _ camera 4,
                                                                      _ videoFormat VideoFormat.JPEG_422,
                                                                      _ startOffset 4, _ size 4, _ maxSize 4,
                                                                      _ targetSize 4, _ qFactor 4,
                                                                      _ alarmBitmaskHigh 4, _ status 4,
                                                                      _ sessionTime 4, _ milliseconds 4,
                                                                      _ res Array(2,3,4,5), _ title "4",
                                                                      _ alarm "4",
                                                                      _ format arbitrary[PictureStruct].sample.get,
                                                                      _ locale "4", _ utcOffset 4,
                                                                      _ alarmBitmask 4)
 import uk.org.netvu.util.BuildersTests

 "Builder constraints" areSpecifiedBy BuildersTests.testBuilder[ImageDataStruct, ImageDataStructBuilder](new ImageDataStructBuilder, complete, setters, "ImageDataStructBuilderTest")
})
