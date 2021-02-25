#!/bin/sh

if [ -z "`pgrep jackd`" ]; then
    pkill -9 pulseaudio
    echo "Bump to jackd"
    nohup jackd -r -d alsa -r 44100 \
	  > /tmp/jackd_server.log &
elif [ -z "`pgrep pulseaudio`" ]; then
    pkill -9 jackd
    echo "Bump to pulse"
    nohup pulseaudio --start -D \
	  > /tmp/pulseaudio_server.log
else
    echo "No servers running"
fi
