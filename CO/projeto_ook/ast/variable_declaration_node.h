#ifndef __CDK_VARDEC_H__
#define __OOK_VARDEC_H__

#include <cdk/ast/expression_node.h>
#include <cdk/ast/sequence_node.h>

namespace ook {
  /**
   * Class for describing variable declaration nodes
   */
  class variable_declaration_node : public cdk::basic_node {
    bool _ispublic;
    bool _imported;
    basic_type *_type;
    std::string _identifier;
    cdk::expression_node *_value;
    
    public:
    inline variable_declaration_node(int lineno, bool ispublic, bool imported, basic_type * type, std::string *identifier, cdk::expression_node *value) :
        cdk::basic_node(lineno), _ispublic(ispublic), _imported(imported), _type(type), _identifier(*identifier), _value(value) {
        }

    public:
    inline bool ispublic() {
        return _ispublic;
    }
    inline bool imported() {
        return _imported;
    }
    inline basic_type *type() {
      return _type;
    }
    inline std::string &identifier(){
        return _identifier;
    }
    inline cdk::expression_node *value(){
        return _value;
    }
    void accept(basic_ast_visitor *sp, int level) {
      sp->do_variable_declaration_node(this, level);
    }

  };

} // ook

#endif

