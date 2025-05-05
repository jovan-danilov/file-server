#!/bin/bash

exec java \
 -jar app.jar \
 $JAVA_MIN_MEM \
 $JAVA_MAX_MEM \
 -Xlog:gc \
 -Dfile.encoding=UTF-8 \
 -Duser.timezone=UTC \
