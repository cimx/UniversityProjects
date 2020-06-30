#include <string>
#include <sstream>
#include "targets/type_checker.h"
#include "targets/postfix_writer.h"
#include "targets/counter_size.h"
#include "ast/all.h"  // all.h is automatically generated


//---------------------------------------------------------------------------

void ook::postfix_writer::do_sequence_node(cdk::sequence_node * const node, int lvl) {
  for (size_t i = 0; i < node->size(); i++) {
    node->node(i)->accept(this, lvl);
  }
}

//---------------------------------------------------------------------------

void ook::postfix_writer::do_integer_node(cdk::integer_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  if(!_variable_declaration){
    _pf.INT(node->value());
  }    
  else _pf.CONST(node->value());
}

void ook::postfix_writer::do_double_node(cdk::double_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  std::string lbl = mklbl(++_lbl);

  if (_inside_function) {  //LOCAL
    _pf.RODATA();
    _pf.ALIGN();
    _pf.LABEL(lbl);
    _pf.DOUBLE(node->value());
    _pf.TEXT();
    _pf.ALIGN();
    _pf.ADDR(lbl);
    _pf.DLOAD();
  }
  else                   //GLOBAL
    _pf.DOUBLE(node->value());
}

void ook::postfix_writer::do_string_node(cdk::string_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  std::string lbl = mklbl(++_lbl);

  /* generate the string */
  _pf.RODATA(); // strings are DATA readonly
  _pf.ALIGN(); // make sure we are aligned
  _pf.LABEL(lbl); // give the string a name
  _pf.STR(node->value()); // output string characters


  if (_inside_function) {     //LOCAL
    /* leave the address on the stack */
    _pf.TEXT(); // return to the TEXT segment
    _pf.ADDR(lbl); // the string to be printed
  }
  else {                      //GLOBAL
    _pf.DATA();
    _pf.ID(lbl);
  }
}

//---------------------------------------------------------------------------

void ook::postfix_writer::do_neg_node(cdk::neg_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  node->argument()->accept(this, lvl);      // determine the value

  if (node->type()->name() == basic_type::TYPE_DOUBLE)
    _pf.DNEG();
  else
  _pf.NEG();    // 2-complement
}

void ook::postfix_writer::do_not_node(cdk::not_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  node->argument()->accept(this, lvl); // determine the value
  _pf.NOT();
}

//---------------------------------------------------------------------------

void ook::postfix_writer::do_and_node(cdk::and_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  
  std::string lbl = mklbl(++_lbl);

  node->left()->accept(this, lvl);
  _pf.JZ(lbl);

  node->right()->accept(this, lvl);
  _pf.LABEL(lbl);
}

void ook::postfix_writer::do_or_node(cdk::or_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  
  std::string lbl = mklbl(++_lbl);

  node->left()->accept(this, lvl);
  _pf.JNZ(lbl);

  node->right()->accept(this, lvl);
  _pf.LABEL(lbl);
}

//---------------------------------------------------------------------------

