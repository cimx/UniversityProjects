#include <string>
#include "targets/type_checker.h"
#include "targets/symbol.h"
#include "ast/all.h"  // automatically generated

#define ASSERT_UNSPEC \
    { if (node->type() != nullptr && \
          node->type()->name() != basic_type::TYPE_UNSPEC) return; }

//---------------------------------------------------------------------------

void ook::type_checker::do_integer_node(cdk::integer_node * const node, int lvl) {
  ASSERT_UNSPEC;
  node->type(new basic_type(4, basic_type::TYPE_INT));
}
void ook::type_checker::do_double_node(cdk::double_node * const node, int lvl) {
  ASSERT_UNSPEC;
  node->type(new basic_type(8, basic_type::TYPE_DOUBLE));
}
void ook::type_checker::do_string_node(cdk::string_node * const node, int lvl) {
  ASSERT_UNSPEC;
  node->type(new basic_type(4, basic_type::TYPE_STRING));
}

//---------------------------------------------------------------------------

inline void ook::type_checker::processUnaryExpression(cdk::unary_expression_node * const node, int lvl) {
  ASSERT_UNSPEC;

  node->argument()->accept(this, lvl + 2);

  if ((node->argument()->type()->name() != basic_type::TYPE_INT) && (node->argument()->type()->name() != basic_type::TYPE_DOUBLE))
    throw std::string("wrong type in argument of unary expression");

  // in OOK, expressions are always int or real
  if (node->argument()->type()->name() == basic_type::TYPE_INT)  
    node->type(new basic_type(4, basic_type::TYPE_INT));
  if (node->argument()->type()->name() == basic_type::TYPE_DOUBLE)  
    node->type(new basic_type(4, basic_type::TYPE_DOUBLE));
}

void ook::type_checker::do_neg_node(cdk::neg_node * const node, int lvl) {
  processUnaryExpression(node, lvl);
}
void ook::type_checker::do_not_node(cdk::not_node * const node, int lvl) {
  processUnaryExpression(node, lvl);
}

//---------------------------------------------------------------------------

inline void ook::type_checker::processBinaryExpression(cdk::binary_expression_node * const node, int lvl) {
  ASSERT_UNSPEC;
  node->left()->accept(this, lvl + 2);
  if (node->left()->type()->name() != basic_type::TYPE_INT)
    throw std::string("wrong type in left argument of binary expression");

  node->right()->accept(this, lvl + 2);
  if (node->right()->type()->name() != basic_type::TYPE_INT)
    throw std::string("wrong type in right argument of binary expression");

  // in OOK, expressions are always int
  node->type(new basic_type(4, basic_type::TYPE_INT));
}

inline void ook::type_checker::processBinaryExpressionWithDouble(cdk::binary_expression_node * const node, int lvl) {
  ASSERT_UNSPEC;

  node->left()->accept(this, lvl + 2);
  if ((node->left()->type()->name() != basic_type::TYPE_INT) && (node->left()->type()->name() != basic_type::TYPE_DOUBLE))
    throw std::string("wrong type in left argument of binary expression");
  
  node->right()->accept(this, lvl + 2);
  if ((node->right()->type()->name() != basic_type::TYPE_INT) && (node->right()->type()->name() != basic_type::TYPE_DOUBLE))
    throw std::string("wrong type in right argument of binary expression");

  if((node->left()->type()->name() != basic_type::TYPE_INT) && (node->right()->type()->name() != basic_type::TYPE_DOUBLE))
    node->type(new basic_type(8, basic_type::TYPE_DOUBLE));
  else 
    node->type(new basic_type(4, basic_type::TYPE_INT));
}

void ook::type_checker::do_and_node(cdk::and_node * const node, int lvl) {
  processBinaryExpression(node, lvl);
}
void ook::type_checker::do_or_node(cdk::or_node * const node, int lvl) {
  processBinaryExpression(node, lvl);
}
void ook::type_checker::do_mod_node(cdk::mod_node * const node, int lvl) {
  processBinaryExpression(node, lvl);
}

