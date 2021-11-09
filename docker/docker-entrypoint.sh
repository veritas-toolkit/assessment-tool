#! /bin/bash
set -x
cd $(dirname $0)/.. || exit

if [ "$(ls config/)" == "" ]; then
    chown -R veritas:veritas /opt/veritas
fi

exec gosu veritas:veritas bash -c "bin/docker-run.sh"