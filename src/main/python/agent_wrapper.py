#!/usr/bin/python

import socket

HOST = 'localhost'
PORT = 57000

def get_socket_repsonse():
    conn = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.connect(HOST, PORT)
    data = s.receive(128)
    s.close()
    return data


