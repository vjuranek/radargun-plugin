#!/bin/bash 

set -x

# Script for launching RG master
# See org.jenkinsci.plugins.radargun.script.ScriptSource#getMasterCmdLine for description of parameters
# begin passed to this script and their order

$2 -c $3 -s $4
