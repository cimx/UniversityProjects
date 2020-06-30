#ifndef __OOK_COUNTER_SIZE__H
#define __OOK_COUNTER_SIZE__H

#include <string>
#include <iostream>
#include <cdk/symbol_table.h>
#include <cdk/ast/basic_node.h>
#include "targets/basic_ast_visitor.h"
#include "targets/symbol.h"

namespace ook {

  class counter_size : public basic_ast_visitor {
    private:
      size_t _size;
    public:
      counter_size(std::shared_ptr<cdk::compiler> compiler, cdk::basic_node * const node) :
        basic_ast_visitor(compiler), _size(0){
            node->accept(this,0);
        }

    public:
      size_t size() {
        return _size;
      }

    public:
    // CDK nodes (general)
    virtual void do_nil_node(cdk::nil_node * const node, int lvl);
    virtual void do_data_node(cdk::data_node * const node, int lvl);
    virtual void do_sequence_node(cdk::sequence_node * const node, int lvl);

    public:
    virtual void do_integer_node(cdk::integer_node * const node, int lvl);
    virtual void do_double_node(cdk::double_node * const node, int lvl);
    virtual void do_string_node(cdk::string_node * const node, int lvl);

    public:
    virtual void do_neg_node(cdk::neg_node * const node, int lvl);
    virtual void do_not_node(cdk::not_node * const node, int lvl);

    public:
    virtual void do_add_node(cdk::add_node * const node, int lvl);
    virtual void do_sub_node(cdk::sub_node * const node, int lvl);
    virtual void do_mul_node(cdk::mul_node * const node, int lvl);
    virtual void do_div_node(cdk::div_node * const node, int lvl);
    virtual void do_mod_node(cdk::mod_node * const node, int lvl);
    virtual void do_lt_node(cdk::lt_node * const node, int lvl);
    virtual void do_le_node(cdk::le_node * const node, int lvl);
    virtual void do_ge_node(cdk::ge_node * const node, int lvl);
    virtual void do_gt_node(cdk::gt_node * const node, int lvl);
    virtual void do_ne_node(cdk::ne_node * const node, int lvl);
    virtual void do_eq_node(cdk::eq_node * const node, int lvl);
    virtual void do_and_node(cdk::and_node * const node, int lvl);
    virtual void do_or_node(cdk::or_node * const node, int lvl);

    public:
    virtual void do_identifier_node(cdk::identifier_node * const node, int lvl);
    virtual void do_rvalue_node(cdk::rvalue_node * const node, int lvl);
    virtual void do_assignment_node(cdk::assignment_node * const node, int lvl);

    public:
    virtual void do_evaluation_node(ook::evaluation_node * const node, int lvl);
    virtual void do_print_node(ook::print_node * const node, int lvl);
    virtual void do_read_node(ook::read_node * const node, int lvl);

    public:
    virtual void do_while_node(ook::while_node * const node, int lvl);
    virtual void do_if_node(ook::if_node * const node, int lvl);
    virtual void do_if_else_node(ook::if_else_node * const node, int lvl);
    
    virtual void do_stop_node(ook::stop_node * const node, int lvl);
    virtual void do_return_node(ook::return_node * const node, int lvl);
    virtual void do_next_node(ook::next_node * const node, int lvl);
    virtual void do_block_node(ook::block_node * const node, int lvl);
    virtual void do_function_definition_node(ook::function_definition_node * const node, int lvl);
    virtual void do_function_declaration_node(ook::function_declaration_node * const node, int lvl);
    virtual void do_null_node(ook::null_node * const node, int lvl);
    virtual void do_variable_declaration_node(ook::variable_declaration_node * const node, int lvl);
    virtual void do_memory_alloc_node(ook::memory_alloc_node * const node, int lvl);
    virtual void do_function_call_node(ook::function_call_node * const node, int lvl);
    virtual void do_lvalue_index_node(ook::lvalue_index_node * const node, int lvl);
    virtual void do_memory_address_node(ook::memory_address_node * const node, int lvl);
    virtual void do_identity_node(ook::identity_node * const node, int lvl);

  };
} // ook

#endif
