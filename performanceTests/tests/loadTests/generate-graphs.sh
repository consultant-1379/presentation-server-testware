#!/bin/bash

PROPS_FILE=${1:-loadTests.properties}
GRAPH_GENERATOR_LOCATION="../../apache-jmeter-2.9/lib/ext/CMDRunner.jar"

source $PROPS_FILE

if [ ! -f $GRAPH_GENERATOR_LOCATION ]; then
    echo "ERROR: Could not find graph generator JAR at expected location $GRAPH_GENERATOR_LOCATION"
    exit 1
fi

### if we don't have a logfile, fail ###
if [ ! -f $LOGFILE ]; then
    echo "Could not find jmeter log file $LOGFILE after test run"
    exit 1
fi

### generate graphs ###
echo ""
echo "Generating response time graph..."
java -jar $GRAPH_GENERATOR_LOCATION --tool Reporter --generate-png response_times.png --input-jtl $LOGFILE --plugin-type ResponseTimesOverTime --relative-times no --width 1280 --height 1024 > /dev/null 2>&1
if [ ! -f response_times.png ]; then
    echo "ERROR: Could not generate response time graph file response_times.png"
    exit 2
else
    echo "Generated response time graph file response_times.png"
fi

echo ""
echo "Generating TPS graph..."
java -jar $GRAPH_GENERATOR_LOCATION --tool Reporter --generate-png tps.png --input-jtl $LOGFILE --plugin-type TransactionsPerSecond --relative-times no --width 1280 --height 1024 > /dev/null 2>&1
if [ ! -f tps.png ]; then
    echo "ERROR: Could not generate TPS graph file tps.png"
    exit 2
else
    echo "Generated TPS graph file tps.png"
fi

echo ""
echo "Generating active thread graph..."
java -jar $GRAPH_GENERATOR_LOCATION --tool Reporter --generate-png active_threads.png --input-jtl $LOGFILE --plugin-type ThreadsStateOverTime --relative-times no --width 1280 --height 1024 > /dev/null 2>&1
if [ ! -f active_threads.png ]; then
    echo "ERROR: Could not generate active thread graph file active_threads.png"
    exit 2
else
    echo "Generated active thread graph file active_threads.png"
fi

echo ""
echo "Generating bytes throughut graph..."
java -jar $GRAPH_GENERATOR_LOCATION --tool Reporter --generate-png bytes_throughput.png --input-jtl $LOGFILE --plugin-type BytesThroughputOverTime --relative-times no --width 1280 --height 1024 > /dev/null 2>&1
if [ ! -f bytes_throughput.png ]; then
    echo "ERROR: Could not generate bytes throughput graph file bytes_throughput.png"
    exit 2
else
    echo "Generated bytes throughput graph file bytes_throughput.png"
fi

echo ""
echo "Generating server hits per second graph..."
java -jar $GRAPH_GENERATOR_LOCATION --tool Reporter --generate-png server_hits_per_second.png --input-jtl $LOGFILE --plugin-type HitsPerSecond --relative-times no --width 1280 --height 1024 > /dev/null 2>&1
if [ ! -f server_hits_per_second.png ]; then
    echo "ERROR: Could not generate hits per second graph file server_hits_per_second.png"
    exit 2
else
    echo "Generated hits per second graph file server_hits_per_second.png"
fi

echo ""
echo "Generating response time percentiles graph..."
java -jar $GRAPH_GENERATOR_LOCATION --tool Reporter --generate-png response_time_percentiles.png --input-jtl $LOGFILE --plugin-type ResponseTimesPercentiles --width 1280 --height 1024 > /dev/null 2>&1
if [ ! -f response_time_percentiles.png ]; then
    echo "ERROR: Could not generate response time percentiles graph file response_time_percentiles.png"
    exit 2
else
    echo "Generated response time percentiles graph file response_time_percentiles.png"
fi

echo ""
echo "Generating response times vs threads graph..."
java -jar $GRAPH_GENERATOR_LOCATION --tool Reporter --generate-png response_times_vs_threads.png --input-jtl $LOGFILE --plugin-type TimesVsThreads --width 1280 --height 1024 > /dev/null 2>&1
if [ ! -f response_times_vs_threads.png ]; then
    echo "ERROR: Could not generate response times vs threads graph file response_times_vs_threads.png"
    exit 2
else
    echo "Generated response times vs threads graph file response_times_vs_threads.png"
fi

echo ""
echo "Generating throughput vs threads graph..."
java -jar $GRAPH_GENERATOR_LOCATION --tool Reporter --generate-png throughput_vs_threads.png --input-jtl $LOGFILE --plugin-type ThroughputVsThreads --width 1280 --height 1024 > /dev/null 2>&1
if [ ! -f throughput_vs_threads.png ]; then
    echo "ERROR: Could not generate throughput vs threads graph file throughput_vs_threads.png"
    exit 2
else
    echo "Generated throughput vs threads graph file throughput_vs_threads.png"
fi

### generate the aggregate report ###
echo ""
echo "Generating aggregate report for test run data..."
java -jar $GRAPH_GENERATOR_LOCATION --tool Reporter --generate-csv aggregate_report.csv --input-jtl $LOGFILE --plugin-type AggregateReport > /dev/null 2>&1
if [ ! -f aggregate_report.csv ]; then
    echo "ERROR: Could not generate aggregate report file aggregate_report.csv"
    exit 2
else
    echo "Generate aggregate report file aggregate_report.csv"
fi
