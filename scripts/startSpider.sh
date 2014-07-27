#!/bin/bash
APP_HOME=/Users/apple/privacy/works/idea/spider/target/spider-1.0-SNAPSHOT

cdto(){
        cd $1;
}

cdto ${APP_HOME}

APP_CONF="${SNAPSHOT_HOME}/conf"
CLASSPATH="${APP_CONF}"
for i in "${APP_HOME}"/lib/*.jar
do
        CLASSPATH="${CLASSPATH}:$i"
done
export CLASSPATH

java com.app.lgr.spider.SpiderMain