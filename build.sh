#!/bin/bash
mkdir -p build && javac -cp src -d build $(find src -type f -iname "*.java")
