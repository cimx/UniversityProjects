#ifndef __OOK_PRINTNODE_H__
#define __OOK_PRINTNODE_H__

#include <cdk/ast/expression_node.h>

namespace ook {

  /**
   * Class for describing print nodes.
   */
  class print_node: public cdk::basic_node {
    cdk::expression_node *_argument;
    bool _is_newline;

  public:
    inline print_node(int lineno, cdk::expression_node *argument, bool nl) :
        cdk::basic_node(lineno), _argument(argument), _is_newline(nl) {
    }

  public:
    inline cdk::expression_node *argument() {
      return _argument;
    }
    inline bool is_newline() {
      return _is_newline;
    }
    void accept(basic_ast_visitor *sp, int level) {
      sp->do_print_node(this, level);
    }

  };

} // ook

#endif