void ook::postfix_writer::do_add_node(cdk::add_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  node->left()->accept(this, lvl);
  if (node->type()->name() == basic_type::TYPE_DOUBLE){
    if (node->left()->type()->name() == basic_type::TYPE_INT) 
      _pf.I2D();
  } 

  node->right()->accept(this, lvl);
  if (node->type()->name() == basic_type::TYPE_DOUBLE){
    if (node->right()->type()->name() == basic_type::TYPE_INT)  
      _pf.I2D();
  }

  if (node->type()->name() == basic_type::TYPE_DOUBLE)
    _pf.DADD();
  else
    _pf.ADD();
}
void ook::postfix_writer::do_sub_node(cdk::sub_node * const node, int lvl) {
 ASSERT_SAFE_EXPRESSIONS;

  node->left()->accept(this, lvl);
  if (node->type()->name() == basic_type::TYPE_DOUBLE)
    if (node->left()->type()->name() == basic_type::TYPE_INT) 
      _pf.I2D();

  node->right()->accept(this, lvl);
  if (node->type()->name() == basic_type::TYPE_DOUBLE)
    if (node->right()->type()->name() == basic_type::TYPE_INT)
      _pf.I2D();

  if (node->type()->name() == basic_type::TYPE_DOUBLE)
    _pf.DSUB();
  else
    _pf.SUB();
}
void ook::postfix_writer::do_mul_node(cdk::mul_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  node->left()->accept(this, lvl);
  if (node->type()->name() == basic_type::TYPE_DOUBLE)
    if (node->left()->type()->name() == basic_type::TYPE_INT) 
      _pf.I2D();

  node->right()->accept(this, lvl);
  if (node->type()->name() == basic_type::TYPE_DOUBLE)
    if (node->right()->type()->name() == basic_type::TYPE_INT)
      _pf.I2D();


  if (node->type()->name() == basic_type::TYPE_DOUBLE)
    _pf.DMUL();
  else
    _pf.MUL();
}
void ook::postfix_writer::do_div_node(cdk::div_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  node->left()->accept(this, lvl);
  if (node->type()->name() == basic_type::TYPE_DOUBLE)
    if (node->left()->type()->name() == basic_type::TYPE_INT)
      _pf.I2D();

  node->right()->accept(this, lvl);
  if (node->type()->name() == basic_type::TYPE_DOUBLE)
    if (node->right()->type()->name() == basic_type::TYPE_INT)
      _pf.I2D();

  if (node->type()->name() == basic_type::TYPE_DOUBLE)
    _pf.DDIV();
  else
    _pf.DIV();
}
void ook::postfix_writer::do_mod_node(cdk::mod_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl);
  node->right()->accept(this, lvl);
  _pf.MOD();
}
void ook::postfix_writer::do_lt_node(cdk::lt_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  node->left()->accept(this, lvl);
  if ((node->type()->name()  == basic_type::TYPE_DOUBLE) && (node->left()->type()->name() == basic_type::TYPE_INT))
    _pf.I2D(); 
    
  node->right()->accept(this, lvl);
  if ((node->type()->name()  == basic_type::TYPE_DOUBLE) && (node->right()->type()->name() == basic_type::TYPE_INT))
    _pf.I2D(); 
    
  if(node->type()->name()  == basic_type::TYPE_DOUBLE) {
    _pf.DCMP();
    _pf.INT(0);
  }     
  _pf.LT();
}
void ook::postfix_writer::do_le_node(cdk::le_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  node->left()->accept(this, lvl);
  if ((node->type()->name()  == basic_type::TYPE_DOUBLE) && (node->left()->type()->name() == basic_type::TYPE_INT))
    _pf.I2D();    
    
  node->right()->accept(this, lvl);
  if ((node->type()->name()  == basic_type::TYPE_DOUBLE) && (node->right()->type()->name() == basic_type::TYPE_INT))
     _pf.I2D();    
    
  if(node->type()->name()  == basic_type::TYPE_DOUBLE) {
    _pf.DCMP();
    _pf.INT(0);
  }     

  _pf.LE();
}
void ook::postfix_writer::do_ge_node(cdk::ge_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  node->left()->accept(this, lvl);
  if ((node->type()->name()  == basic_type::TYPE_DOUBLE) && (node->left()->type()->name() == basic_type::TYPE_INT))
    _pf.I2D(); 
    
  node->right()->accept(this, lvl);
  if ((node->type()->name()  == basic_type::TYPE_DOUBLE) && (node->right()->type()->name() == basic_type::TYPE_INT))
    _pf.I2D();  
    
  if(node->type()->name()  == basic_type::TYPE_DOUBLE) {
    _pf.DCMP();
    _pf.INT(0);
  }     
  _pf.GE();
}
void ook::postfix_writer::do_gt_node(cdk::gt_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  
  node->left()->accept(this, lvl);
  if ((node->type()->name()  == basic_type::TYPE_DOUBLE) && (node->left()->type()->name() == basic_type::TYPE_INT))
    _pf.I2D(); 
    
  node->right()->accept(this, lvl);
  if ((node->type()->name()  == basic_type::TYPE_DOUBLE) && (node->right()->type()->name() == basic_type::TYPE_INT))
    _pf.I2D(); 
    
  if(node->type()->name()  == basic_type::TYPE_DOUBLE) {
    _pf.DCMP();
    _pf.INT(0);
  }     
  _pf.GT();
}
void ook::postfix_writer::do_ne_node(cdk::ne_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  node->left()->accept(this, lvl);
  if ((node->type()->name()  == basic_type::TYPE_DOUBLE) && (node->left()->type()->name() == basic_type::TYPE_INT))
    _pf.I2D(); 
    
  node->right()->accept(this, lvl);
  if ((node->type()->name()  == basic_type::TYPE_DOUBLE) && (node->right()->type()->name() == basic_type::TYPE_INT))
    _pf.I2D(); 
      
  if(node->type()->name()  == basic_type::TYPE_DOUBLE) {
    _pf.DCMP();
    _pf.INT(0);
  }     
  _pf.NE();
}
void ook::postfix_writer::do_eq_node(cdk::eq_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  node->left()->accept(this, lvl);
  if ((node->type()->name()  == basic_type::TYPE_DOUBLE) && (node->left()->type()->name() == basic_type::TYPE_INT))
    _pf.I2D(); 
    
  node->right()->accept(this, lvl);
  if ((node->type()->name()  == basic_type::TYPE_DOUBLE) && (node->right()->type()->name() == basic_type::TYPE_INT))
    _pf.I2D(); 
    
  if(node->type()->name()  == basic_type::TYPE_DOUBLE) {
    _pf.DCMP();
    _pf.INT(0);
  }     
  _pf.EQ();
}

