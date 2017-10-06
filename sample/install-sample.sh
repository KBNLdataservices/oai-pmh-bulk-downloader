#!/usr/bin/env bash

echo "--- downloading sources ---"
wget --no-cache https://raw.githubusercontent.com/KBNLresearch/oai-pmh-bulk-downloader/master/dist/oai-pmh-bulk-downloader-latest.jar

echo "--- downloading sample application configuration ---"
wget --no-cache https://raw.githubusercontent.com/KBNLresearch/oai-pmh-bulk-downloader/master/sample/example.yaml

echo "--- downloading sample stylesheet ---"
wget --no-cache https://raw.githubusercontent.com/KBNLresearch/oai-pmh-bulk-downloader/master/sample/anp.xsl

echo "--- downloading sample repository configuration ---"
wget --no-cache https://raw.githubusercontent.com/KBNLresearch/oai-pmh-bulk-downloader/master/sample/anp.json


echo "--- creating output directories ---"
mkdir -p output/in
mkdir output/rejected
mkdir output/processing

echo "--- booting up the server in the background ---"
java -jar oai-pmh-bulk-downloader-latest.jar server example.yaml > application.log &
SERVER_PID=$!

echo "--- waiting 10 seconds for server to boot, please wait ---"
sleep 10

echo "--- loading database schema ---"
curl -XPOST http://localhost:18081/tasks/create-database-schema

echo "--- adding anp.xsl stylesheet ---"
curl -XPOST -F 'file=@anp.xsl' http://localhost:18080/stylesheets

echo "--- adding ANP oai/pmh repository configuration ---"
curl -XPOST -H 'Accept: application/json' -H 'Content-type: application/json' -d@anp.json  http://localhost:18080/repositories/

echo "--- Sample application was loaded and configured ---"
echo "Visit http://localhost:18080 in your browser to enter the dashboard."
echo "IMPORTANT: run 'kill $SERVER_PID' to stop the application, that is:"
echo "kill $SERVER_PID"