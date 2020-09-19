#!/bin/bash

on() {
	echo "=======================
	$1"
}

dr="loader servidor aplicacao"
cp=.
for i in $dr; do
	cd $i
	on "COMPILAR $i"
	dd=../classes/sun/$i
	! test -d $dd && mkdir -p $dd
	javac -d $dd -cp $cp $(find ./ -name "*.java")
	cp="$cp:$dd"
	if [ $? -ne 0 ]; then
		on "ERROR"
		exit
	fi
	cd ..
done
