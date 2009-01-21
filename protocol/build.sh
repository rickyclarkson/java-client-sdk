#!/bin/sh

find . -name \*.m4 -not -name macros.m4 -exec ./m4it.sh {} \;

cd gensrc
rm -f ../src/main/java/uk/org/netvu/protocol/DisplayPicCGI.java
scalac displaypiccgi.scala && scala displaypiccgi > ../src/main/java/uk/org/netvu/protocol/DisplayPicCGI.java &&
chmod -w ../src/main/java/uk/org/netvu/protocol/DisplayPicCGI.java &&

rm -f ../src/main/java/uk/org/netvu/protocol/ReplayPicCGI.java
scalac replaypiccgi.scala && scala replaypiccgi > ../src/main/java/uk/org/netvu/protocol/ReplayPicCGI.java &&
chmod -w ../src/main/java/uk/org/netvu/protocol/ReplayPicCGI.java &&
cd ..

MAVEN_OPTS=-Xmx1G mvn -Dsurefire.useFile=false clean compile test $1 | sed 's/\[WARNING\] //'