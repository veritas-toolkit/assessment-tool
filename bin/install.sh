#! /bin/bash

cd $(dirname $0)/.. || exit
cd py || exit

pip3 install virtualenv

if [[ -d venv ]]; then
  rm -rf venv
fi
# create virtual environment
virtualenv venv -p python3
# using virtual environment
source venv/bin/activate
# install python module
pip3 install --no-cache-dir -r requirements.txt