#ifndef __CDK_FUNDEFNODE_H__
#define __CDK_FUNDEFNODE_H__

#include <cdk/ast/basic_node.h>
#include <cdk/ast/expression_node.h>
#include <cdk/ast/sequence_node.h>
#include <cdk/basic_type.h>
#include "function_declaration_node.h"

namespace ook {

  /**
   * Class for describing function definition nodes.
   */
  class function_definition_node: public cdk::basic_node {
    bool _ispublic;
    basic_type *_type;
    std::string _identifier;
    cdk::sequence_node *_variables;
    cdk::expression_node *_literal; 
    cdk::basic_node *_body;

  public:
    inline function_definition_node(int lineno, bool ispublic, basic_type *type, std::string *identifier, cdk::sequence_node *variables, cdk::expression_node *literal, cdk::basic_node *body) :
        cdk::basic_node(lineno), _ispublic(ispublic), _type(type), _identifier(*identifier), _variables(variables), _literal(literal), _body(body) {
    }
    inline function_definition_node(int lineno, ook::function_declaration_node *fundec, cdk::basic_node *body) :
        cdk::basic_node(lineno), _ispublic(fundec->ispublic()), _type(fundec->type()), _identifier(fundec->identifier()), _variables(fundec->variables()), _literal(fundec->literal()), _body(body) { 
    }
    
  public:
    inline bool ispublic(){
      return _ispublic;
    }
    inline basic_type *type() {
      return _type;
    }
    inline std::string &identifier() {
      return _identifier;
    }
    inline cdk::sequence_node *variables() {
      return _variables;
    }
    inline cdk::expression_node *literal() {
      return _literal;
    }
    inline cdk::basic_node *body() {
      return _body;
    }

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_function_definition_node(this, level);
    }

  };

} // ook

#endif 
