#ifndef __OOK_SEMANTICS_SYMBOL_H__
#define __OOK_SEMANTICS_SYMBOL_H__

#include <string>
#include <cdk/basic_type.h>

namespace ook {

    class symbol {
      basic_type *_type;
      std::string _name;
      long _value; // hack!

      bool _function = false;               //true if function , false if variable
      bool _fun_defined = false;            //true if function has been defined, false if function only declared
      
      std::vector<basic_type*> _arguments;  //types of arguments of a function

    public:
      //variables
      inline symbol(basic_type *type, const std::string &name, long value) :
          _type(type), _name(name), _value(value) {
      }
      //functions
      inline symbol(basic_type *type, const std::string &name, std::vector<basic_type*> arguments, bool function, bool fun_defined, long value) :
        _type(type), _name(name), _arguments(arguments), _function(function), _fun_defined(fun_defined), _value(value) {
      }
      virtual ~symbol() {
        delete _type;
      }

      inline basic_type *type() const {
        return _type;
      }
      inline const std::string &name() const {
        return _name;
      }
      inline long value() const {
        return _value;
      }
      inline long value(long v) {
        return _value = v;
      }

      inline bool function(){
          return _function;
      }
      inline void function(bool isfunc){
        _function = isfunc;
      }
      inline std::vector<basic_type*> arguments() {
        return _arguments;
      }
      inline void arguments(std::vector<basic_type*> args) {
        _arguments = args;
      }
      inline bool function_defined(){
        return _fun_defined;
      }
      inline void function_defined(bool isDefined){
        _fun_defined = isDefined;
      }
    };

} // ook

#endif
