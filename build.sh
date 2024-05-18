#!/bin/bash
mkdir -p build && javac -cp src -d build $(find src -type f -iname "*.java")

#!/bin/bash

# Write a script to build your backend in this file, install any necessary dependencies
# (As required by your chosen backend language)
