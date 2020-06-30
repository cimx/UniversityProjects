#ifndef __CDK_MEMADD_NODE_H__
#define __CDK_MEMADD_NODE_H__

#include <cdk/ast/unary_expression_node.h>

namespace ook {

  /**
   * Class for describing memory address nodes.
   */
  class memory_address_node: public cdk::unary_expression_node {

    public:
      inline memory_address_node(int lineno, cdk::expression_node *lvalue) :
        cdk::unary_expression_node(lineno, lvalue) {
        }

    public:
      void accept(basic_ast_visitor *sp, int level) {
        sp->do_memory_address_node(this, level);
      }

  };

} // ook

#endif

