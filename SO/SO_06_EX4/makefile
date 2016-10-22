# Makefile
all: par-shell fibonacci clean

list.o: list.c list.h
	gcc -Wall -g -c list.c

main.o: main.c
	gcc -Wall -g -c main.c

par-shell: par-shell.o commandlinereader.o list.o
	gcc -pthread -o par-shell par-shell.o commandlinereader.o list.o

fibonacci: fibonacci.o
	gcc -o fibonacci fibonacci.o

par-shell.o: par-shell.c
	gcc -pthread -Wall -g -c par-shell.c

commandlinereader.o: commandlinereader.c commandlinereader.h
	gcc -Wall -g -c commandlinereader.c

fibonacci.o: fibonacci.c 
	gcc -Wall -g -c fibonacci.c

clean:
	rm *.o