//---------------------------------------------------------------------------

void ook::postfix_writer::do_identifier_node(cdk::identifier_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  // simplified generation: all variables are global
  _pf.ADDR(node->name());
}

void ook::postfix_writer::do_rvalue_node(cdk::rvalue_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  node->lvalue()->accept(this, lvl);
  if (node->lvalue()->type()->name() == basic_type::TYPE_DOUBLE) {
    _pf.DLOAD();
  }
  else {
    _pf.LOAD(); // depends on type size
  }
}

void ook::postfix_writer::do_assignment_node(cdk::assignment_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  node->rvalue()->accept(this, lvl); // determine the new value
  _pf.DUP();

  if (new_symbol() == nullptr) {
    node->lvalue()->accept(this, lvl); // where to store the value
  } else {
    _pf.DATA(); // variables are all global and live in DATA
    _pf.ALIGN(); // make sure we are aligned
    _pf.LABEL(new_symbol()->name()); // name variable location
    _pf.CONST(0); // initialize it to 0 (zero)
    _pf.TEXT(); // return to the TEXT segment
    node->lvalue()->accept(this, lvl);  //DAVID: bah!
  }
  if((node->lvalue()->type()->name() != basic_type::TYPE_DOUBLE)) 
    _pf.STORE();  // store the value at address
  else
    _pf.DSTORE();
}

//---------------------------------------------------------------------------

/*void ook::postfix_writer::do_program_node(ook::program_node * const node, int lvl) {
  // The ProgramNode (representing the whole program) is the
  // main function node.

  // generate the main function (RTS mandates that its name be "_main")
  _pf.TEXT();
  _pf.ALIGN();
  _pf.GLOBAL("_main", _pf.FUNC());
  _pf.LABEL("_main");
  _pf.ENTER(0);  // OOK doesn't implement local variables

  node->statements()->accept(this, lvl);

  // end the main function
  _pf.INT(0);
  _pf.POP();
  _pf.LEAVE();
  _pf.RET();

  // these are just a few library function imports
  _pf.EXTERN("readi");
  _pf.EXTERN("printi");
  _pf.EXTERN("prints");
  _pf.EXTERN("println");
}*/

//---------------------------------------------------------------------------

void ook::postfix_writer::do_evaluation_node(ook::evaluation_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  node->argument()->accept(this, lvl); // determine the value
  if (node->argument()->type()->name() == basic_type::TYPE_INT) {
    _pf.TRASH(4); // delete the evaluated value
  } else if (node->argument()->type()->name() == basic_type::TYPE_STRING) {
    _pf.TRASH(4); // delete the evaluated value's address
  } else if (node->argument()->type()->name() == basic_type::TYPE_DOUBLE) {
    _pf.TRASH(8); // delete the evaluated value's address
  } else if (node->argument()->type()->name() == basic_type::TYPE_POINTER){
      _pf.TRASH(4); 
  } else if (node->argument()->type()->name() == basic_type::TYPE_VOID){}
  else {
    std::cerr << "ERROR: CANNOT HAPPEN!" << std::endl;
    exit(1);
  } 
}

void ook::postfix_writer::do_print_node(ook::print_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  node->argument()->accept(this, lvl); // determine the value to print

  if (node->argument()->type()->name() == basic_type::TYPE_INT) {
    _pf.CALL("printi");
    _pf.TRASH(4); // delete the printed value
  } else if (node->argument()->type()->name() == basic_type::TYPE_STRING) {
    _pf.CALL("prints");
    _pf.TRASH(4); // delete the printed value's address
  } else if (node->argument()->type()->name() == basic_type::TYPE_DOUBLE) {
    _pf.CALL("printd");
    _pf.TRASH(8); 
  } else {
    std::cerr << "ERROR: CANNOT HAPPEN!" << std::endl;
    exit(1);
  }
  if(node->is_newline())
    _pf.CALL("println"); // print a newline
}

//---------------------------------------------------------------------------

