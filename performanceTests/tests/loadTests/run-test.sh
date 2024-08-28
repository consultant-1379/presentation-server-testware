#!/bin/bash

TEST_CONFIG=run-test.config
JMX_NAME=various
if [ -f "$TEST_CONFIG" ]; then
    . $TEST_CONFIG
fi

GUI_MODE="-n"
PROPS_FILE="${JMX_NAME}.properties"
JMETER_LOCATION="../../apache-jmeter-2.9/bin/jmeter"
GRAPH_GENERATOR_LOCATION="../../apache-jmeter-2.9/lib/ext/CMDRunner.jar"

### check whether we should run in GUI or non-GUI mode ###
if [[ $# -gt 0 && $1 == "-g" ]]; then
    echo "Running in GUI mode..."
    GUI_MODE=""
fi

### make sure we have everything ###
if [ ! -f $JMETER_LOCATION ]; then
    echo "ERROR: Could not find jmeter at expected location $JMETER_LOCATION"
    exit 1
fi

if [ ! -f $GRAPH_GENERATOR_LOCATION ]; then
    echo "ERROR: Could not find graph generator JAR at expected location $GRAPH_GENERATOR_LOCATION"
    exit 1
fi

### set today's and yesterday's date in the temporary props file ###
TODAY=$(date "+%Y-%m-%d")
YESTERDAY=$(date -r $((`date '+%s'` - 86400)) "+%Y-%m-%d")

if [ -f "test.properties" ]; then
    rm -f "test.properties" > /dev/null 2>&1
fi

cp -f $PROPS_FILE test.properties > /dev/null 2>&1
sed -i -e "s/%%TODAY%%/$TODAY/g" test.properties
sed -i -e "s/%%YESTERDAY%%/$YESTERDAY/g" test.properties

source test.properties

### rotate CSV file ###
if [ -f $LOGFILE ]; then
    echo "Moving previous $LOGFILE to ${LOGFILE}.old"
    mv $LOGFILE ${LOGFILE}.old > /dev/null 2>&1
fi

### cleanup old log file ###
rm -f jmeter.log > /dev/null 2>&1

### kick off the test ###
if [[ $GUI_MODE == "-n" ]]; then
    echo "Running jmeter test with $NUM_THREADS threads for $TEST_DURATION seconds..."
fi
$JMETER_LOCATION $GUI_MODE -t ${JMX_NAME}.jmx -p test.properties -q ../../global_props/jmeter_users.properties -q ../../global_props/jmeter_logger.properties -l $LOGFILE
echo "Jmeter has exited; return code is $?"

### cleanup the temporary properties file ###
rm -f test.properties > /dev/null 2>&1
