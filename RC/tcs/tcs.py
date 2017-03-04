from __future__ import print_function
import socket
import sys
import os
import fileinput

BUFFER_SIZE = 2048

TCSport = 58018

if len(sys.argv) > 1:
	USERport = int(sys.argv[2])

#socket TCS - Client [UDP]
sock = socket.socket(socket.AF_INET,socket.SOCK_DGRAM)
sock.bind(("",TCSport))
open("languages.txt", 'w').close()

try:
	while True:
		data,addr = sock.recvfrom(BUFFER_SIZE)
		data = data.split(" ")



		if data[0] == "ULQ\n":
			f = open("languages.txt",'r')
			num_lang = 0
			languages = ""
			message = "ULR "
			if os.stat("languages.txt").st_size == 0:
				message += " EOF"
				sock.sendto(message, addr)
			else:
				for line in f:
					currLine = line.split(" ")
					languages += " " + currLine[0]
					num_lang += 1
				message += str(num_lang) + languages
				sock.sendto(message, addr)

		elif data[0] == "UNQ":
			encontrou = False
			f = open("languages.txt",'r')
			for line in f:
				currLine = line.split(" ")
				if currLine[0] == data[1]:
					encontrou = True
					ip = currLine[1]
					port = currLine[2]
					break
			if encontrou:
				message = "UNR " + ip + " " + port
			else:
				message = "UNR EOF"
			sock.sendto(message, addr)

		elif data[0] == "SRG":
			if len(data) != 4:
				status == "ERR"

			else:
				language = data [1]
				TRSip = data[2]
				TRSport = data[3]
				status = "OK"
				f = open("languages.txt")
				if language in f.read():
					status = "NOK"
				f.close()
				if status == "OK":
					f = open("languages.txt",'a')
					f.write(language + " " + TRSip + " " + TRSport)
					f.flush()
					f.close()
			sock.sendto("SRR " + status, addr)

		elif data[0] == "SUN":
			language = data[1];

			for line in fileinput.input("languages.txt", inplace=True):
				if language in line:
					continue
				print(line, end='')
			status = "OK"
			sock.sendto("SUR " + status, addr)
except KeyboardInterrupt:
	print("goodbye")
	sys.exit(1)
