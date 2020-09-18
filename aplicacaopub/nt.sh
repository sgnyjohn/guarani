nome=$1
dest=$2
ip=$3

#descobrindo o ip
#nmblookup -d 1 -U 172.27.79.11 -R MIC9689

echo "desmontando $dest"
smbumount $dest

u=$USER
USER="signey john"

#o ip para work colocar pdc para server o proprio

x="smbmount $nome"
x="$x $dest -o "
x="$x codepage=cp850,iocharset=iso8859-1"
x="$x,ip=$ip,workgroup=WIN_PALACIO"
#x="$x,username=signey john,password=sdfsdfz"

echo $x $USER
$x
USER=$u

echo "DIR $dest..."
ls $dest
