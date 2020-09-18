#include <arpa/telnet.h>
#include <stdio.h>
#include <errno.h>
#include <string.h>
#include <signal.h>
#include <unistd.h>
#include <fcntl.h>
#include <netdb.h>
#include <netinet/in.h>
#include <sys/socket.h>

#define DEBUG 0
#define TRACKING 0

#define CONN_OK_W_DATA 2
#define CONN_OK        1
#define CONN_FAILED    0

#define PROMPT   "tn-gw->"
#define CONN_PROMPT_OK "Connected to"
#define CONN_CLOSED "Remote server has closed connection"

#define PACKET_SIZE  1024
#define MAX(a,b) (a<b?b:a)
#define FSET(a,b,c)  (FD_ISSET(a,c)?a:b)
#define NFSET(a,b,c) (FD_ISSET(a,c)?b:a)


int writen(int fd, char *ptr, int n);
int readn(int fd, char *ptr, int n);
int forward(int fin, int fout);
int  telnet_init(void);

unsigned short server_port = 12346;

char *proxy_name = "proxy";
unsigned short proxy_port = 23;

char *target_host = "target";
unsigned short target_port = 80;
struct sockaddr_in serverSock, clientSock, proxySock;
int fserver, ftarget, fcurrent, fproxy;

void usage ( void ) {

  printf ( "Usage: telnet_proxy [OPTION]\n"
           "open a tunnel via a proxy server\n\n"
           "  -?     \t this help\n"
           "  -l port\t local port to listen ( default 12345 )\n"
           "  -p host\t proxy hostname ( default proxy )\n"
           "  -P port\t proxy port ( default 23 )\n"
           "  -t host\t target hostname ( default target )\n"
           "  -T port\t target port ( default 80 )\n");
}

