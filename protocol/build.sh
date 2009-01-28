#!/bin/sh

cd gensrc
scalac *.scala &&
rm -f ../src/main/java/uk/org/netvu/protocol/DisplayPicCGI.java &&
scala displaypiccgi > ../src/main/java/uk/org/netvu/protocol/DisplayPicCGI.java &&
chmod -w ../src/main/java/uk/org/netvu/protocol/DisplayPicCGI.java &&
rm -f ../src/main/java/uk/org/netvu/protocol/ReplayPicCGI.java &&
scala replaypiccgi > ../src/main/java/uk/org/netvu/protocol/ReplayPicCGI.java &&
chmod -w ../src/main/java/uk/org/netvu/protocol/ReplayPicCGI.java &&
rm -f ../src/main/java/uk/org/netvu/protocol/ProxyMode.java &&
scala proxymode > ../src/main/java/uk/org/netvu/protocol/ProxyMode.java &&
chmod -w ../src/main/java/uk/org/netvu/protocol/ProxyMode.java &&

rm -f ../src/main/java/uk/org/netvu/protocol/TransmissionMode.java &&
scala transmissionmode > ../src/main/java/uk/org/netvu/protocol/TransmissionMode.java &&
chmod -w ../src/main/java/uk/org/netvu/protocol/TransmissionMode.java

rm -f ../src/main/java/uk/org/netvu/protocol/VideoFormat.java &&
scala videoformat > ../src/main/java/uk/org/netvu/protocol/VideoFormat.java &&
chmod -w ../src/main/java/uk/org/netvu/protocol/VideoFormat.java

rm -f ../src/main/java/uk/org/netvu/protocol/AbstractPicCGI.java &&
scala abstractpiccgi > ../src/main/java/uk/org/netvu/protocol/AbstractPicCGI.java &&
chmod -w ../src/main/java/uk/org/netvu/protocol/AbstractPicCGI.java

cd .. &&
MAVEN_OPTS=-Xmx1G mvn -Dsurefire.useFile=false clean compile test $1 | sed 's/\[WARNING\] //'