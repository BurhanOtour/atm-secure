#!/usr/bin/env python3
import requests as req
import json 


def main():
    command = {"type": "learned","variable": "account","secret": "accountname"}
    payload = {'REQUEST':json.dumps(command)}
    headers = {'Content-Type' : 'application/x-www-form-urlencoded','Accept':'*/*'}
    r = req.post("http://127.0.0.1:80/sample/index.php", data = payload, headers = headers)
    response = json.loads(r.text)
    print(response['type'])


if __name__ == "__main__":
    main()