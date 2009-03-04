package uk.org.netvu.filter

import javax.swing._
import java.awt._
import javax.imageio._
import java.io._

object ManualCheck { def main(args: Array[String]) = {
 val frame = new JFrame
 frame setLayout new FlowLayout
 frame add new JLabel(new ImageIcon("../jpeg/dvip3s-ad-dev-adh-352x256.jpeg"))
 frame add new JLabel(new ImageIcon(new MonochromeFilter().filter(ImageIO.read(new File("../jpeg/dvip3s-ad-dev-adh-352x256.jpeg")))))
 frame add new JLabel(new ImageIcon(new ContrastFilter(1.5).filter(ImageIO.read(new File("../jpeg/dvip3s-ad-dev-adh-352x256.jpeg")))))
 frame add new JLabel(new ImageIcon(BrightnessFilter.betterBrightnessFilter(200).filter(ImageIO.read(new File("../jpeg/dvip3s-ad-dev-adh-352x256.jpeg")))))
 frame.pack
 frame setVisible true
} }
