#!/bin/bash

# Jenkins RadarGun plugin
# Script for launching RG master

set -x
ssh $1 $2 $3 $4 -i $5 -J "$6"
