#!/bin/bash

cd ../
PROJECT=`pwd`
CP=$PROJECT/lib/build/*:$PROJECT/lib/dist/*:$PROJECT/build/test/classes:$PROJECT/build/main/classes:$PROJECT/src/main/resources
CONF="-Djava.util.logging.config.file=./src/test/resources/server-logging.properties"
WAIT_FOR_DEBUG=n

nohup java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=$WAIT_FOR_DEBUG,address=5005 $CONF -cp $CP com.epickrram.romero.util.WebAppRunner $@ > $PROJECT/build/server-console.log &
