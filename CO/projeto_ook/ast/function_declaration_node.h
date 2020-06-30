#ifndef __CDK_FUNDECNODE_H__
#define __CDK_FUNDECNODE_H__

#include <cdk/ast/basic_node.h>
#include <cdk/ast/sequence_node.h>
#include <cdk/basic_type.h>
#include <cdk/ast/expression_node.h>

namespace ook {

  /**
   * Class for describing function declaration nodes.
   */
  class function_declaration_node: public cdk::basic_node {
    bool _ispublic;
    bool _imported;
    basic_type *_type;
    std::string _identifier;
    cdk::sequence_node *_variables;
    cdk::expression_node *_literal;

  public:
    inline function_declaration_node(int lineno, bool ispublic, bool imported, basic_type *type, std::string *identifier, cdk::sequence_node *variables, cdk::expression_node *literal) :
        cdk::basic_node(lineno), _ispublic(ispublic), _imported(imported), _type(type), _identifier(*identifier), _variables(variables), _literal(literal){
    }

  public:
    inline bool ispublic(){
      return _ispublic;
    }
    inline bool imported() {
      return _imported;
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

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_function_declaration_node(this, level);
    }

  };

} // ook

#endif 
