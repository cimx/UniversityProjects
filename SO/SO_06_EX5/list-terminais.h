#ifndef LISTTERMINAIS_H
#define LISTTERMINAIS_H

#include <stdlib.h>
#include <stdio.h>


/* lst_terminal - each element of the list points to the next element */
typedef struct lst_terminal {
   int pidterminal;
   struct lst_terminal *next;
} lst_terminal_t;

/* list_terminais */
typedef struct {
   lst_terminal_t * first;
} list_terminais;

/* lst_new - allocates memory for list_terminais and initializes it */
list_terminais* terminais_new();

/* lst_destroy - free memory of list_terminais and all its items */
void terminais_destroy(list_terminais *);

/* insert_new_process - insert a new item with process id and its start time in list 'list' */
void insert_new_terminal(list_terminais *list, int pid);

int remove_terminal(list_terminais *list); 
#endif 
