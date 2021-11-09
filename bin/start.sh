#! /bin/bash

cd $(dirname $0)/.. || exit

if [[ -f py/venv/bin/activate ]]; then
  source py/venv/bin/activate
fi

nohup java -jar $(pwd)/jar/veritas-assessment-tool.jar 1>>log/stdout.log 2>&1 &