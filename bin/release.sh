#!/usr/bin/env sh

cd $(dirname $0)/../target || exit

tar -czvf veritas-assessment-tool.tar.gz \
    jar/veritas-assessment-tool.jar \
    config/ \
    file/db/directory_for_database_file \
    bin/ \
    py/*
