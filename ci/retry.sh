#!/bin/sh

MAX_RETRIES=3
RETRY_WAIT_DURATION=15
IGNORE=true

COMMAND=""
while [ $# -gt 0 ]; do
	key="$1"
	
	case $key in
	    -m|--max-retries) MAX_RETRIES="$2"; shift; shift ;;
	    -w|--wait-between-retries) RETRY_WAIT_DURATION="$2"; shift; shift ;;
	    --) IGNORE=false ; shift ;;
	    *) [ "$IGNORE" = "false" ] && COMMAND="${COMMAND}$1 " ; shift ;;
	esac
done


n=0
until [ "$n" -gt $MAX_RETRIES ]; do
   sh -c "$COMMAND" && break
   n=$((n+1)) 
   if [ "$n" -le $MAX_RETRIES ]; then
	   echo ""
	   echo "/!\ Command '$COMMAND' has failed."
	   echo "    Retrying ($n/$MAX_RETRIES) in $RETRY_WAIT_DURATION seconds..."
	   echo ""
	   sleep $RETRY_WAIT_DURATION
	else
	   echo ""
	   echo "/!\ Command '$COMMAND' has failed $n times."
	   exit 1;
	fi
done
