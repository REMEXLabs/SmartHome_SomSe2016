from datetime import datetime

import pifacedigitalio as p
import requests
from socketIO_client import SocketIO, LoggingNamespace

p.init()

# BASE-URL
BURL = "http://smartcare.mi.hdm-stuttgart.de"
# SOCKET-PORT
PORT = 8080
# REST-URL
URL = "http://smartcare.mi.hdm-stuttgart.de/api/public/patientdevice.json"

# Patient-ID
PATIENT = 1
# Button1 belongs to sonos ("radio")
BUTTON0 = 5
# Button2 belongs to hue ("licht")
BUTTON1 = 3
# Button4 belongs to door
BUTTON4 = 2

devices = ["TV", "Rollershutter", "Philips Hue", "Sonos"]

states = {
    'ON': 1,
    'OFF': 2,
    'STOP': 3,
    'PAUSE': 4,
    'PLAY': 5,
    'OPEN': 6,
    'CLOSED': 7
}


def send_state_change(patient_id, device_id, new_state):
    res = requests.put(URL + "/{}".format(device_id), data={'state': new_state})
    if (res.status_code == 200):
        print("Changed state of device #{} => new state: {}".format(device_id, new_state))  # TODO
    else:
        raise Exception(
            "An error occurred while setting state of device {}. HTTP Status: {}: {}".format(devices[device_id],
                                                                                             res.status_code, res.text))


def get_state(id):
    req = requests.get(URL + "/{}".format(id)).json()
    try:
        return req[0]['state']
    except IndexError:
        raise Exception("Device not found")


def toggle_state(state):
    if (state in ["ON", "OFF"]):
        if (state == "ON"):
            return states["OFF"]
        return states["ON"]
    elif (state in ["PLAY", "PAUSE"]):
        if (state == "PLAY"):
            return states["PAUSE"]
        return states["PLAY"]
    elif (state in ["OPEN", "CLOSED"]):
        if (state == "OPEN"):
            return states["CLOSED"]
        return states["OPEN"]


def emergency_sent_msg(*args):
    print('Emergency sent')


def toggle_input(arg):
    if (arg.pin_num == 0):
        # trigger emergency
        with SocketIO(BURL, PORT, LoggingNamespace) as socketIO:
            socketIO.emit('notification', {'text': 'emergency triggered from button'}, emergency_sent_msg)
            socketIO.wait_for_callbacks(seconds=1)

    elif (arg.pin_num == 1):
        # toggle hue
        new_state = toggle_state(get_state(BUTTON1))
        send_state_change(PATIENT, BUTTON1, new_state)
    elif (arg.pin_num == 2):
        # toggle sonos
        new_state = toggle_state(get_state(BUTTON0))
        send_state_change(PATIENT, BUTTON0, new_state)
    elif (arg.pin_num == 3):
        # toggle door
        new_state = toggle_state(get_state(BUTTON4))
        send_state_change(PATIENT, BUTTON4, new_state)
    elif (arg.pin_num == 4):
        # Never triggered!
        # toggle sleep cycle
        sc_url = "http://smartcare.mi.hdm-stuttgart.de/api/public/sleepcycle.json"
        req = requests.get(sc_url).json()
        last_sc = req[len(req) - 1]
        if (last_sc['dateto']):
            res = requests.post(sc_url, data={'dateFrom': datetime.now().strftime("%Y-%m-%d %H:%M:%S"), 'patientId': 1})
            print(res)
            if (res.status_code == 200):
                print("Created new sleepcycle.")
            else:
                print(res.text)
        else:
            res = requests.put(sc_url + "/{}".format(last_sc['id']),
                               data={'dateTo': datetime.now().strftime("%Y-%m-%d %H:%M:%S")})
            if (res.status_code == 200):
                print("Updated sleepcycle.")
            else:
                print(res.text)

pifacedigital = p.PiFaceDigital()
listener = p.InputEventListener(chip=pifacedigital)
listener.register(0, p.IODIR_FALLING_EDGE, toggle_input)
listener.register(1, p.IODIR_FALLING_EDGE, toggle_input)
listener.register(2, p.IODIR_FALLING_EDGE, toggle_input)
listener.register(3, p.IODIR_FALLING_EDGE, toggle_input)

listener.activate()
