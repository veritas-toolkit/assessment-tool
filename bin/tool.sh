#!/bin/bash

POSITIONAL=()


function check_user() {
  local l_user="$1"
  local l_user_count=`sqlite3 $SQLITE_FILE <<EOF
  select count(id) from vat_user where username = "$l_user";
EOF
`
  if [[ $l_user_count -eq 0 ]]; then
    echo "Not found user: $l_user"
    exit 1;
  fi
}

function unlock() {
  if [[ $# -eq 0 ]]; then
    echo "use command: '$0 unlock -u USERNAME'"
    exit 1;
  fi
  local l_user="$1"
  check_user l_user
  sqlite3 $SQLITE_FILE <<EOF
  update vat_user set locked=0 where username = "$l_user";
EOF
  clear_cache
}

function reset_password() {
  if [[ $# -eq 0 ]]; then
    echo "use command: '$0 reset_password -u USERNAME'"
    exit 1;
  fi
  local l_user="$1"
  check_user l_user
  local l_password='$2a$10$XRz2ew7AX.StHRF.XptumuXSSSHiF4ylTCC5uDBO1l2z51maRnE82'
  sqlite3 $SQLITE_FILE <<EOF
  update vat_user set should_change_password=1, password="$l_password" where username = "$l_user";
EOF
  clear_cache
}

function user_list() {
  sqlite3 $SQLITE_FILE <<EOF
  select id, username from vat_user order by id;
EOF
}

function print_help() {
  echo "
  help: print this help info.
  list: list all user.
  unlock: unlock user
          flag: -u USERNAME
  reset_password: reset user's password
          flag: -u USERNAME
  "
}

function clear_cache() {
  touch 'file/db/.clear_cache'
}

cd "$(dirname $0)" || exit
cd .. || exit

SQLITE_FILE='file/db/sqlite.db'

if [[ $# -eq 0 ]]; then
  print_help;
  exit 1;
elif [[ $# -gt 0 ]]; then
  COMMAND="$1"
  shift
fi


while [[ $# -gt 0 ]]; do
  key="$1"

  case $key in
    -u|--user)
      G_USER="$2"
      shift # past argument
      shift # past value
      ;;
    *)    # unknown option
      POSITIONAL+=("$1") # save it in an array for later
      shift # past argument
      ;;
  esac
done

case $COMMAND in
  list)
    user_list
    ;;
  unlock)
    unlock $G_USER
    ;;
  reset_password)
    reset_password $G_USER
    ;;
  help)
    print_help;
    ;;
  *)
    echo "'$COMMAND' is not a legal command.";
    echo "see '$0 help'"
    exit 1
    ;;
esac