#include <string>
#include "targets/xml_writer.h"
#include "targets/type_checker.h"
#include "ast/all.h"  // automatically generated


//---------------------------------------------------------------------------

inline std::string getType(basic_type *type){
    switch(type->name()){
        case basic_type::TYPE_INT:              return "int"; break;
        case basic_type::TYPE_DOUBLE:           return "float"; break;
        case basic_type::TYPE_STRING:           return "string"; break;
        case basic_type::TYPE_POINTER:          return "pointer"; break;
        case basic_type::TYPE_VOID:             return "void"; break;
        default:                                return " ";
    }
}
//---------------------------------------------------------------------------

void ook::xml_writer::do_sequence_node(cdk::sequence_node * const node, int lvl) {
  os() << std::string(lvl, ' ') << "<sequence_node size='" << node->size() << "'>" << std::endl;
  for (size_t i = 0; i < node->size(); i++)
    node->node(i)->accept(this, lvl + 2);
  closeTag(node, lvl);
}

//---------------------------------------------------------------------------

void ook::xml_writer::do_integer_node(cdk::integer_node * const node, int lvl) {
  process_literal(node, lvl);
}

void ook::xml_writer::do_string_node(cdk::string_node * const node, int lvl) {
  process_literal(node, lvl);
}

//---------------------------------------------------------------------------

inline void ook::xml_writer::do_unary_expression(cdk::unary_expression_node * const node, int lvl) {
  //ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  node->argument()->accept(this, lvl + 2);
  closeTag(node, lvl);
}

void ook::xml_writer::do_neg_node(cdk::neg_node * const node, int lvl) {
  do_unary_expression(node, lvl);
}
void ook::xml_writer::do_not_node(cdk::not_node * const node, int lvl) {
  do_unary_expression(node, lvl);
}
//---------------------------------------------------------------------------

inline void ook::xml_writer::do_binary_expression(cdk::binary_expression_node * const node, int lvl) {
  //ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  node->left()->accept(this, lvl + 2);
  node->right()->accept(this, lvl + 2);
  closeTag(node, lvl);
}
void ook::xml_writer::do_and_node(cdk::and_node * const node, int lvl) {
  do_binary_expression(node, lvl);
}
void ook::xml_writer::do_or_node(cdk::or_node * const node, int lvl) {
  do_binary_expression(node, lvl);
}
void ook::xml_writer::do_add_node(cdk::add_node * const node, int lvl) {
  do_binary_expression(node, lvl);
}
void ook::xml_writer::do_sub_node(cdk::sub_node * const node, int lvl) {
  do_binary_expression(node, lvl);
}
void ook::xml_writer::do_mul_node(cdk::mul_node * const node, int lvl) {
  do_binary_expression(node, lvl);
}
void ook::xml_writer::do_div_node(cdk::div_node * const node, int lvl) {
  do_binary_expression(node, lvl);
}
void ook::xml_writer::do_mod_node(cdk::mod_node * const node, int lvl) {
  do_binary_expression(node, lvl);
}
void ook::xml_writer::do_lt_node(cdk::lt_node * const node, int lvl) {
  do_binary_expression(node, lvl);
}
void ook::xml_writer::do_le_node(cdk::le_node * const node, int lvl) {
  do_binary_expression(node, lvl);
}
void ook::xml_writer::do_ge_node(cdk::ge_node * const node, int lvl) {
  do_binary_expression(node, lvl);
}
void ook::xml_writer::do_gt_node(cdk::gt_node * const node, int lvl) {
  do_binary_expression(node, lvl);
}
void ook::xml_writer::do_ne_node(cdk::ne_node * const node, int lvl) {
  do_binary_expression(node, lvl);
}
void ook::xml_writer::do_eq_node(cdk::eq_node * const node, int lvl) {
  do_binary_expression(node, lvl);
}

//---------------------------------------------------------------------------

void ook::xml_writer::do_identifier_node(cdk::identifier_node * const node, int lvl) {
  //ASSERT_SAFE_EXPRESSIONS;
  os() << std::string(lvl, ' ') << "<" << node->label() << ">" << node->name() << "</" << node->label() << ">" << std::endl;
}

void ook::xml_writer::do_rvalue_node(cdk::rvalue_node * const node, int lvl) {
  //ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
    node->lvalue()->accept(this, lvl + 4);
  closeTag(node, lvl);
}

