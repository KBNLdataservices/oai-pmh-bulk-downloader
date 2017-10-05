#!/usr/bin/env bash

version=`xsltproc get-ver.xsl pom.xml`

mvn clean package

cp target/oai-pmh-bulk-downloader-$version.jar ./dist/

cp target/oai-pmh-bulk-downloader-$version.jar ./dist/oai-pmh-bulk-downloader-latest.jar

git add .
git commit -am "Distributes version $version."

git tag v$version

git push origin --tags
git push origin master