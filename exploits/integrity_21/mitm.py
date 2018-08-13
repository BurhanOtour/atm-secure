#!/usr/bin/env python3
import traceback
import random
import socket
import argparse
import threading
import signal
import sys
import time
from contextlib import contextmanager
import datetime
import json

running = True

class MITM:

    def __init__(self):
        signal.signal(signal.SIGTERM, self.signalhandler)

    def signalhandler(sn, sf):
        global running
        running = False
        sys.exit()

    def start_operation(self, port, remotehost, remoteport):

        # Create server using TCP/IP socket
        self._server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        # self._server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        self._server.bind((remotehost, port))
        self._server.listen(3)
        self.targetport = remoteport
        self.remotehost = remotehost
        messagesReceived = []

        while running == True:
            k,a = self._server.accept()
            fragments = []
            chunk = k.recv(10000)
            fragments.append(chunk)

            message = b"".join(fragments)

            messagesReceived.append(message)

            if len(messagesReceived) == 3:
                v = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                v.connect((self.remotehost, self.targetport))

                v.sendall(message)

                fragments = []

                chunk = v.recv(10000)
                fragments.append(chunk)
                tempmessage = b"".join(fragments)

            v = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            v.connect((self.remotehost, self.targetport))

            v.sendall(message)

            fragments = []

            chunk = v.recv(10000)
            fragments.append(chunk)

            message = b"".join(fragments)
            
            k.sendall(message)
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
    MITM = MITM()
    MITM.start_operation(args.p, args.s, args.q)