void ook::type_checker::do_add_node(cdk::add_node * const node, int lvl) {
  //processBinaryExpression(node, lvl);
  processBinaryExpressionWithDouble(node, lvl);
}
void ook::type_checker::do_sub_node(cdk::sub_node * const node, int lvl) {
  //processBinaryExpression(node, lvl);
  processBinaryExpressionWithDouble(node, lvl);
}
void ook::type_checker::do_mul_node(cdk::mul_node * const node, int lvl) {
  //processBinaryExpression(node, lvl);
  processBinaryExpressionWithDouble(node, lvl);
}
void ook::type_checker::do_div_node(cdk::div_node * const node, int lvl) {
  //processBinaryExpression(node, lvl);
  processBinaryExpressionWithDouble(node, lvl);
}
void ook::type_checker::do_lt_node(cdk::lt_node * const node, int lvl) {
  //processBinaryExpression(node, lvl);
  processBinaryExpressionWithDouble(node, lvl);
}
void ook::type_checker::do_le_node(cdk::le_node * const node, int lvl) {
  //processBinaryExpression(node, lvl);
  processBinaryExpressionWithDouble(node, lvl);
}
void ook::type_checker::do_ge_node(cdk::ge_node * const node, int lvl) {
  //processBinaryExpression(node, lvl);
  processBinaryExpressionWithDouble(node, lvl);
}
void ook::type_checker::do_gt_node(cdk::gt_node * const node, int lvl) {
  //processBinaryExpression(node, lvl);
  processBinaryExpressionWithDouble(node, lvl);
}
void ook::type_checker::do_ne_node(cdk::ne_node * const node, int lvl) {
  //processBinaryExpression(node, lvl);
  processBinaryExpressionWithDouble(node, lvl);
}
void ook::type_checker::do_eq_node(cdk::eq_node * const node, int lvl) {
  //processBinaryExpression(node, lvl);
  processBinaryExpressionWithDouble(node, lvl);
}

//---------------------------------------------------------------------------

void ook::type_checker::do_identifier_node(cdk::identifier_node * const node, int lvl) {
  ASSERT_UNSPEC;
  const std::string &id = node->name();
  std::shared_ptr<ook::symbol> symbol = _symtab.find(id);

  if (symbol != nullptr) {
    node->type(symbol->type());
  }
  else {
    throw id;
  }
}

void ook::type_checker::do_rvalue_node(cdk::rvalue_node * const node, int lvl) {
  ASSERT_UNSPEC;
  try {
    node->lvalue()->accept(this, lvl);
    node->type(node->lvalue()->type());
  } catch (const std::string &id) {
    throw std::string("undeclared variable '" + id + "'");
  }
}

void ook::type_checker::do_assignment_node(cdk::assignment_node * const node, int lvl) {
  ASSERT_UNSPEC;

  try {
    node->lvalue()->accept(this, lvl);
  } catch (const std::string &id) {
    std::shared_ptr<ook::symbol> symbol = std::make_shared<ook::symbol>(new basic_type(4, basic_type::TYPE_INT), id, 0);
    _symtab.insert(id, symbol);
    _parent->set_new_symbol(symbol);  // advise parent that a symbol has been inserted
    node->lvalue()->accept(this, lvl);  //DAVID: bah!
  }

  if ((node->lvalue()->type()->name() != basic_type::TYPE_INT) &&
      (node->lvalue()->type()->name() != basic_type::TYPE_DOUBLE) &&
      (node->lvalue()->type()->name() != basic_type::TYPE_STRING) &&
      (node->lvalue()->type()->name() != basic_type::TYPE_POINTER))
    throw std::string("wrong type in left argument of assignment expression");

  node->rvalue()->accept(this, lvl + 2);
  if ((node->rvalue()->type()->name() != basic_type::TYPE_INT) &&
      (node->rvalue()->type()->name() != basic_type::TYPE_DOUBLE) &&
      (node->rvalue()->type()->name() != basic_type::TYPE_STRING) &&
      (node->rvalue()->type()->name() != basic_type::TYPE_POINTER))
    throw std::string("wrong type in right argument of assignment expression");

  if (node->rvalue()->type()->name() == basic_type::TYPE_INT)
    node->type(new basic_type(4, basic_type::TYPE_INT));
  else if (node->rvalue()->type()->name() == basic_type::TYPE_DOUBLE)
    node->type(new basic_type(8, basic_type::TYPE_DOUBLE));
  else if (node->rvalue()->type()->name() == basic_type::TYPE_STRING)
    node->type(new basic_type(4, basic_type::TYPE_STRING));
  else if (node->rvalue()->type()->name() == basic_type::TYPE_POINTER)
    node->type(new basic_type(4, basic_type::TYPE_POINTER));
}

