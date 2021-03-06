#!/usr/bin/env python2
import traceback
import random
import socket
import argparse
import threading
import signal
import sys
from contextlib import contextmanager

running = True
verbose = True  
COUNTER = 0

CLIENT2SERVER = 1
SERVER2CLIENT = 2


def mitm(buff, direction):
    hb = buff
    if direction == CLIENT2SERVER:
        if COUNTER == 3:
            hb = None
        else:
            pass
    elif direction == SERVER2CLIENT:
        if COUNTER == 3:
            hb = None
            #print("buff b: ", hb)
        else:
            pass
    return hb


@contextmanager
def ignored(*exceptions):
    try:
        yield
    except exceptions:
        pass


def killpn(a, b, n):
    if n != CLIENT2SERVER:
        killp(a, b)


def killp(a, b):
    with ignored(Exception):
        a.shutdown(socket.SHUT_RDWR)
        a.close()
        b.shutdown(socket.SHUT_RDWR)
        b.close()
    return


def worker(client, server, n):
    while running:
        b = ""
        with ignored(Exception):
            b = client.recv(1024)
            #print("b before mtim worker :" , b)
        if len(b) == 0:
            killpn(client, server, n)
            return
        try:
            b = mitm(b, n)
            #print("b after mtim worker: " , b)
        except:
            pass
        try:
            if b != None:
                server.send(b)
                #print("b from worker: ", b)
        except server.timeout and socket.error:
            server.close()
            #print("timeout from worker" )
            return
    killp(client, server)
    return


###ServerSide Worker###

def workerserver(client, server, n):
    #client.setdefaulttimeout(5)
    while running == True:
        b = ""
        with ignored(Exception):
            b = client.recv(1024)
            #print("b after mtim server: " , b)
        if len(b) == 0:
            killpn(client, server, n)
            return
        try:
            b = mitm(b, n)
            #print("b after mtim server: " , b)
        except:
            pass
        try:
            if b != None:
                server.send(b)
                #print("b from server: ", b)
        except server.timeout :
            server.close()
            #print("timeout from server" )
            return
        except socket.error:
            server.close()
            return
    killp(client, server)
    return



def signalhandler(sn, sf):
    running = False


def doProxyMain(port, remotehost, remoteport):
    signal.signal(signal.SIGTERM, signalhandler)
    try:
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        s.bind(("0.0.0.0", port))
        s.listen(1)
        workers = []
        while running:
            k, a = s.accept()
            v = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            v.connect((remotehost, remoteport))
            t1 = threading.Thread(target=worker, args=(k, v, CLIENT2SERVER))  
            t2 = threading.Thread(target=workerserver, args=(v, k, SERVER2CLIENT))
            global COUNTER
            COUNTER = COUNTER + 1
            t2.start()
            t1.start()
            workers.append((t1, t2, k, v))
    except KeyboardInterrupt:   # ctrl +c is KeyboardInterrupt
        signalhandler(None, None)
    for t1, t2, k, v in workers:
        killp(k, v)
        t1.join()
        t2.join()
    return


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Proxy')
    parser.add_argument('-p', type=int, default=4000, help="listen port")
    parser.add_argument('-s', type=str, default="127.0.0.1", help="server ip address")
    parser.add_argument('-q', type=int, default=3000, help="server port")
    parser.add_argument('-c', type=str, default="127.0.0.1", help="command server")
    parser.add_argument('-d', type=int, default=5000, help="command port")
    args = parser.parse_args()
    print('started\n')
    sys.stdout.flush()
    doProxyMain(args.p, args.s, args.q)

