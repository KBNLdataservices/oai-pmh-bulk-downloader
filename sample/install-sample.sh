#!/usr/bin/env bash

wget https://github.com/KBNLresearch/oai-pmh-bulk-downloader/raw/master/dist/oai-pmh-bulk-downloader-latest.jar
wget https://github.com/KBNLresearch/oai-pmh-bulk-downloader/raw/master/sample/example.yaml
wget https://github.com/KBNLresearch/oai-pmh-bulk-downloader/raw/master/sample/anp.xsl

mkdir -p output/in
mkdir output/rejected
mkdir output/processing

java -jar oai-pmh-bulk-downloader-latest.jar server example.yaml

curl -XPOST http://localhost:18081/tasks/create-database-schema
curl -XPOST -F 'file=@anp.xsl' http://localhost:18080/stylesheets
curl -XPOST -H 'Accept: application/json' -H 'Content-type: application/json' -d@anp.json  http://localhost:18080/repositories/
