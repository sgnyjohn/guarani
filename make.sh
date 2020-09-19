#!/bin/bash

on() {
	echo "=======================
	$1"
}

! test -d ./class && mkdir class

dr="loader servidor aplicacao aplicacaopub"

for i in $dr; do
	cd $i
	on "COMPILAR $i"
	javac -d ../class/ -cp ../class/ $(find ./ -name "*.java")
	if [ $? -ne 0 ]; then
		on "ERROR"
		exit
	fi
	cd ..
done
