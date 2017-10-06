# oai-pmh-bulk-downloader

Quite a number of archives, libraries and similar cultural heritage 
institutions expose their  digital collections by means of an 
[OAI/PMH](http://www.openarchives.org/OAI/openarchivesprotocol.html) server. 

Some only host metadata content, but quite a few also expose digital content via 
URLs contained within the metadata block contained in the OAI/PMH envelope.

This tool aims at downloading these 'web resources' in bulk, in parallel.   
 

## General

The oai-pmh-bulk-downloader is a self contained Java based webserver, built with 
[Dropwizard](http://www.dropwizard.io), which can be run locally, preferrably
on a Linux distribution, because of the amount of I/O operations on the file system
required and assumptions about moving files around.

It ships a very basic dashboard by means of which OAI/PMH endpoints can be configured
in combination with XSLT stylesheets to instruct the downloader about what to do
with the XML exposed by the OAI/PMH endpoints.

Use of the dashboard is not a requirement, however, because all its operations are
exposed via RESTful endpoints, allowing integration with (for instance) bash scripts
using curl commands.


## Quick start with the sample installer

The easiest way to get a feel for the application is using the following commands
to install the sample server.

### Prerequisites

For the sample installer to work the following two depencies must be met:
- A version Java 8 needs to be [installed and configured](http://tipsonubuntu.com/2016/07/31/install-oracle-java-8-9-ubuntu-16-04-linux-mint-18/)
- The ```curl``` tool must be present: ```sudo apt-get install curl``` 

### The install command

```bash
  mkdir oai-pmh-bulk-downloader
  cd oai-pmh-bulk-downloader
  wget --no-cache https://raw.githubusercontent.com/KBNLresearch/oai-pmh-bulk-downloader/master/sample/install-sample.sh -O - | sh
```

Further instructions on the sample installer can be found [here](https://github.com/KBNLresearch/oai-pmh-bulk-downloader/tree/master/sample)


## Setting up for productive use

It is greatly advisable to follow the complete tutoral under 
[Quick start](https://github.com/KBNLresearch/oai-pmh-bulk-downloader/tree/master/sample)
before attempting to set up the oai-pmh-bulk-downloader for productive use, unless
you are sure you know what you are doing.

## REST API 

## Contributing

This tool does a lot of stuff and if you followed the tutorial you can see that it is quite
laborious to set up. However, it is intended for reuse and I have made every effort to
make it suitable for that purpose.

You can contribute via forking and issuing pull requests, but make sure the regressions succeed
by running ```mvn clean test```

### Wishlist

* Ways to integrate this tool neatly in other workflows (as per issue #3)
* Testing support for as many OAI/PMH servers as possible (implementations of OAI/PMH servers vary)
  - any added sample stylesheets and configurations are welcome!
* Review of record version management:
  - currently each update is treated as a new record and deletions are ignored
  - maybe instructions to a receiving endpoint (see #3) in a workflow can be added to
  delegate handling of OAI/PMH statuses (updated/deleted)
