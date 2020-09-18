#cp -f $2 /tmp/tmp.jpg
/usr/local/bin/djpeg -gif -scale 1/8 -outfile /tmp/tmpt.gif /tmp/tmp.jpg
#cp -f /tmp/tmpt.gif $1
