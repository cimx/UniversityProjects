%{
// $Id: ook_parser.y,v 1.10 2017/07/19 23:25:11 ist181172 Exp $
//-- don't change *any* of these: if you do, you'll break the compiler.
#include <cdk/compiler.h>
#include "ast/all.h"
#define LINE       compiler->scanner()->lineno()
#define yylex()    compiler->scanner()->scan()
#define yyerror(s) compiler->scanner()->error(s)
#define YYPARSE_PARAM_TYPE std::shared_ptr<cdk::compiler>
#define YYPARSE_PARAM      compiler
//-- don't change *any* of these --- END!
%}

%union {
  int                   i;	        /* integer value */
  double                d;          /* double/real value */
  std::string          *s;	        /* symbol name or string literal */
  cdk::basic_node      *node;	      /* node pointer */
  cdk::sequence_node   *sequence;
  cdk::expression_node *expression; /* expression nodes */
  cdk::lvalue_node     *lvalue;
  basic_type           *type;
  ook::function_declaration_node    *fundecl;
};

%token <i> tINTEGER
%token <d> tDOUBLE
%token <s> tIDENTIFIER tSTR
%token tPUBLIC tIMPORT tIF tELSE tWHILE tNEXT tSTOP tRETURN tNULL tINT tFLOAT tSTRING tPOINTER tVOID tPRINT tPRINTNL tREAD tSWEEP

%nonassoc tIF
%nonassoc ')'
%nonassoc tELSE

%right '=' 
%left '|'
%left '&' 
%nonassoc '~'
%left tEQ tNE 
%left tGE tLE '>' '<'
%left '+' '-' 
%left '*' '/' '%'
%nonassoc tUNARY '?'
%nonassoc '['


%type <node> file declaration vardec body param instr cond iter
%type <sequence> declarations vardecs fparams instrs argscall
%type <expression> expr literal funcall
%type <lvalue> lval
%type <type> type void
%type <fundecl> fundec
%type <s> str


%{
//-- The rules below will be included in yyparse, the main parsing function.
%}
%%

file              : declarations               { YYPARSE_PARAM->ast($1); }
                  |                            { YYPARSE_PARAM->ast(new ook::null_node(LINE)); }
                  ;

declarations      : declarations declaration   { $$ = new cdk::sequence_node(LINE, $2, $1);} 
                  | declaration                { $$ = new cdk::sequence_node(LINE, $1);} 
                  ;

declaration       : vardec                     { $$ = $1; }
                  | fundec                     { $$ = $1; }
                  | fundec body                { $$ = new ook::function_definition_node(LINE, $1, $2); }
                  ;

type              : tINT                       { $$ = new basic_type(4, basic_type::TYPE_INT); }    
                  | tFLOAT                     { $$ = new basic_type(8, basic_type::TYPE_DOUBLE); }
                  | tSTRING                    { $$ = new basic_type(4, basic_type::TYPE_STRING); }
                  | tPOINTER                   { $$ = new basic_type(4, basic_type::TYPE_POINTER); }
                  ;

vardec            : tPUBLIC type tIDENTIFIER '=' expr ';'   { $$ = new ook::variable_declaration_node(LINE, true, false, $2, $3, $5); }
                  | tPUBLIC type tIDENTIFIER ';'            { $$ = new ook::variable_declaration_node(LINE, true, false, $2, $3, nullptr); }
                  | type tIDENTIFIER '=' expr ';'           { $$ = new ook::variable_declaration_node(LINE, false, false, $1, $2, $4); }
                  | type tIDENTIFIER ';'                    { $$ = new ook::variable_declaration_node(LINE, false, false, $1, $2, nullptr); }
                  | tIMPORT type tIDENTIFIER ';'            { $$ = new ook::variable_declaration_node(LINE, false, true, $2, $3, nullptr); }
                  ;

vardecs           : vardec                     { $$ = new cdk::sequence_node(LINE, $1); } 
                  | vardecs vardec             { $$ = new cdk::sequence_node(LINE, $2, $1);}
                  ;

fundec            : tPUBLIC type tIDENTIFIER '(' fparams ')'              { $$ = new ook::function_declaration_node(LINE,true,false,$2,$3,$5,nullptr); }
                  | tPUBLIC type tIDENTIFIER '(' fparams ')' '=' literal  { $$ = new ook::function_declaration_node(LINE,true,false,$2,$3,$5,$8); }
                  | tIMPORT type tIDENTIFIER '(' fparams ')'              { $$ = new ook::function_declaration_node(LINE,false,true,$2,$3,$5,nullptr); }
                  | tIMPORT type tIDENTIFIER '(' fparams ')' '=' literal  { $$ = new ook::function_declaration_node(LINE,false,true,$2,$3,$5,$8); }
                  | type tIDENTIFIER '(' fparams ')'                      { $$ = new ook::function_declaration_node(LINE,false,false,$1,$2,$4,nullptr); }
                  | type tIDENTIFIER '(' fparams ')' '=' literal          { $$ = new ook::function_declaration_node(LINE,false,false,$1,$2,$4,$7); }
                  | void tIDENTIFIER '(' fparams ')'                      { $$ = new ook::function_declaration_node(LINE,false,false,$1,$2,$4,nullptr); }
                  | tPUBLIC void tIDENTIFIER '(' fparams ')'              { $$ = new ook::function_declaration_node(LINE,true,false,$2,$3,$5,nullptr); }
                  | tIMPORT void tIDENTIFIER '(' fparams ')'              { $$ = new ook::function_declaration_node(LINE,false,true,$2,$3,$5,nullptr); }
                  ;

void              : tVOID                      { $$ = new basic_type(0, basic_type::TYPE_VOID); }
                  ;

