#ifndef __CDK_LVALUEINDEXNODE_H__
#define __CDK_LVALUEINDEXNODE_H__

#include <cdk/ast/expression_node.h>
#include <cdk/ast/lvalue_node.h>

namespace ook {

  /**
   * Class for describing indexation nodes.
   */
  class lvalue_index_node: public cdk::lvalue_node {
    cdk::expression_node *_index, *_address;

    public:
      inline lvalue_index_node(int lineno, cdk::expression_node *index, cdk::expression_node *address) :
        cdk::lvalue_node(lineno), _index(index), _address(address) {
	    }

    public:
      inline cdk::expression_node *index()  { 
		    return _index; 
  	  }
      inline cdk::expression_node *address() { 
	     	return _address; 
      }

      void accept(basic_ast_visitor *sp, int level) {
        sp->do_lvalue_index_node(this, level);
      }

  };

} // ook

#endif