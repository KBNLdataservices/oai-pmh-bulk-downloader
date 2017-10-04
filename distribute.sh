#!/usr/bin/env bash

version=`xsltproc get-ver.xsl pom.xml`

mvn clean package

cp target/oai-pmh-bulk-downloader-$version.jar ./dist/

git add .
git commit -m "Distributes version $version."

git tag v$version

git push origin master --follow-tags