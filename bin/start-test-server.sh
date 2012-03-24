#!/bin/bash

cd ../
PROJECT=`pwd`
CP=$PROJECT/lib/build/*:$PROJECT/lib/dist/*:$PROJECT/build/test/classes:$PROJECT/build/main/classes

echo  java -cp $CP com.epickrram.romero.util.WebAppRunner $@ 
nohup java -cp $CP com.epickrram.romero.util.WebAppRunner $@ > $PROJECT/build/server.log &
