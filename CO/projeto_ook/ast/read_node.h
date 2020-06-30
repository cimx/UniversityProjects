// $Id: read_node.h,v 1.3 2017/07/09 20:28:20 ist181172 Exp $ -*- c++ -*-
#ifndef __OOK_READNODE_H__
#define __OOK_READNODE_H__

#include <cdk/ast/lvalue_node.h>

namespace ook {

  /**
   * Class for describing read nodes.
   */
  class read_node: public cdk::expression_node  {
    //cdk::lvalue_node *_argument;

  public:
    inline read_node(int lineno/*, cdk::lvalue_node *argument*/) :
        cdk::expression_node(lineno)/*, _argument(argument) */{
    }

  public:
    /*inline cdk::lvalue_node *argument() {
      return _argument;
    }*/

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_read_node(this, level);
    }

  };

} // ook

#endif
