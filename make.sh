#!/bin/bash

on() {
	echo "=======================
	$1"
}

dr="loader servidor aplicacao aplicacaopub"
cp=.
for i in $dr; do
	cd $i
	on "COMPILAR $i"
	! test -d ../class/$i && mkdir -p ../class/$i
	javac -d ../class/$i -cp $cp $(find ./ -name "*.java")
	cp="$cp:../class/$i"
	if [ $? -ne 0 ]; then
		on "ERROR"
		exit
	fi
	cd ..
done