int main(int argc , char *argv[])
{
  int ret, len, c;
  struct hostent *proxyHostEnt;        /* description du host serveur */
  unsigned long hostAddr;                       /* addr du serveur */
  fd_set fdsr;
  long flags;


  while ( ( c = getopt(argc,argv,"?l:p:P:t:T:") ) != -1 ) {
    switch ( c ) {
      case 'l' : server_port = atoi(optarg);   break;
      case 'P' : proxy_port  = atoi(optarg);   break;
      case 'T' : target_port = atoi(optarg);   break;
      case 'p' : proxy_name  = strdup(optarg); break;
      case 't' : target_host = strdup(optarg); break;
      case '?' :
      default  :
        usage();
        return 0;
    }
  }

  printf("listen port:%d, proxy: %s:%d, target: %s:%d\n",server_port,
         proxy_name,proxy_port,target_host,target_port);


  fserver = socket(AF_INET, SOCK_STREAM, 0);
  if (fserver < 0) {
    perror("socket");
    return -1;
  } else if ( DEBUG ) {
    printf("server : %d\n",fserver);
  }
  serverSock.sin_family = AF_INET;
  serverSock.sin_addr.s_addr = INADDR_ANY;
  serverSock.sin_port = htons(server_port);
  if (bind(fserver, (struct sockaddr *) &serverSock, sizeof(struct sockaddr_in))
      < 0) {
    perror("bind");
    return -1;
  }
  if (listen(fserver, 0) < 0) {
    perror("listen");
    return -1;
  }

  for (;;) {
    FD_ZERO(&fdsr);
    FD_SET(fserver, &fdsr);

    ret = select(fserver + 1, &fdsr, 0, 0, 0);
    if (ret <= 0) {
      close(fserver);
    }
    fcurrent = accept(fserver, (struct sockaddr *) &clientSock, &len);
    fcntl(fcurrent, F_GETFL, &flags);
    flags |= O_NONBLOCK;
    fcntl(fcurrent, F_SETFL, &flags);

    if (DEBUG) {
      printf("current : %d\n", fcurrent);
    }

    bzero(&proxySock, sizeof(proxySock));
    hostAddr = inet_addr(proxy_name);
    if ((long) hostAddr != (long) -1)
      bcopy(&hostAddr, &proxySock.sin_addr, sizeof(hostAddr));
    else {      /* si on a donne un nom  */

      proxyHostEnt = gethostbyname(proxy_name);
      if (proxyHostEnt == NULL) {
        printf("ca chie gethost\n");
        exit(0);
      }
      bcopy(proxyHostEnt->h_addr, &proxySock.sin_addr,proxyHostEnt->h_length);
    }
    proxySock.sin_port = htons(proxy_port); /* host to network port */
    proxySock.sin_family = AF_INET; /* AF_*** : INET=internet */
    if ((fproxy = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
      printf("ca chie creation socket client\n");
      exit(0);
    }
    if (connect (fproxy, (struct sockaddr *) &proxySock,sizeof(proxySock))<0) {
      printf("ca chie demande de connection\n");
      exit(0);
    }
    if (DEBUG) {
      printf("proxy : %d\n", fproxy);
    }
    if ( telnet_init() == CONN_OK ) {
      fcntl(fproxy, F_GETFL, &flags);
      flags |= O_NONBLOCK;
      fcntl(fproxy, F_SETFL, &flags);

      do {
        FD_ZERO(&fdsr);
        FD_SET(fcurrent, &fdsr);
        FD_SET(fproxy, &fdsr);
        if (DEBUG) printf("now wait ... \n");
        ret = select(MAX(fcurrent, fproxy) + 1, &fdsr, 0, 0, 0);
        if (ret <= 0) {
          if (DEBUG) printf("on ferme\n");
          close(fproxy);
          close(fcurrent);
          break;
        }
        len = 0;
        if ( FD_ISSET(fcurrent,&fdsr) )
          len = forward(fcurrent, fproxy);
        if ( FD_ISSET(fproxy,&fdsr) )
          len = forward(fproxy, fcurrent);
      } while (len);
      if (DEBUG) printf("on ferme\n");
      close(fcurrent);
      close(fproxy);
    } else {
      if (DEBUG) printf("on ferme\n");
      close(fcurrent);
      close(fproxy);
    }
  }

  return 0;
}


int get_prompt(int fd)
{
  int nl, nr, n=PACKET_SIZE;
  char deb_ptr[PACKET_SIZE];
  char *ptr = deb_ptr;

  nl = n;
  while (nl > 0) {
    nr = read(fd, ptr, nl);
    if (nr < 0) return nr;    /* error */
    else if (nr == 0) break;
    nl -= nr;
    ptr += nr;
    if (strstr(ptr-10,PROMPT)) return CONN_OK;
  }
  return CONN_FAILED;
}

int get_connected(int fd, char *deb_ptr, int n)
{
  int nl, nr;
  char *ptr = deb_ptr, *ptr2;
  char conn_prompt_ok_string[PACKET_SIZE];

  sprintf(conn_prompt_ok_string,"%s %s.\r\n",CONN_PROMPT_OK, target_host);

  nl = n;
  while (nl > 0) {
    nr = read(fd, ptr, nl);
    ptr[nr] = 0x00;
    if ( DEBUG ) printf("->%s<-\n", ptr);
    if (nr < 0) return nr;    /* error */
    else if (nr == 0) break;
    nl -= nr;
    ptr += nr;
    if ( ptr2 = strstr(deb_ptr,conn_prompt_ok_string) ) {
      ptr2 += strlen(conn_prompt_ok_string);
      if ( DEBUG ) printf("->%s<-",ptr2);
      writen(fcurrent, ptr2, strlen(ptr2));
      return CONN_OK;
    }
    if ( strstr(ptr-10,PROMPT) ) return CONN_FAILED;
  }
  return CONN_FAILED;
}


int  telnet_init(void)
{
  char option[] = { IAC, WILL, TELOPT_BINARY };
  int szo = sizeof(option);
  unsigned char buf[PACKET_SIZE + 1];
  int n , ret;

  /* force binary on the socket */
  if (DEBUG) printf("sending options ...");
  writen(fproxy, option, szo);
  option[1] = DO;
  writen(fproxy, option, szo);

  if (DEBUG) printf("reading header ...");
  n = get_prompt(fproxy);
  if (DEBUG) printf("connected proxy !!\n");

  sprintf(buf,"connect %s %d\n", target_host, target_port);
  writen(fproxy, buf, strlen(buf));
  ret = get_connected(fproxy, buf, PACKET_SIZE);
  if (DEBUG) printf("connected target ? %s\n",(ret==CONN_OK?"oui":"non"));

  return ret;
}


int writen(int fd, char *ptr, int n)
{

int nl, nw;
        nl = n;
        while ( nl > 0 ) {
                nw = write(fd, ptr, nl);
                if ( nw <= 0 )
                        return nw;     /*error*/
                nl -= nw;
                ptr += nw;
        }
        return (n-nl);
}


int readn(int fd, char *ptr, int n){

int nl, nr;
        nl = n;
        while ( nl > 0 ) {
                nr = read(fd,ptr,nl);
                if (nr < 0 ) {
                  if ( nr == -1 )  {
                    *ptr = 0x00;
                    return (n-nl);
                  }          
                        return nr;     /*error*/
                } else {
                        if ( nr == 0 )
                                break;
                }
                nl -= nr;
                ptr += nr;
        }
        *ptr = 0x00;
        return (n-nl);
}


int forward(int fin, int fout)
{
  int total=0, len=0, i = 0;
  char buffer[PACKET_SIZE + 1];
  char *ptr;

  if (DEBUG) printf ( "^[[31m%d -> %d\n^[[30m",fin,fout);

  do {
    len = readn(fin, buffer, PACKET_SIZE);
    buffer[len] = 0x00;
    if ( ptr = strstr(buffer, CONN_CLOSED) ) {
      if ( DEBUG ) printf("----> '%s' found\n",CONN_CLOSED);
      *ptr=0x00;
    }
    if ( ( len = strlen(buffer) ) != 0 )  {
      if (TRACKING) printf("%d-%d->%s<--\n",i++,len,buffer);
      writen(fout, buffer, len);
      total += len;
    }
  } while (len == PACKET_SIZE);
  return total;
}

