#!/bin/bash -e
export SCRIPT_HOME=$( cd "$( dirname "$0" )" &> /dev/null && pwd )
cd $SCRIPT_HOME
if [ ! -f sireum ]; then
  curl -Lo sireum http://files.sireum.org/sireum
  chmod +x sireum
fi
if [ ! -f mill-standalone ]; then
  curl -Lo mill-standalone http://files.sireum.org/mill-standalone
  chmod +x mill-standalone
fi
if [ ! -f versions.properties ]; then
  curl -Lo versions.properties https://raw.githubusercontent.com/sireum/kekinian/master/versions.properties
fi