fparams           : param                      { $$ = new cdk::sequence_node(LINE, $1); }
                  | fparams ',' param          { $$ = new cdk::sequence_node(LINE, $3, $1); }
                  |                            { $$ = nullptr; }
                  ;  

param             : type tIDENTIFIER           { $$ = new ook::variable_declaration_node(LINE, false, true, $1, $2, nullptr); }
                  ;

literal           : tINTEGER                   { $$ = new cdk::integer_node(LINE, $1); }
                  | tDOUBLE                    { $$ = new cdk::double_node(LINE, $1); }
                  | tNULL                      { $$ = new ook::null_node(LINE); }
                  | str                        { $$ = new cdk::string_node(LINE, $1); }   
                  ;

str               : str tSTR                   { $$ = new std::string(*$1 + *$2); delete $1; delete $2;}
                  | tSTR                       { $$ = $1; }
                  ;
         
body              : '{' vardecs instrs '}'     { $$ = new ook::block_node(LINE, $2, $3); }
                  | '{' vardecs '}'            { $$ = new ook::block_node(LINE, $2, nullptr); }
                  | '{' instrs '}'             { $$ = new ook::block_node(LINE, nullptr, $2); }
                  | '{' '}'                    { $$ = new ook::block_node(LINE, nullptr, nullptr); }
                  ;

instrs            : instrs instr               { $$ = new cdk::sequence_node(LINE, $2, $1); }
                  | instr                      { $$ = new cdk::sequence_node(LINE, $1); }
                  ;

instr             : expr ';'                   { $$ = new ook::evaluation_node(LINE, $1); }
                  | expr tPRINT                { $$ = new ook::print_node(LINE, $1, false); }
                  | expr tPRINTNL              { $$ = new ook::print_node(LINE, $1, true); }
                  | tSTOP ';'                  { $$ = new ook::stop_node(LINE, 1); }
                  | tSTOP tINTEGER ';'         { $$ = new ook::stop_node(LINE, $2); }
                  | tNEXT ';'                  { $$ = new ook::next_node(LINE, 1); }
                  | tNEXT tINTEGER ';'         { $$ = new ook::next_node(LINE, $2); }
                  | tRETURN                    { $$ = new ook::return_node(LINE); }
                  | cond                       { $$ = $1; }
                  | iter                       { $$ = $1; }
                  | body                       { $$ = $1; }
                  ;  

cond              : tIF '(' expr ')' instr              { $$ = new ook::if_node(LINE, $3, $5); }
                  | tIF '(' expr ')' instr tELSE instr  { $$ = new ook::if_else_node(LINE, $3, $5, $7); }
                  ;

iter              : tWHILE '(' expr ')' instr           { $$ = new ook::while_node(LINE, $3, $5); }
                  ;

funcall           : tIDENTIFIER '(' argscall ')'        { $$ = new ook::function_call_node(LINE, $1, $3); }     
                  | tIDENTIFIER '(' ')'                 { $$ = new ook::function_call_node(LINE, $1, new cdk::sequence_node(LINE)); } 
                  ;
         
argscall          : expr ',' argscall                   { $$ = new cdk::sequence_node(LINE, $1, $3); }
                  | expr                                { $$ = new cdk::sequence_node(LINE); }          
                  ;

expr              : funcall                     { $$ = $1;  }
                  | literal                     { $$ = $1;  }
                  | '-' expr %prec tUNARY       { $$ = new cdk::neg_node(LINE, $2); }
                  | '+' expr %prec tUNARY       { $$ = new ook::identity_node(LINE, $2); }
                  | '~' expr %prec tUNARY       { $$ = new cdk::not_node(LINE, $2); }                          
                  | expr '+' expr               { $$ = new cdk::add_node(LINE, $1, $3); }
                  | expr '-' expr               { $$ = new cdk::sub_node(LINE, $1, $3); }
                  | expr '*' expr               { $$ = new cdk::mul_node(LINE, $1, $3); }
                  | expr '/' expr               { $$ = new cdk::div_node(LINE, $1, $3); }
                  | expr '%' expr               { $$ = new cdk::mod_node(LINE, $1, $3); }
                  | expr '<' expr               { $$ = new cdk::lt_node(LINE, $1, $3); }
                  | expr '>' expr               { $$ = new cdk::gt_node(LINE, $1, $3); }
                  | expr tGE expr               { $$ = new cdk::ge_node(LINE, $1, $3); }
                  | expr tLE expr               { $$ = new cdk::le_node(LINE, $1, $3); }
                  | expr tNE expr               { $$ = new cdk::ne_node(LINE, $1, $3); }
                  | expr tEQ expr               { $$ = new cdk::eq_node(LINE, $1, $3); }
                  | expr '&' expr               { $$ = new cdk::and_node(LINE, $1, $3); }
                  | expr '|' expr               { $$ = new cdk::or_node(LINE, $1, $3); }
                  | '(' expr ')'                { $$ = $2; }
                  | tREAD                       { $$ = new ook::read_node(LINE); }
                  | lval                        { $$ = new cdk::rvalue_node(LINE, $1); }  //FIXME
                  | lval '=' expr               { $$ = new cdk::assignment_node(LINE, $1, $3); }
                  | lval '?'                    { $$ = new ook::memory_address_node(LINE, $1); }       
                  | '[' expr ']'                { $$ = new ook::memory_alloc_node(LINE, $2); }  
                  ;

lval              : tIDENTIFIER                 { $$ = new cdk::identifier_node(LINE, $1); }
                  | expr '[' expr ']'           { $$ = new ook::lvalue_index_node(LINE,$1,$3); }            
                  ;

%%
