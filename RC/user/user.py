from __future__ import print_function
import socket
import sys
import os
import errno
from socket import error as socket_error

BUFFER_SIZE = 2048

TCSport = 58018
TCSname = socket.gethostbyname(socket.gethostname())

languages = []

for i in range(0,len(sys.argv)):
	if sys.argv[i] == "-n":
		TCSname = sys.argv[i+1]
	elif sys.argv[i] == "-p":
		TCSport = int(sys.argv[i+1])

#socket Client - TCS [UDP]
fd = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
TCSip = socket.gethostbyname(TCSname)

while True:
	command = raw_input()
	command_list = command.split(" ")

	if command == "exit":
		fd.close()
		break

	elif command == "list":

		languages = []

		msg = "ULQ\n"
		fd.sendto(msg, (TCSip, TCSport))

		r, addr = fd.recvfrom(BUFFER_SIZE)
		received = r.split(" ")

		if received[2] == "EOF":
			print("No available languages")
		else:
			for i in range(2,int(received[1])+2):
				languages += [received[i]]
				line = str(i-1) + "- " + received[i]
				print(line)

	elif command_list[0] == "request":
		if len(command_list) < 4:
			print("request bad formulated, try again!")
			continue
		nlang = command_list[1] #number of the requested language
		translation_type = command_list[2]
		number_words = len(command_list)-3

		if not nlang.isdigit() or translation_type not in ("f", "t") or number_words>10:
			print("request bad formulated, try again!")
		elif len(languages) < int(nlang):
			print("Language not available")
		else:
			msg = "UNQ " + languages[int(nlang)-1]
			fd.sendto(msg, (TCSip, TCSport))


			r, addr = fd.recvfrom(BUFFER_SIZE)
			if r=="UNR EOF":
				print("Language not available")
			else:
				received = r.split(" ")
				TRSip = received[1]
				TRSport = int(received[2])
				#socket Client - TSR [TCP]
				sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
				sock.connect((TRSip, TRSport))

				if translation_type == "t":
					message = "TRQ " + translation_type + " " + str(number_words)
					for i in range(3,number_words+3):
						message += " " + command_list[i]
					sock.sendto(message, (TRSip, TRSport))
					received = sock.recv(BUFFER_SIZE)
					if received == "TRR NTA":
						print("translation not available")
					else:
						translation = received.split(" ")

						message = ""
						for i in range(3,len(translation)):
							message += translation[i] + " "
						print(message)

				elif translation_type == "f":
					if len(command_list) != 4:
						print("request bad formulated, try again!")
						sock.sendto("error", (TRSip, TRSport))
						continue
					image = command_list[3]
					if not os.path.isfile(image):
						print("non existent image")
						sock.sendto("error", (TRSip, TRSport))
						continue

					size = os.stat(image).st_size
					message = "TRQ " + translation_type + " " + image + " " + str(size) + " "

					sock.sendto(message, (TRSip, TRSport))


					''' User sends image for translation '''
					f = open(image,'rb')

					while (size > 0):
						l = f.read(BUFFER_SIZE)
						sent = sock.sendto(l, (TRSip, TRSport))
						size = size - sent

					f.close()

					''' User receives translated image'''
					recv = sock.recv(BUFFER_SIZE).split(" ")
					if recv[1] == "NTA":
						print("translation not available")
					else:
						recv_image = recv[2]
						size = recv_size = int(recv[3])

						with open(recv_image, 'wb') as f:
							while recv_size > 0:
								data = sock.recv(BUFFER_SIZE)
								f.write(data)
								recv_size = recv_size - sys.getsizeof(data)
						f.close()

						message = "received file " + recv_image + " " + str(recv_size)
						print(message)
	else:
		print("Command not recognised, try again!")
