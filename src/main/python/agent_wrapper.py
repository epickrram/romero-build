#!/usr/bin/python

import socket
import sys
import time
import properties

HOST_PROPERTY_KEY = 'romero.agent.status.server.port'

def get_agent_server_port(property_file):
    property_reader = properties.PropertiesReader()
    if property_reader.load(property_file):
        return property_reader.get_int_property(HOST_PROPERTY_KEY)
    else:
        print("Failed to load properties")
        return None

def get_socket_response(port):
    try:
        conn = socket.create_connection(['localhost', port])
        data = conn.recv(128)
        conn.close()
        return data
    except socket.error as error:
        print("Failed to connect: " + str(error))
        return ""

if __name__ == "__main__":
    if len(sys.argv) < 1:
        print("Please supply properties filename")
        sys.exit(1)
    port = get_agent_server_port(sys.argv[1])
    if port is not None:
        for i in range(10):
            response = get_socket_response(port)
            print(response)
            time.sleep(5)
    else:
        print("Unable to determine port from properties file: " + str(sys.argv[1:]))
