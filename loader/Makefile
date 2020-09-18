#
# Makefile for Rimfaxe Webserver
# 
# ver 1.03
#

# compilers

# Edit these 3 lines to match your environment :

#JAVAH_COMPILER = /usr/local/bin/gcjh
#JAVA_COMPILER = /usr/local/bin/gcj
#CPP_COMPILER = /usr/local/bin/gcc

#JAVAH_COMPILER = /usr/local/gcc/4.2.0/bin/gcjh
#JAVA_COMPILER = /usr/local/gcc/4.2.0/bin/gcj
#CPP_COMPILER = /usr/local/gcc/4.2.0/bin/gcc

#JAVAH_COMPILER = /usr/local/gcc/3.3.2/bin/gcjh
#JAVA_COMPILER = /usr/local/gcc/3.3.2/bin/gcj
#CPP_COMPILER = /usr/local/gcc/3.3.2/bin/gcc

#JAVAH_COMPILER = /gcjh
#JAVA_COMPILER = /gcj
#CPP_COMPILER = /gcc


# Do not change anything below.

GCJ_COMPILE_OPTIONS = -O2  -fPIC -Wno-deprecated -fno-bounds-check -fno-store-check
GCC_COMPILE_OPTIONS = -O2  -Wall -fPIC 

load :  load.o cry.o
	echo "build load"
	$(GCJ)  \
	-Dgnu.gcj.runtime.NameFinder.demangle=true \
	-Dgnu.gcj.runtime.NameFinder.use_addr2line=true \
	-Djava.library.path=... \
	-Djava.class.path=... \
	-Djava.home=... \
	-Dgnu.classpath.home.url=... \
	-Dgnu.classpath.home=... \
	-Dgnu.java.security.provider.Gnu=.  \
	load.o cry.o \
	-L. --main=br.org.guarani.loader.load -o load
	ls -lh load
	#strip -s load
	ls -lh load
	#cp rws ../bin
	#cp *.so ../lib
	
#
#
load.o : br/org/guarani/loader/*.java 
	echo "build load.o"
	$(GCJ) $(GCJ_COMPILE_OPTIONS) -fjni \
		--encoding=ISO-8859-1 \
		-classpath crypto/ \
		-c  br/org/guarani/loader/*.java   \
		-o load.o

cry.o : crypto/gnu/crypto/*.java  crypto/gnu/crypto/cipher/*.java  crypto/gnu/crypto/util/*.java 
	echo "build load.o"
	$(GCJ) $(GCJ_COMPILE_OPTIONS) -fjni \
		--encoding=ISO-8859-1 \
		-c  crypto/gnu/crypto/*.java  crypto/gnu/crypto/cipher/*.java  crypto/gnu/crypto/util/*.java  \
		-o cry.o


clean :
	find | grep a~ | xargs rm -f
	rm -f rws
	rm -f *.so
	rm -f *.o


