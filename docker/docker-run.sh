#! /bin/bash

cd $(dirname $0)/.. || exit

if [[ ! -d file/db ]]; then
  mkdir file/db
fi
if [ "$(ls -A config/)" == "" ]; then
    cp .default_config/* config/
fi

if [[ -f py/venv/bin/activate ]]; then
  source py/venv/bin/activate
fi

exec java -jar $(pwd)/jar/veritas-assessment-tool.jar