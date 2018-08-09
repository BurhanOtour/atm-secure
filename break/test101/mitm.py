#!/usr/bin/env python2
import traceback
import random
import socket
import argparse
import threading
import signal
import sys
import time
import copy
import http.client
import json

from contextlib import contextmanager

running = True
verbose = True

CLIENT2SERVER = 1
SERVER2CLIENT = 2
REQUESTCOUNTER = 0
RECREQ = False
REQUESTPKT = None
REMOTEHOST = None
REMOTEPORT = None
COMMANDSERVER = None
COMMANDPORT = None

def mitm(buff, direction):
  hb = buff
  if direction == CLIENT2SERVER:
    global REQUESTCOUNTER
    REQUESTCOUNTER+=1
    pass
  elif direction == SERVER2CLIENT:
    if REQUESTCOUNTER==3:
      return None
    pass
  return hb

@contextmanager
def ignored(*exceptions):
  try:
    yield
  except exceptions:
    pass

def killpn( a, b, n):
  if n != CLIENT2SERVER:
    killp( a, b)

def killp(a, b):
  with ignored(Exception):
    a.shutdown(socket.SHUT_RDWR)
    a.close()
    b.shutdown(socket.SHUT_RDWR)
    b.close()
  return


#Sever Forwarded
def serverworker(client, server, n):
  while running == True:
    b = ""
    with ignored(Exception):
      b = client.recv(1024)
    if len(b) == 0:
      killpn(client,server,n)
      return
    try:
      b = mitm(b,n)
    except:
      pass
    try:
      if b != None:
        print(b)
        server.send(b)
        global REQUESTPKT
        REQUESTPKT = copy.deepcopy(b)
        print("Req:::")
        print(REQUESTPKT)
    except:
      killpn(client,server,n)
      return
  killp(client,server)
  return


def worker(client, server, n):
  while running == True:
    b = ""
    with ignored(Exception):
      b = client.recv(1024)
    if len(b) == 0:
      killpn(client,server,n)
      return
    try:
      b = mitm(b,n)
    except:
      pass
    try:
      if b != None:
        print ("Response:")
        print (b)
        server.send(b)
        if REQUESTCOUNTER == 2:
          global RECREQ
          RECREQ = True
          sendRequest(REQUESTPKT)
    except:
      killpn(client,server,n)
      return
  killp(client,server)
  return

def signalhandler(sn, sf):
  running = False

def sendRequest(msg):
  try:
    port = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    port.connect((REMOTEHOST, REMOTEPORT))
    port.send(msg)
    port.close()
    sendPostCloseRequest()
  except Exception as e:
    print(e)

def sendPostCloseRequest():
  connection = http.client.HTTPSConnection(COMMANDSERVER,COMMANDPORT,timeout=10)
  payload = '{\"type\": \"done\"}'
  headers = {'Content-type': 'application/json'}
  json_data = json.dumps(payload)
  connection.request('POST', '/post', json_data, headers)
  response = conn.getresponse()

def doProxyMain(port, remotehost, remoteport):
  signal.signal(signal.SIGTERM, signalhandler)
  try:
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    s.bind(("0.0.0.0", port))
    s.listen(1)
    workers = []
    while running == True:
      k,a = s.accept()
      v = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
      v.connect((remotehost, remoteport))
      t1 = threading.Thread(target=worker, args=(k,v,CLIENT2SERVER))
      t2 = threading.Thread(target=serverworker, args=(v,k,SERVER2CLIENT))
      t2.start()
      t1.start()
      workers.append((t1,t2,k,v))
  except KeyboardInterrupt:
    signalhandler(None, None)
  for t1,t2,k,v in workers:
    killp(k,v)
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

  REMOTEHOST = args.s
  REMOTEPORT = args.q
  COMMANDSERVER = args.c
  COMMANDPORT = args.d

  print('started\n')
  sys.stdout.flush()
  doProxyMain(args.p, args.s, args.q)
