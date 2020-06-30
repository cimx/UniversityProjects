%option c++ yyclass="ook_scanner" outfile="ook_scanner.cpp"
%option stack noyywrap yylineno 8bit
%{ 
/* $Id: ook_scanner.l,v 1.8 2017/07/25 21:57:09 ist181172 Exp $ */
// make relevant includes before including the parser's tab file
#include <string>
#include <cdk/ast/sequence_node.h>
#include <cdk/ast/expression_node.h>
#include "ook_scanner.h"
#include "ook_parser.tab.h"

// don't change this
#define yyerror LexerError
%}

INTEGER 			   0|[1-9][0-9]*

OCTAL				   0[0-7]*[1-7]

REAL1           	   [1-9][0-9]*\.[0-9]*[1-9]([eE][+-]?[1-9][0-9]*)?
REAL2				   \.[0-9]*[1-9]([eE][+-]?[1-9][0-9]*)?
REAL3				   [1-9][0-9]*[eE][+-]?[1-9][0-9]*


%x X_STRING X_STRING_MORE X_STRING_IGNORE X_COMMENT 
%%

"//".$*                 ; /* ignore comments */

">="                   return tGE; 
"<="                   return tLE; 
"=="                   return tEQ; 
"!="                   return tNE; 

"public"               return tPUBLIC;
"import"               return tIMPORT;
"if"                   return tIF; 
"else"                 return tELSE; 
"while"                return tWHILE; 
"next"				   return tNEXT;
"stop"				   return tSTOP;
"return"			   return tRETURN;
"null"				   return tNULL;	 
"int"				   return tINT;
"float"				   return tFLOAT;	
"string"			   return tSTRING;
"pointer"			   return tPOINTER;
"void"				   return tVOID; 
"!"                	   return tPRINT;
"!!"                   return tPRINTNL; 
"@"                    return tREAD;


[A-Za-z][A-Za-z0-9]*				yylval.s = new std::string(yytext); return tIDENTIFIER;

\"                        			yy_push_state(X_STRING); yylval.s = new std::string("");
<X_STRING>\"              			yy_pop_state(); return tSTR;
<X_STRING>\\\"            			yy_pop_state(); *yylval.s += std::string(1,(int)*yytext);  
<X_STRING>\\\\            			yy_pop_state(); *yylval.s += std::string(1,(int)*yytext);
<X_STRING>\\              			yy_push_state(X_STRING_MORE);
<X_STRING>.               			*yylval.s += yytext;
			
<X_STRING_MORE>n                    yy_pop_state(); *yylval.s += std::string(1,10);
<X_STRING_MORE>t                    yy_pop_state(); *yylval.s += std::string(1,9);  
<X_STRING_MORE>r                    yy_pop_state(); *yylval.s += std::string(1,13);
<X_STRING_MORE>[\"\\]               yy_pop_state(); *yylval.s += std::string(1,(int)*yytext);  
<X_STRING_MORE>0[0]?                BEGIN(X_STRING_IGNORE); return tSTR;
<X_STRING_MORE>[0-9a-fA-F]{1,2}     yy_pop_state(); *yylval.s += std::string(1,strtol(yytext, nullptr, 16));
<X_STRING_MORE>.|\n                 yyerror("Unknown carachter in string");

<X_STRING_IGNORE>\"             	BEGIN(INITIAL);
<X_STRING_IGNORE>\\\\           	;
<X_STRING_IGNORE>\\\"           	;
<X_STRING_IGNORE>\\             	;
<X_STRING_IGNORE>.|\n           	;     
 
"/*"                      			yy_push_state(X_COMMENT);
<X_COMMENT>"/*"           			yy_push_state(X_COMMENT);
<X_COMMENT>"*/"           			yy_pop_state();
<X_COMMENT>.|\n           			;

{REAL1}|{REAL2}|{REAL3}       		{ yylval.d = strtod(yytext, nullptr); if(errno == ERANGE) yyerror("Overflow in real number"); return tDOUBLE; }

{OCTAL}	          					{ yylval.i = strtol(yytext, nullptr, 8); if(errno == ERANGE) yyerror("Overflow in octal number"); return tINTEGER; }

{INTEGER}	             			yylval.i = strtol(yytext, nullptr, 10); return tINTEGER;

0[8-9]+|0[0]+                 		yyerror("number except 0 and octal numbers can't star with zero");

[-()<,>=+*/%{}.~|&?\[\].;]       	return *yytext;

[ \t\n]+               				; /* ignore whitespace */

.                      				yyerror("Unknown character");

%%
// Very, very dirty hack: flex is a mess generating C++ scanners.
int ook_scanner::yywrap() { return 1; }
