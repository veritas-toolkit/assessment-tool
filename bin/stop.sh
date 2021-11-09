#!/usr/bin/env sh

cd $(dirname $0)/.. || exit

get_pid() {
  PID=$(pgrep -f "${PWD}/jar/veritas-assessment-tool.jar")
  if [ -z "$PID" ]; then
    return 1
  else
    echo "$PID"
    return 0
  fi
}

stop_process() {
  if get_pid; then
    echo "pid: $PID"
    kill -15 "$PID"
    while get_pid; do
      sleep 0.5
    done
  fi
}
get_pid
stop_process
