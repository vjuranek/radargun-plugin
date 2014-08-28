#!/bin/bash 

# Jenkins RadarGun plugin
# Script for launching RG master
# See org.jenkinsci.plugins.radargun.script.ScriptSource#getMasterCmdLine for description of parameters
# begin passed to this script and their order

set -x
ssh $1 $2 $3 -c $4 -s $5
