#!/bin/bash
# -Djavax.net.ssl.trustStore

# autorizar o java a abrir a porta nro baixo
# setcap CAP_NET_BIND_SERVICE=+eip $(readlink -f $(which java))
# /usr/lib/jvm/java-11-openjdk-armhf/bin/java


ret=?
cmd=?
test -z $dirLog && dirLog=$raiz/logs
! test -d $dirLog && mkdir -p $dirLog

deb=$3
maq=$2
if [ ".$2." == ".." ] ; then
 maq=sun
fi

echo "maq=$maq"
#read

#sun="$sun -verbosegc"
#sun="/usr/local/jdk1.6.0_04/jre/bin/java  -Xmx512m -verbosegc"
gcj="$raiz/bin/guarani.bin"
cd $raiz/bin

#######################################################
status() {
	pid
	echo "processos: $PID"
}
#######################################################
PID=
pid() {
	#xx="`ps ax | grep "${conf}" | grep -v 'grep '`"
	#echo "xx=$xx="
	PID="`ps ax | grep "${conf}"|grep "$sun"|grep -v grep | awk '{print $1}'`"
	#PID="$PID `ps ax | grep "${conf}"|grep $gcj|grep -v grep | awk '{print $1}'`"
}
#########################
log() {
	echo "`date "+%Y-%m-%d %H:%M"` $1" >>$dirLog/guarani.txt
	#if [ "$2" == "" ]; then
		echo "`date "+%Y-%m-%d %H:%M"` $1"
	#fi
}
#########################
startupsun() {
	#LC_ALL=pt_BR
	#export LC_ALL
	
	if ! test -e "$sun" && ! which $sun; then
		echo "não achei maq java...sun=$sun"
		exit 1
	else
		echo "achei maq java $sun"
	fi

	if [ ".$CLASSPATH" == "." ]; then
		export CLASSPATH=$sunClass
	fi
	#$raiz/lib/mm.mysql-2.0.14-bin.jar:

	cmd="$sun $sunOp -cp $CLASSPATH br.org.guarani.loader.load $conf $deb"
	ret=$?
	echo "result=$ret"
}
#########################
startupgcj() {
	LC_ALL=pt_BR
	export LC_ALL

	export CLASSPATH=:$raiz/bin/pacotes/mail.jar:$raiz/bin/pacotes/mysql
	echo "cp=$CLASSPATH"
	#ead
	#/mnt/sda5/etch32/prg/ssl
	#/tmp/ssl
	#$raiz/lib/mm.mysql-2.0.14-bin.jar
	
	cd $raiz/bin
	cmd="$gcj $conf $deb"
	ret=$?
	echo "result=$ret"
}
########################
stop() {
	echo "===> parando GUARANI... "
	# aborta script se não responde ? desde quando ? por causa 
	#  que havia um "set -e" no script
	wget -t 1 -T 10 --proxy=off -O /dev/null "http://localhost:$porta/adm.class?op=stop&_GS_=aa"
	echo "===> a parando GUARANI..."
}
#######################
case "$1" in
	status)
		status
	;;
	starts)
		startup$maq
		while true; do
			log "START modo seguro: $EU $cmd"
			$cmd
			ret=$?
			log "SAIU modo seguro: ret=$ret $EU $cmd"
			dr=$raiz/classes/sun/servidor
			if test -d $dr; then
				# se dev e classe do servidor foi compilada ultimo minuto
				ret=0
				echo "===>> $dr"
				#read
				if [ "$(find $dr/ -mmin -1)" != "" ]; then
					ret=77
				fi
			fi
			case $ret in
				0)
					break;
				;;
				77)
					echo "#DEV, servidor compilado, reinicia"
				;;
				78)
					echo "#PRODUÇÃO, atualização, reinicia"
				;;
				143)
					log "KILL no modo seguro: ret=$ret $EU $cmd"
				;;
					*)
				;;
			esac
		done
		log "FIM modo seguro: ret=$ret $EU $cmd"
	;;
	start)
		pid
		if [ "$PID" != "" ] ; then
			echo "Já RODANDO '$PID'"
			exit 0
		fi
		echo "Iniciando Guarani!!"
		if [ $seg -eq 1 ]; then
			log "INI modo seguro: $EU $maq"
			$EU starts $maq &
		else
			startup$maq
			log "INI modo normal: $EU $cmd"
			#ead
			$cmd &
		fi
	;;
	stop)
		log "STOP script: $EU"
		stop
	;;
	restart)
		stop
		echo "parando ..."
		sleep 2

		echo "iniciando ..."
		startup$maq
		$cmd
	;;
	*)
		echo "**************************************************************"
		echo "Uso: /etc/init.d/guarani.sh {start|stop|restart} [maq] [debug]"
		echo "Onde: "
		echo " [maq] máquina java [jvm] {sun|gcj|gun}"
		echo " [debug] mostrar na tela logs . significa todos ou voce pode"
		echo "  lista-los separados por ,"
		echo "**************************************************************"
		exit 1
	;;
esac

exit 0



#The only trick I had to do was to set the GCJ_PROPERTIES environment to
#"gnu.gcj.runtime.VMClassLoader.library_control=never".  This prevents
#gij from loading .so files that were built with previous versions of
#gcj.  The binary compatibility ABI should fix this in the future.

