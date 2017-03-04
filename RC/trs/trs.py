import socket
import sys
import os
import signal
import errno



BUFFER_SIZE = 2048
MAX_CLIENTS = 200

TRSport = 59000
TCSname = socket.gethostname()
TCSport = 58018
userConnected = False

def trs_exit():
	print("TRS quitting")
	sys.exit(1)

try:
	TRSname = socket.gethostname()
	TRSip = socket.gethostbyname(TRSname)
except:
	print("Connection aborted")
	trs_exit()

TRSname = socket.gethostname()
TRSip = socket.gethostbyname(TRSname)


if len(sys.argv) not in (2, 4, 6, 8):
	print("request bad formulated")
	sys.exit(1)
language = sys.argv[1]
i = 2

while (i < len(sys.argv)):
	if sys.argv[i] == "-n":
		TRSname = sys.argv[i+1]
		try:
			TRSip = socket.gethostbyname(TRSname)
		except socket.error:
			print("unvalid ip")
			sys.exit(1)
	elif sys.argv[i] == "-p":
		TRSport = int(sys.argv[i+1])

	elif sys.argv[i] == "-e":
		TCSport = int(sys.argv[i+1])
	else:
		print("request bad formulated")
		sys.exit(1)
	i += 2

#socket TRS - TCS [UDP]
TCSsocket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
TCSip = socket.gethostbyname(TCSname)
#REGISTERS TRS SERVER ON TCS
message = "SRG " + language + " " + str(TRSip) + " " + str(TRSport) + "\n"
TCSsocket.sendto(message, (TCSip, TCSport))

try:
	TCSsocket.settimeout(2)
	received = TCSsocket.recv(BUFFER_SIZE).split()
	status = received[1]
	TCSsocket.settimeout(None)
except socket.timeout:
	print("Translation Contact Center not available. Quitting")
	TCSsocket.close()
	sys.exit(1)

if status == "NOK":
	print("Language already exists")
	sys.exit(1)
elif status == "ERR":
	print("Request bad formulated")
	sys.exit(1)

#socket TRS - Client [TCP]
try:
	s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	s.bind(('', TRSport))
	s.listen(MAX_CLIENTS) #argument defines the max number of clients waiting
except:
	print("Unable to connect to TCS")
	message = "SUN " + language + " " + str(TRSip) + " " + str(TRSport) + "\n"
	TCSsocket.sendto(message, (TCSip, TCSport))
	received = TCSsocket.recv(BUFFER_SIZE)
	trs_exit()


try:
	while True:

		userSocket, addr = s.accept()
		userConnected = True

		received = (userSocket.recv(BUFFER_SIZE)).split(" ")
		if received[0] == "error":
			continue
		elif received[1] == "t":
			filename = language + '/text_translation.txt'
			message = "TRR t " + received[2]

			for i in range(3,int(received[2])+3):
				f = open(filename, 'r')
				encontrou = False
				for line in f:
					words = line.split()
					if words[0] == received[i]:
						encontrou = True
						message += " " + words[1]
						break
				if not encontrou:
					message = "TRR NTA"
					break

			userSocket.send(message)
			f.close()

		elif received[1] == "f":
			filename = 'file_translation.txt'
			recv_image = received[2]
			recv_size = int(received[3])

			f = open(language + '/' + filename, 'r')
			encontrou = False
			for line in f:
				words = line.split()
				if words[0] == recv_image:
					encontrou = True
					break
			if not encontrou:
				message = "TRR NTA"
				break
			f.close()
			''' **************************************
			* TRS receives image sent by user
				************************************** '''

			with open(language + '/' + recv_image, 'wb') as f:
				while recv_size > 0:
					received = userSocket.recv(BUFFER_SIZE)
					f.write(received)
					recv_size = recv_size - sys.getsizeof(received)
			f.close()

			''' TRS serches for translated image '''
			f = open(language + '/' + filename, 'r')

			for line in f:
				words = line.split()
				if words[0] == recv_image:
					transl_image = words[1]
					break
			f.close()

			transl_size = os.stat(language + '/' + transl_image).st_size


			if not encontrou:
				message = "TRR NTA"
			else:
				message = "TRR f " + transl_image + " " + str(transl_size) + " "
				userSocket.send(message)
				''' TRS sends translated image'''
				f = open(language + '/' + transl_image,'rb')

				while (transl_size > 0):
					data = f.read(BUFFER_SIZE)
					sent = userSocket.send(data)
					transl_size = transl_size - sent

				f.close()
				userSocket.close()
				''' ----------------------------------- '''
except KeyboardInterrupt:
	status = "NOK"
	message = "SUN " + language + " " + str(TRSip) + " " + str(TRSport) + "\n"
	TCSsocket.sendto(message, (TCSip, TCSport))
	received = TCSsocket.recv(BUFFER_SIZE).split()
	status = received[1]
	if status == "NOK":
		print("Request denied")
	elif status == "ERR":
		print("Request bad formulated")
	else:
		trs_exit()
