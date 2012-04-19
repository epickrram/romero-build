#!/bin/bash

cd ../
PROJECT=`pwd`
CP=$PROJECT/lib/build/*:$PROJECT/lib/dist/*:$PROJECT/build/test/classes:$PROJECT/build/main/classes

nohup java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5007 -cp $CP com.epickrram.romero.testing.agent.JUnitAgentRunner localhost $@ > $PROJECT/build/agent.log &