//---------------------------------------------------------------------------

void ook::type_checker::do_evaluation_node(ook::evaluation_node * const node, int lvl) {
  node->argument()->accept(this, lvl + 2);
}

void ook::type_checker::do_print_node(ook::print_node * const node, int lvl) {
  node->argument()->accept(this, lvl + 2);

  if (node->argument()->type()->name() == basic_type::TYPE_POINTER) 
    throw std::string("unuble to print pointer");
  if((node->argument()->type()->name() != basic_type::TYPE_INT) && 
     (node->argument()->type()->name() != basic_type::TYPE_DOUBLE) &&
     (node->argument()->type()->name() != basic_type::TYPE_STRING))
    throw std::string("wrong type to print");
  
}

//---------------------------------------------------------------------------

void ook::type_checker::do_read_node(ook::read_node * const node, int lvl) {
  ASSERT_UNSPEC;
  /*try {
    //node->argument()->accept(this, lvl);
  } catch (const std::string &id) {
    throw "undeclared variable '" + id + "'";
  }*/
}

//---------------------------------------------------------------------------

void ook::type_checker::do_while_node(ook::while_node * const node, int lvl) {
  node->condition()->accept(this, lvl + 4);
}

//---------------------------------------------------------------------------

void ook::type_checker::do_if_node(ook::if_node * const node, int lvl) {
  node->condition()->accept(this, lvl + 4);
}

void ook::type_checker::do_if_else_node(ook::if_else_node * const node, int lvl) {
  node->condition()->accept(this, lvl + 4);
}

/*
*****************************************************************************************
----------------------------------- NEW NODES--------------------------------------------
*****************************************************************************************
*/

void ook::type_checker::do_block_node(ook::block_node * const node, int lvl) {}

void ook::type_checker::do_function_call_node(ook::function_call_node * const node, int lvl) {
  ASSERT_UNSPEC;

  const std::string &id = node->identifier();
  std::shared_ptr<ook::symbol> symbol = _symtab.find(id);

  if(symbol && symbol->function()) {
      const std::vector<basic_type*> arguments = symbol->arguments();

      if(node->arguments()) {

          if(arguments.size() != node->arguments()->size()) 
            throw std::string("wrong number of arguments");

          for (size_t i = 0; i < arguments.size()  &&  i < node->arguments()->size(); i++) {
              cdk::expression_node *argument = (cdk::expression_node*) node->arguments()->node(i);
              node->arguments()->node(i)->accept(this, lvl);

              if(argument->type()->name() == basic_type::TYPE_UNSPEC) {
                  if (arguments.at(i)->name() == basic_type::TYPE_INT) {
                    argument->type(new basic_type(4, basic_type::TYPE_INT));
                    continue;
                  }
                  else if(arguments.at(i)->name() == basic_type::TYPE_DOUBLE)  {
                      argument->type(new basic_type(8, basic_type::TYPE_DOUBLE));
                      continue;
                  }
              }
              if (argument->type()->name() != arguments.at(i)->name()) 
                throw std::string("argument types are incorrect");
          }
      }
  }
  else 
    throw std::string("function " + id + " not declared");

  node->type(symbol->type());
}

