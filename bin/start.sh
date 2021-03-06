#! /bin/bash

cd $(dirname $0)/.. || exit

if [[ -f py/venv/bin/activate ]]; then
  source py/venv/bin/activate
fi

JVM_OPT='-Dlog4j2.formatMsgNoLookups=true'

nohup java ${JVM_OPT} -jar $(pwd)/jar/veritas-assessment-tool.jar 1>>log/stdout.log 2>&1 &