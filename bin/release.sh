#!/usr/bin/env sh

cd $(dirname $0)/../target || exit

mkdir -p file/db
mkdir -p log
mkdir -p log/backup

tar -czvf veritas-assessment-tool.tar.gz \
    jar/veritas-assessment-tool.jar \
    config/ \
    log/ \
    file/db/ \
    bin/ \
    py/*
zip -r veritas-assessment-tool.zip \
    jar/veritas-assessment-tool.jar \
    config/ \
    log/ \
    file/db/ \
    bin/ \
    py/*
