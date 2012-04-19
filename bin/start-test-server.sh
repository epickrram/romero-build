#!/bin/bash

cd ../
PROJECT=`pwd`
CP=$PROJECT/lib/build/*:$PROJECT/lib/dist/*:$PROJECT/build/test/classes:$PROJECT/build/main/classes
CONF="-Djava.util.logging.config.file=./src/test/resources/server-logging.properties"

nohup java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 $CONF -cp $CP com.epickrram.romero.util.WebAppRunner $@ > $PROJECT/build/server-console.log &
