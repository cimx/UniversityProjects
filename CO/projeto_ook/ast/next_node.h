#ifndef __CDK_NEXTNODE_H__
#define __CDK_NEXTNODE_H__

#include <cdk/ast/expression_node.h>

namespace ook {

  /**
   * Class for describing next (continue) nodes.
   */
  class next_node: public cdk::basic_node {
    //cdk::expression_node *_argument;
    int _n;

  public:
    inline next_node(int lineno, int n /* cdk::expression_node *argument*/) :
        cdk::basic_node(lineno), _n(n) /*_argument(argument)*/ {
    }

  public:
    inline int n(){
      return _n;
    }

    /*inline cdk::expression_node *argument() {
      return _argument;
    }*/

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_next_node(this, level);
    }

  };

} // ook

#endif
