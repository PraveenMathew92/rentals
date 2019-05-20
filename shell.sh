#!usr/bin/env sh

$PRG=`./gradlew`
echo $PRG
while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    echo "ls = $ls"
    link=`expr "$ls" : '.*-> \(.*\)$'`
    echo "link = $link"
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=`dirname "$PRG"`"/$link"
    fi
    echo "PRG = $PRG"
done