void ook::type_checker::do_function_declaration_node(ook::function_declaration_node * const node, int lvl) {
  //node->variables()->accept(this, lvl+1);
}

void ook::type_checker::do_function_definition_node(ook::function_definition_node * const node, int lvl) {
  ASSERT_UNSPEC;

  const std::string &id = node->identifier();
  std::vector<basic_type*> arguments;

  std::shared_ptr<ook::symbol> symbol = _symtab.find(id);
  
  if (node->literal()) {
    node->literal()->accept(this, lvl);
    if (node->type()->name() != node->literal()->type()->name())
      throw std::string("incompatible return and function types for function " + id); 
  }

  if(node->variables()){
    node->variables()->accept(this, lvl);
    for (size_t i = 0; i < node->variables()->size(); i++) {
      ook::variable_declaration_node *argument = (ook::variable_declaration_node*) node->variables()->node(i);
      arguments.push_back(argument->type());
    }
  }

  if(node->body())
    node->body()->accept(this, lvl);

  if (symbol) {
    if (!symbol->function())  
      throw std::string(id + " is not a function");
    if (symbol->function_defined()) 
      throw std::string("function " + id + " already defined");
    if(symbol->type()->name() != node->type()->name())
        throw std::string("function " + id + " declared with different return type");
    if(symbol->arguments().size() != arguments.size())
      throw std::string("function " + id + " declared with different arguments");
    for (size_t i = 0; i < symbol->arguments().size(); i++)
      if(symbol->arguments().at(i)->name() != arguments.at(i)->name())
        throw std::string("function " + id + " declared with different arguments types");
    symbol->function_defined(true);
  } 
}

void ook::type_checker::do_identity_node(ook::identity_node * const node, int lvl) {
  processUnaryExpression(node,lvl);
}

void ook::type_checker::do_lvalue_index_node(ook::lvalue_index_node * const node, int lvl) {
  ASSERT_UNSPEC;

  node->index()->accept(this, lvl + 2);

  if (node->index()->type()->name() != basic_type::TYPE_POINTER)  
    throw std::string("wrong type in argument of lvalue index node");

  node->address()->accept(this, lvl + 2);
 
  if (node->address()->type()->name() != basic_type::TYPE_INT)
    throw std::string("wrong type in argument of lvalue index node");
  
  node->type(new basic_type(8, basic_type::TYPE_DOUBLE));
}

void ook::type_checker::do_memory_address_node(ook::memory_address_node * const node, int lvl) {
  ASSERT_UNSPEC;
  node->type(new basic_type(4, basic_type::TYPE_POINTER));
}

void ook::type_checker::do_memory_alloc_node(ook::memory_alloc_node * const node, int lvl) {
  ASSERT_UNSPEC;
  node->type(new basic_type(4, basic_type::TYPE_POINTER));
}

void ook::type_checker::do_next_node(ook::next_node * const node, int lvl) {}
void ook::type_checker::do_null_node(ook::null_node * const node, int lvl) {}
void ook::type_checker::do_return_node(ook::return_node * const node, int lvl) {}
void ook::type_checker::do_stop_node(ook::stop_node * const node, int lvl) {}

void ook::type_checker::do_variable_declaration_node(ook::variable_declaration_node * const node, int lvl) {
  ASSERT_UNSPEC;

  std::shared_ptr<ook::symbol> symbol = _symtab.find(node->identifier());
  
  if (symbol && (symbol->type()->name() != node->type()->name()))
    throw std::string("variable name taken");

  if(node->value())
    node->value()->accept(this, lvl+2);
}