void ook::xml_writer::do_assignment_node(cdk::assignment_node * const node, int lvl) {
  //ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);

  node->lvalue()->accept(this, lvl);
  reset_new_symbol();

  node->rvalue()->accept(this, lvl + 4);
  closeTag(node, lvl);
}

//---------------------------------------------------------------------------

/*void ook::xml_writer::do_program_node(ook::program_node * const node, int lvl) {
  openTag(node, lvl);
  node->statements()->accept(this, lvl + 4);
  closeTag(node, lvl);
}*/

//---------------------------------------------------------------------------

void ook::xml_writer::do_evaluation_node(ook::evaluation_node * const node, int lvl) {
  //ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  node->argument()->accept(this, lvl + 2);
  closeTag(node, lvl);
}

void ook::xml_writer::do_print_node(ook::print_node * const node, int lvl) {
  //ASSERT_SAFE_EXPRESSIONS;
  if (node->is_newline())
    openTag("println", lvl);
  else
    openTag(node, lvl);    
  node->argument()->accept(this, lvl + 2);
  closeTag(node, lvl);
}

//---------------------------------------------------------------------------

void ook::xml_writer::do_read_node(ook::read_node * const node, int lvl) {
  //ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  //node->argument()->accept(this, lvl + 2);
  closeTag(node, lvl);
}

//---------------------------------------------------------------------------

void ook::xml_writer::do_while_node(ook::while_node * const node, int lvl) {
  //ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  openTag("condition", lvl + 2);
  node->condition()->accept(this, lvl + 4);
  closeTag("condition", lvl + 2);
  openTag("block", lvl + 2);
  node->block()->accept(this, lvl + 4);
  closeTag("block", lvl + 2);
  closeTag(node, lvl);
}

//---------------------------------------------------------------------------

void ook::xml_writer::do_if_node(ook::if_node * const node, int lvl) {
  //ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  openTag("condition", lvl + 2);
  node->condition()->accept(this, lvl + 4);
  closeTag("condition", lvl + 2);
  openTag("then", lvl + 2);
  node->block()->accept(this, lvl + 4);
  closeTag("then", lvl + 2);
  closeTag(node, lvl);
}

void ook::xml_writer::do_if_else_node(ook::if_else_node * const node, int lvl) {
  //ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  openTag("condition", lvl + 2);
  node->condition()->accept(this, lvl + 4);
  closeTag("condition", lvl + 2);
  openTag("then", lvl + 2);
  node->thenblock()->accept(this, lvl + 4);
  closeTag("then", lvl + 2);
  openTag("else", lvl + 2);
  node->elseblock()->accept(this, lvl + 4);
  closeTag("else", lvl + 2);
  closeTag(node, lvl);
}

/*
*****************************************************************************************
----------------------------------- NEW NODES--------------------------------------------
*****************************************************************************************
*/

void ook::xml_writer::do_block_node(ook::block_node * const node, int lvl) {
  openTag(node, lvl);
  if(node->declarations() != nullptr) {
    openTag("declarations", lvl+2);
      node->declarations()->accept(this, lvl+4);
    closeTag("declarations", lvl+2);
  }
  if(node->instructions() != nullptr) {
    openTag("instructions", lvl+2);
      node->instructions()->accept(this, lvl+4);
    closeTag("instructions", lvl+2);
  }
  closeTag(node, lvl);
}

void ook::xml_writer::do_function_call_node(ook::function_call_node * const node, int lvl) {
  os() << std::string(lvl, ' ') << "<function_call_node identifier='" << node->identifier() << "'>" << std::endl;
  if(node->arguments() != nullptr) {
    openTag("arguments", lvl+2);
      node ->arguments()->accept(this, lvl+4);
    closeTag("arguments", lvl+2);
  }
  closeTag(node,lvl);
}

void ook::xml_writer::do_function_declaration_node(ook::function_declaration_node * const node, int lvl) {
  if (node->ispublic()) {
    os() << std::string(lvl, ' ') << "<function_declaration_node qualifier='public' " \
             << "type='" << getType(node->type()) << "' " \
             << "identifier='" << node->identifier() << "'>" \
             << std::endl;
    }
  else if (node->imported()) {
    os() << std::string(lvl, ' ') << "<function_declaration_node qualifier='imported' " \
         << "type='" << getType(node->type()) << "' " \
         << "identifier='" << node->identifier() << "'>" \
         << std::endl;
  }
  else{
    os() << std::string(lvl, ' ') << "<function_declaration_node qualifier='private' " \
         << "type='" << getType(node->type()) << "' " \
         << "identifier='" << node->identifier() << "'>" \
         << std::endl;
  }
  if(node->variables() != nullptr) {
    openTag("variables_fdec", lvl+2);
      node->variables()->accept(this, lvl+4);
    closeTag("variables_fdec", lvl+2);
  }
  closeTag(node, lvl);
}

