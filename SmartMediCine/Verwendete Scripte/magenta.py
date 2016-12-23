#Import
import RPi.GPIO as GPIO
import time
import datetime
import os
import sys
import colorsys
 
#Board Mode: Angabe der Pin-Nummer
GPIO.setmode(GPIO.BOARD)
 
#GPIO Pin definieren fuer den Dateneingang vom Sensor
HALL_GPIO = 32
TILT_GPIO = 36

GPIO.setup(HALL_GPIO,GPIO.IN)
GPIO.setup(TILT_GPIO,GPIO.IN)
countOne=0
countZero=0
countGekippt=0

try:  

 while True:
	if GPIO.input(HALL_GPIO)==1:
		print GPIO.input(TILT_GPIO)
		print "offen"
		if GPIO.input(TILT_GPIO) == 1:
				countOne=countOne+1
		if GPIO.input(TILT_GPIO) == 0:
				countZero=countZero+1
	
		if countOne >= 5:	
			countOne = 0
			countZero = 0

			for i in range(3):
				time.sleep(0.4)
				os.system("/usr/bin/curl --header \"Content-Type: text/plain\" --request POST --data \"OFF\" http://192.168.0.166:8080/rest/items/MilightSwitch")
				time.sleep(0.4)
				os.system("/usr/bin/curl --header \"Content-Type: text/plain\" --request POST --data \"ON\" http://192.168.0.166:8080/rest/items/MilightSwitch")
			
				if i==2:
					time.sleep(0.8)
					os.system("/usr/bin/curl --header \"Content-Type: text/plain\" --request POST --data \"OFF\" http://192.168.0.166:8080/rest/items/MilightSwitch")

			break
		if countZero >= 50 and countOne < 5:
			countOne = 0
			countZero = 0
			print "nicht gekippt"
		
	time.sleep(0.01)
	if GPIO.input(HALL_GPIO)==0:
		print "zu"
					
	time.sleep(0.1)
 
except KeyboardInterrupt:
 print "Beendet"
 GPIO.cleanup()