void ook::postfix_writer::do_read_node(ook::read_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  
  if (node->type()->name() == basic_type::TYPE_INT){
    _pf.CALL("readi");
    _pf.PUSH();
  }
  else if (node->type()->name() == basic_type::TYPE_DOUBLE) {
    _pf.CALL("readd");
    _pf.DPUSH();
  }

}

//---------------------------------------------------------------------------

void ook::postfix_writer::do_while_node(ook::while_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  std::string lbl1 = mklbl(++_lbl);  //test condition
  std::string lbl2 = mklbl(++_lbl);  //end iteration

  _next_lbls.push_back(lbl1);
  _stop_lbls.push_back(lbl2);

  _pf.LABEL(lbl1);
  node->condition()->accept(this, lvl);
  _pf.JZ(lbl2);
  node->block()->accept(this, lvl + 2);
  _pf.JMP(lbl1);
  _pf.LABEL(lbl2);

  _next_lbls.pop_back();
  _stop_lbls.pop_back();
}

//---------------------------------------------------------------------------

void ook::postfix_writer::do_if_node(ook::if_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  int lbl1;
  node->condition()->accept(this, lvl);
  _pf.JZ(mklbl(lbl1 = ++_lbl));
  node->block()->accept(this, lvl + 2);
  _pf.LABEL(mklbl(lbl1));
}

//---------------------------------------------------------------------------

void ook::postfix_writer::do_if_else_node(ook::if_else_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  int lbl1, lbl2;
  node->condition()->accept(this, lvl);
  _pf.JZ(mklbl(lbl1 = ++_lbl));
  node->thenblock()->accept(this, lvl + 2);
  _pf.JMP(mklbl(lbl2 = ++_lbl));
  _pf.LABEL(mklbl(lbl1));
  node->elseblock()->accept(this, lvl + 2);
  _pf.LABEL(mklbl(lbl1 = lbl2));
}


/*
*****************************************************************************************
----------------------------------- NEW NODES--------------------------------------------
*****************************************************************************************
*/

void ook::postfix_writer::do_block_node(ook::block_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  _symtab.push();
  if (node->declarations())  
    node->declarations()->accept(this, lvl);
  if (node->instructions())  
    node->instructions()->accept(this, lvl);
  _symtab.pop();
}

void ook::postfix_writer::do_function_call_node(ook::function_call_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS

  std::shared_ptr<ook::symbol> symbol = _symtab.find(node->identifier());
  std::vector <basic_type*> arguments = symbol->arguments();
  int trash = 0;
  
  if(node->arguments()->size() > 0){
    for(int i = (node->arguments()->size()) - 1; i >= 0; i--) {    //Coloca os argumentos na pilha, por ordem inversa
      node->arguments()->node(i)->accept(this, lvl);
      trash += arguments[i]->size();
    }
  }

  if(node->identifier() == "ook")
     _pf.CALL("_main");
  else if(node->identifier() == "_main")
    _pf.CALL("ook");
  else
    _pf.CALL(node->identifier());    

  _pf.TRASH(trash);    

  if(node->type()->name() != basic_type::TYPE_VOID){
    if (node->type()->name() == basic_type::TYPE_DOUBLE)
      _pf.DPUSH();
    else
      _pf.PUSH();  
  }
}

void ook::postfix_writer::do_function_declaration_node(ook::function_declaration_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS
}

