#!/bin/bash

# Set variables for directories
SOURCE_DIR="src"
BUILD_DIR="build"

# Check if the build directory exists, if not, create it
if [ ! -d "$BUILD_DIR" ]; then
    mkdir "$BUILD_DIR"
fi

# Compile Java source files and place them in the build directory
javac -d "$BUILD_DIR" $(find "$SOURCE_DIR" -type f -name "*.java")

# Check if the compilation was successful
if [ $? -eq 0 ]; then
    echo "Java source files compiled successfully."
else
    echo "Error: Failed to compile Java source files."
    exit 1
fi
