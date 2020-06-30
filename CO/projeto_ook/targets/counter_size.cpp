#include "targets/counter_size.h"
#include "ast/all.h"  // all.h is automatically generated

void ook::counter_size::do_nil_node(cdk::nil_node * const node, int lvl){}
void ook::counter_size::do_data_node(cdk::data_node * const node, int lvl){}

void ook::counter_size::do_integer_node(cdk::integer_node * const node, int lvl){}
void ook::counter_size::do_double_node(cdk::double_node * const node, int lvl){}
void ook::counter_size::do_string_node(cdk::string_node * const node, int lvl){}

void ook::counter_size::do_neg_node(cdk::neg_node * const node, int lvl){}
void ook::counter_size::do_not_node(cdk::not_node * const node, int lvl){}

void ook::counter_size::do_add_node(cdk::add_node * const node, int lvl){}
void ook::counter_size::do_sub_node(cdk::sub_node * const node, int lvl){}
void ook::counter_size::do_mul_node(cdk::mul_node * const node, int lvl){}
void ook::counter_size::do_div_node(cdk::div_node * const node, int lvl){}
void ook::counter_size::do_mod_node(cdk::mod_node * const node, int lvl){}
void ook::counter_size::do_lt_node(cdk::lt_node * const node, int lvl){}
void ook::counter_size::do_le_node(cdk::le_node * const node, int lvl){}
void ook::counter_size::do_ge_node(cdk::ge_node * const node, int lvl){}
void ook::counter_size::do_gt_node(cdk::gt_node * const node, int lvl){}
void ook::counter_size::do_ne_node(cdk::ne_node * const node, int lvl){}
void ook::counter_size::do_eq_node(cdk::eq_node * const node, int lvl){}
void ook::counter_size::do_and_node(cdk::and_node * const node, int lvl){}
void ook::counter_size::do_or_node(cdk::or_node * const node, int lvl){}

void ook::counter_size::do_identifier_node(cdk::identifier_node * const node, int lvl){}
void ook::counter_size::do_rvalue_node(cdk::rvalue_node * const node, int lvl){}
void ook::counter_size::do_assignment_node(cdk::assignment_node * const node, int lvl){}

void ook::counter_size::do_evaluation_node(ook::evaluation_node * const node, int lvl){}
void ook::counter_size::do_print_node(ook::print_node * const node, int lvl){}
void ook::counter_size::do_read_node(ook::read_node * const node, int lvl){}

void ook::counter_size::do_while_node(ook::while_node * const node, int lvl){}
void ook::counter_size::do_if_node(ook::if_node * const node, int lvl){}
void ook::counter_size::do_if_else_node(ook::if_else_node * const node, int lvl){}

void ook::counter_size::do_stop_node(ook::stop_node * const node, int lvl){}
void ook::counter_size::do_return_node(ook::return_node * const node, int lvl){}
void ook::counter_size::do_next_node(ook::next_node * const node, int lvl){}
void ook::counter_size::do_function_declaration_node(ook::function_declaration_node * const node, int lvl){}
void ook::counter_size::do_null_node(ook::null_node * const node, int lvl){}
void ook::counter_size::do_memory_alloc_node(ook::memory_alloc_node * const node, int lvl){}
void ook::counter_size::do_lvalue_index_node(ook::lvalue_index_node * const node, int lvl){}
void ook::counter_size::do_memory_address_node(ook::memory_address_node * const node, int lvl){}
void ook::counter_size::do_identity_node(ook::identity_node * const node, int lvl){}


void ook::counter_size::do_block_node(ook::block_node * const node, int lvl) {
  if (node->instructions())
    node->instructions()->accept(this, lvl);
  if (node->declarations())
    node->declarations()->accept(this, lvl);
}

void ook::counter_size::do_function_definition_node(ook::function_definition_node * const node, int lvl) {
  if(node->type()->name() != basic_type::TYPE_VOID)
    _size += node->type()->size();
  node->body()->accept(this, lvl);
}

void ook::counter_size::do_sequence_node(cdk::sequence_node * const node, int lvl) {
  for (cdk::basic_node * n : node->nodes())
    n->accept(this, lvl);
}

void ook::counter_size::do_function_call_node(ook::function_call_node * const node, int lvl) {
  if (node->arguments())  
    node->arguments()->accept(this, lvl);
}

void ook::counter_size::do_variable_declaration_node(ook::variable_declaration_node * const node, int lvl) {
  _size += node->type()->size();
}