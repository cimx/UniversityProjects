// $Id: stop_node.h,v 1.2 2017/07/09 20:28:20 ist181172 Exp $ -*- c++ -*-
#ifndef __CDK_STOPNODE_H__
#define __CDK_STOPNODE_H__

#include <cdk/ast/expression_node.h>

namespace ook {

  /**
   * Class for describing stop (break) nodes.
   */
  class stop_node: public cdk::basic_node {
    /*cdk::expression_node *_argument;*/
    int _n;

  public:
    inline stop_node(int lineno, int n /* cdk::expression_node *argument*/) :
        cdk::basic_node(lineno), _n(n) /* _argument(argument)*/ {
    }

  public:
    inline int n(){
      return _n;
    }
    
    /*inline cdk::expression_node *argument() {
      return _argument;
    }*/

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_stop_node(this, level);
    }

  };

} // ook

#endif
