#!/bin/sh
#
# Gradle wrapper script. Run 'gradle wrapper' to generate gradle-wrapper.jar
# for offline use. Otherwise ensure 'gradle' is on PATH.
#
APP_HOME=$( cd "$( dirname "$0" )" && pwd -P )
cd "$APP_HOME"
if [ -f "gradle/wrapper/gradle-wrapper.jar" ]; then
    exec java -jar gradle/wrapper/gradle-wrapper.jar "$@"
else
    exec gradle "$@"
fi
