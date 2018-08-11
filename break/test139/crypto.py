#!/usr/bin/env python2
import sys

from base64 import b64decode
import Crypto
from Crypto.Cipher import AES

# Padding for the input string --not
# related to encryption itself.

def decrypt(cipherBytes, key, iv):
    cipherText = cipherBytes.decode("utf-8")
    cipher = b64decode(cipherText)
    aes = AES.new(key, AES.MODE_CBC, iv)
    decd = aes.decrypt(cipher)
    plain = decd.decode("utf-8")
    return plain.split('}', 1)[0]+'}'
