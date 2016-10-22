#include <stdlib.h>
#include <stdio.h>
#include "list-terminais.h"

list_terminais* terminais_new()
{
   list_terminais *list;
   list = (list_terminais*) malloc(sizeof(list_terminais));
   if( list == NULL )
   {
     perror("Erro no malloc da lista de terminais : ");
   }
   list->first = NULL;
   return list;
}

void terminais_destroy(list_terminais *list)
{
	struct lst_terminal *item, *nextitem;

	item = list->first;
	while (item != NULL){
		nextitem = item->next;
		free(item);
		item = nextitem;
	}
	free(list);
}


void insert_new_terminal(list_terminais *list, int pid)
{
	lst_terminal_t *item;

	item = (lst_terminal_t *) malloc (sizeof(lst_terminal_t));
	item->pidterminal = pid;
	item->next = list->first;
	list->first = item;
}

int remove_terminal(list_terminais *list){
	lst_terminal_t *item = list->first;
	int pid;
	if(item != NULL){
		list->first = item->next;
		pid = item->pidterminal;
		free(item);
		return pid;
	}
	return -1;
}