void ook::postfix_writer::do_function_definition_node(ook::function_definition_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  ook::counter_size * cs = new counter_size(_compiler,node);

  std::shared_ptr<ook::symbol> symbol = _symtab.find(node->identifier());
  if (!symbol){
    std::vector<basic_type*> arguments;
    if(node->variables()){
      for (size_t i = 0; i < node->variables()->size(); i++) {
        ook::variable_declaration_node *argument = (ook::variable_declaration_node*) node->variables()->node(i);
        arguments.push_back(argument->type());
      }
    }
    symbol = std::make_shared<ook::symbol> (new basic_type(node->type()->size(), node->type()->name()), node->identifier(), arguments, true, true, 0);
    _symtab.insert(node->identifier(), symbol);
  }

  _symtab.push();

  // generate the main function (RTS mandates that its name be "_main")
  _pf.TEXT();
  _pf.ALIGN();

  if (node->identifier()=="ook"){
    if (node->ispublic())
      _pf.GLOBAL("_main", _pf.FUNC());
    _pf.LABEL("_main");
  } else if (node->identifier()=="main"){
    if (node->ispublic())
      _pf.GLOBAL("ook", _pf.FUNC());
    _pf.LABEL("ook");
  } else {
    if (node->ispublic())
      _pf.GLOBAL(node->identifier(), _pf.FUNC());
    _pf.LABEL(node->identifier());
  } 

  _pf.ENTER(cs->size() + node->type()->size());

  if (node->literal() != nullptr) {
    node->literal()->accept(this, lvl);
    if ((node->type()->name() == basic_type::TYPE_DOUBLE) && (node->literal()->type()->name() == basic_type::TYPE_INT)) {
      _pf.I2D();
    } else if (node->type()->name() == basic_type::TYPE_INT && (node->literal()->type()->name() == basic_type::TYPE_DOUBLE)) {
      _pf.D2I();
    }  

    _pf.LOCAL(- cs->size() - node->type()->size());

    if (node->type()->name() == basic_type::TYPE_DOUBLE) 
      _pf.DSTORE();
    else
      _pf.STORE();
  } else if (node->type()->name() == basic_type::TYPE_INT) {
    _pf.INT(0);
    _pf.LOCA( -cs->size() - node->type()->size());
  }

  _inside_function = true;
  node->body()->accept(this, lvl);
  _inside_function = false;
  
  if (node->type()->name() != basic_type::TYPE_VOID) {
    _pf.LOCAL(-cs->size() - node->type()->size());
    if (node->type()->name() == basic_type::TYPE_DOUBLE) {
      _pf.DLOAD();
      _pf.DPOP();
    } else {
      _pf.LOAD();
      _pf.POP();
    }
  }

  _symtab.pop();

  // end the main function
  _pf.LEAVE();
  _pf.RET();

  // these are just a few library function imports
  /*_pf.EXTERN("readi");
  _pf.EXTERN("readd");
  _pf.EXTERN("printi");
  _pf.EXTERN("printd");
  _pf.EXTERN("prints");
  _pf.EXTERN("println");*/
}

void ook::postfix_writer::do_identity_node(ook::identity_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->argument()->accept(this, lvl);
}

void ook::postfix_writer::do_lvalue_index_node(ook::lvalue_index_node * const node, int lvl) {
  node->index()->accept(this,lvl);
  _pf.INT(node->type()->size());
  _pf.MUL();
  node->address()->accept(this,lvl);
  _pf.ADD();
}

void ook::postfix_writer::do_memory_address_node(ook::memory_address_node * const node, int lvl) {
  node->argument()->accept(this,lvl);
}

void ook::postfix_writer::do_memory_alloc_node(ook::memory_alloc_node * const node, int lvl) {
  node->argument()->accept(this,lvl);
  _pf.INT(node->type()->size());
  _pf.MUL();
  _pf.ALLOC();
  _pf.SP();
}

void ook::postfix_writer::do_next_node(ook::next_node * const node, int lvl) {
  int next_size = _next_lbls.size();

  if(node->n() > next_size)
    throw "Impossible ask of next instruction";
  else if (_next_lbls.empty())
    throw "not inside while cycle";

  _pf.JMP(_next_lbls[next_size - node->n()]);
}

void ook::postfix_writer::do_null_node(ook::null_node * const node, int lvl) { 
  _pf.INT(0);
}

void ook::postfix_writer::do_return_node(ook::return_node * const node, int lvl) {

}

void ook::postfix_writer::do_stop_node(ook::stop_node * const node, int lvl) { 
  int stop_size = _stop_lbls.size();

  if(node->n() > stop_size)
    throw "Impossible ask of stop instruction";
  else if (_stop_lbls.empty())
    throw "not inside while cycle";

  _pf.JMP(_stop_lbls[stop_size - node->n()]);
}

void ook::postfix_writer::do_variable_declaration_node(ook::variable_declaration_node * const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  std::shared_ptr<ook::symbol> symbol = std::make_shared<ook::symbol> (new basic_type(node->type()->size(), node->type()->name()), node->identifier(), 0);
  _symtab.insert(node->identifier(), symbol);

  _variable_declaration = true;
  //if (!_inside_function){    //GLOBAL
    if (node->value() == nullptr) 
      _pf.BSS();
    else if(node->type()->name() != basic_type::TYPE_STRING)
      _pf.DATA();
    else
      _pf.RODATA();
    _lbl_var = node->identifier();
    _pf.ALIGN();
    if (node->ispublic())
      _pf.GLOBAL(node->identifier(), _pf.OBJ());
    _pf.LABEL(node->identifier());
    if (node->value() == nullptr)
      _pf.BYTE(node->type()->size()); 
    else
      node->value()->accept(this, lvl);
    _pf.TEXT();
    _pf.ALIGN();
  //}          //LOCAL 
  _variable_declaration = false;
}