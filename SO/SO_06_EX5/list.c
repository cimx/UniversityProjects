/*
 * list.c - implementation of the integer list functions 
 */

#include <stdlib.h>
#include <stdio.h>
#include <time.h>
#include "list.h"

list_t* lst_new()
{
   list_t *list;
   if((list = (list_t*) malloc(sizeof(list_t))) == NULL) {
     	perror("Erro no malloc do lst_new: ");
   }
   list->first = NULL;
   return list;
}


void lst_destroy(list_t *list)
{
	struct lst_iitem *item, *nextitem;

	item = list->first;
	while (item != NULL){
		nextitem = item->next;
		free(item);
		item = nextitem;
	}
	free(list);
}


void insert_new_process(list_t *list, int pid, time_t starttime)
{
	lst_iitem_t *item;

	if((item = (lst_iitem_t *) malloc(sizeof(lst_iitem_t))) == NULL){
		perror("Erro no malloc do insert_new_process: ");
	}
	item->pid = pid;
	item->estado = 0;
	item->wifexited = false;
	item->starttime = starttime;
	item->endtime = 0;
	item->next = list->first;
	list->first = item;
}


time_t update_terminated_process(list_t *list, int pid, int estado,bool wifexited, time_t endtime)
{
	lst_iitem_t *item;
	time_t starttime = -1;
	for(item = list->first; item != NULL; item = item->next){
		if(item->pid == pid){
			item->endtime = endtime;
			starttime = item->starttime;
			if(wifexited){
					item->wifexited = true;
					item->estado = WEXITSTATUS(estado);
			}
			break;
		}
	}	
	
	printf("teminated updated process with endtime: %s pid: %d\n",ctime(&endtime), pid);
	return starttime;
}


void lst_print(list_t *list)
{
	lst_iitem_t *item;
	printf("Process list with start and end time:\n");
	for(item = list->first; item != NULL ;item = item->next){
		if(item->wifexited == true) {
			printf("PID:%d\t Estado: %d\t Tempo: %g\n", item->pid, item->estado, difftime(item->endtime,item->starttime));
		}
		else{
			printf("Processo %d saiu sem chamar exit\n", item->pid);
		}
	}
	printf("-- end of list.\n");
}
