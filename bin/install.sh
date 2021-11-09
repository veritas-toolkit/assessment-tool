#! /bin/bash

cd $(dirname $0)/.. || exit
cd py || exit

pip3 install virtualenv

virtualenv venv -p python3

source venv/bin/activate

pip3 install --no-cache-dir -r requirements.txt