void ook::xml_writer::do_function_definition_node(ook::function_definition_node * const node, int lvl) {
  if(node->ispublic()){
    os() << std::string(lvl, ' ') << "<function_definition_node qualifier='public' " \
         << "type='" << getType(node->type()) << "' " \
         << "identifier='" << node->identifier() << "'>" \
         << std::endl;
  }
  else{
    os() << std::string(lvl, ' ') << "<function_definition_node qualifier='private' " \
         << "type='" << getType(node->type()) << "' " \
         << "identifier='" << node->identifier() << "'>" \
         << std::endl;
  }
  if(node->variables() != nullptr) {
    openTag("variables_fdef", lvl+2);
      node->variables()->accept(this, lvl+4);
    closeTag("variables_fdef", lvl+2);
  }
  if(node->literal() != nullptr) {
    openTag("literal", lvl+2);
      node->literal()->accept(this, lvl+4);
    closeTag("literal", lvl+2);
  }
  openTag("body", lvl+2);
    node->body()->accept(this, lvl+4);
  closeTag("body", lvl+2);
  closeTag(node, lvl);
}

void ook::xml_writer::do_identity_node(ook::identity_node * const node, int lvl) {
  openTag(node, lvl);
    node->argument()->accept(this, lvl);
  closeTag(node, lvl);
}

void ook::xml_writer::do_lvalue_index_node(ook::lvalue_index_node * const node, int lvl) {
  openTag(node, lvl);
    openTag("index", lvl + 2);
      node->index()->accept(this, lvl + 4);
    closeTag("index", lvl + 2);
    openTag("address", lvl + 2);
      node->address()->accept(this, lvl + 4);
    closeTag("address", lvl + 2);
  closeTag(node, lvl);
}

void ook::xml_writer::do_memory_address_node(ook::memory_address_node * const node, int lvl) {
  openTag(node, lvl);
    node->argument()->accept(this, lvl);
  closeTag(node, lvl);
}

void ook::xml_writer::do_memory_alloc_node(ook::memory_alloc_node * const node, int lvl) {
  openTag(node, lvl);
    node->argument()->accept(this, lvl);
  closeTag(node, lvl);
}

void ook::xml_writer::do_next_node(ook::next_node * const node, int lvl) {
  os() << std::string(lvl, ' ') << "<next_node>" << node->n();
  closeTag(node, lvl);
}

void ook::xml_writer::do_null_node(ook::null_node * const node, int lvl) {
  os() << std::string(lvl, ' ') << "<null_node/>" << std::endl;
}

void ook::xml_writer::do_return_node(ook::return_node * const node, int lvl) {
  os() << std::string(lvl, ' ') << "<return_node/>" << std::endl;
}

void ook::xml_writer::do_stop_node(ook::stop_node * const node, int lvl) {
  os() << std::string(lvl, ' ') << "<stop_node>" << node->n();
  closeTag(node, lvl);
}

void ook::xml_writer::do_variable_declaration_node(ook::variable_declaration_node * const node, int lvl) {
 if(node->ispublic()){
    os() << std::string(lvl, ' ') << "<variable_declaration_node qualifier='public' " \
         << "type='" << getType(node->type()) << "' " \
         << "identifier='" << node->identifier() << "'>" << std::endl;
  }
  else if (node->imported()){
    os() << std::string(lvl, ' ') << "<variable_declaration_node qualifier='imported' " \
         << " type='" << getType(node->type()) << "' " \
         << " identifier='" << node->identifier() << "'>"  << std::endl;
  }
  else {
    os() << std::string(lvl, ' ') << "<variable_declaration_node qualifier='private' " \
         << " type='" << getType(node->type()) << "' " \
         << " identifier='" << node->identifier() << "'>"  << std::endl;
  }
  if(node->value() != nullptr) {
    openTag("expression", lvl+2);
      node->value()->accept(this, lvl+4);
    closeTag("expression", lvl+2);
  }
  closeTag(node, lvl);
}