# Makefile
all: par-shell fibonacci clean 

par-shell: par-shell.o commandlinereader.o
	gcc -o main par-shell.o commandlinereader.o

fibonacci: fibonacci.o
	gcc -o fibonacci fibonacci.o

par-shell.o: par-shell.c
	gcc -Wall -g -c par-shell.c

commandlinereader.o: commandlinereader.c commandlinereader.h
	gcc -Wall -g -c commandlinereader.c

fibonacci.o: fibonacci.c 
	gcc -Wall -g -c fibonacci.c

clean:
	rm *.o
