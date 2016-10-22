# Makefile
all: par-shell par-shell-terminal fibonacci

par-shell: par-shell.c commandlinereader.c commandlinereader.h list.c list.h list-terminais.c list-terminais.h
	gcc -Wall -g -c commandlinereader.c
	gcc -Wall -g -c list.c
	gcc -Wall -g -c list-terminais.c
	gcc -pthread -Wall -g -c par-shell.c
	gcc -pthread -o par-shell par-shell.o commandlinereader.o list.o list-terminais.o

par-shell-terminal: par-shell-terminal.c
	gcc -g -c par-shell-terminal.c
	gcc -o par-shell-terminal par-shell-terminal.o

fibonacci: fibonacci.c
	gcc -Wall -g -c fibonacci.c
	gcc -o fibonacci fibonacci.o

clean:
	rm *.o
