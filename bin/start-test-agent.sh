#!/bin/bash

cd ../
PROJECT=`pwd`
CP=$PROJECT/lib/build/*:$PROJECT/lib/dist/*:$PROJECT/build/test/classes:$PROJECT/build/main/classes
CONF="-Djava.util.logging.config.file=./src/test/resources/agent-logging.properties"
WAIT_FOR_DEBUG=n

nohup java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=$WAIT_FOR_DEBUG,address=5007 $CONF -cp $CP com.epickrram.romero.testing.agent.JUnitAgentRunner localhost $@ > $PROJECT/build/agent-console.log &
