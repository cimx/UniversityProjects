#!/bin/bash
rm -f input/*.fst transdutores/*.pdf transdutores/*.fst

#INPUT para transdutor
fstcompile --isymbols=syms.txt --osymbols=syms.txt --keep_isymbols --keep_osymbols   input/81959_misto.txt   | fstarcsort >  input/81959_misto.fst

fstcompile --isymbols=syms.txt --osymbols=syms.txt --keep_isymbols --keep_osymbols   input/81959_numerico.txt   | fstarcsort >  input/81959_numerico.fst

fstcompile --isymbols=syms.txt --osymbols=syms.txt --keep_isymbols --keep_osymbols   input/81959_pt.txt   | fstarcsort >  input/81959_pt.fst

fstcompile --isymbols=syms.txt --osymbols=syms.txt --keep_isymbols --keep_osymbols   input/81172_misto.txt   | fstarcsort >  input/81172_misto.fst

fstcompile --isymbols=syms.txt --osymbols=syms.txt --keep_isymbols --keep_osymbols   input/81172_numerico.txt   | fstarcsort >  input/81172_numerico.fst

fstcompile --isymbols=syms.txt --osymbols=syms.txt --keep_isymbols --keep_osymbols   input/81172_pt.txt   | fstarcsort >  input/81172_pt.fst
#Compilar os transdutores 
# Compila e gera a versão textual do transdutor que traduz a em b


# A) 1
fstcompile --isymbols=syms.txt --osymbols=syms.txt --keep_isymbols --keep_osymbols   transdutores/mmm2mm.txt  | fstarcsort >  transdutores/mmm2mm.fst

# A) 2
fstcompile --isymbols=syms.txt --osymbols=syms.txt --keep_isymbols --keep_osymbols   transdutores/ignore_data.txt  | fstarcsort >  transdutores/ignore_data.fst

fstcompile --isymbols=syms.txt --osymbols=syms.txt --keep_isymbols --keep_osymbols   transdutores/ignore_data_end.txt  | fstarcsort >  transdutores/ignore_data_end.fst

# B) 3
fstcompile --isymbols=syms.txt --osymbols=syms.txt --keep_isymbols --keep_osymbols   transdutores/mmm2Ing.txt  | fstarcsort >  transdutores/mmm2Ing.fst

# C) 5
fstcompile --isymbols=syms.txt --osymbols=syms.txt --keep_isymbols --keep_osymbols   transdutores/dia.txt  | fstarcsort >  transdutores/dia.fst

# C) 5
fstcompile --isymbols=syms.txt --osymbols=syms.txt --keep_isymbols --keep_osymbols   transdutores/mes.txt  | fstarcsort >  transdutores/mes.fst


# C) 7
fstcompile --isymbols=syms.txt --osymbols=syms.txt --keep_isymbols --keep_osymbols   transdutores/ano_mil.txt  | fstarcsort >  transdutores/ano_mil.fst

# C) 8
fstcompile --isymbols=syms.txt --osymbols=syms.txt --keep_isymbols --keep_osymbols transdutores/ignore_bar.txt  | fstarcsort >  transdutores/ignore_bar.fst


#Special functions
# A) 2
fstconcat transdutores/ignore_data.fst transdutores/mmm2mm.fst  > transdutores/tmp.fst
fstconcat transdutores/tmp.fst transdutores/ignore_data_end.fst  > transdutores/misto2numerico.fst

# B) 3
fstconcat transdutores/ignore_data.fst transdutores/mmm2Ing.fst  > transdutores/tmp.fst
fstconcat transdutores/tmp.fst transdutores/ignore_data_end.fst  > transdutores/en2pt.fst

# B) 4
fstinvert transdutores/en2pt.fst > transdutores/pt2en.fst

# C) 7
fstconcat transdutores/ano_mil.fst transdutores/dia.fst  > transdutores/ano.fst

# C) 8
fstconcat transdutores/dia.fst transdutores/ignore_bar.fst  > transdutores/tmp.fst
fstconcat transdutores/tmp.fst transdutores/mes.fst  > transdutores/tmp1.fst
fstconcat transdutores/tmp1.fst transdutores/ignore_bar.fst  > transdutores/tmp2.fst
fstconcat transdutores/tmp2.fst transdutores/ano.fst  > transdutores/numerico2texto.fst

# D) 9
fstcompose transdutores/misto2numerico.fst transdutores/numerico2texto.fst | fstarcsort > transdutores/misto2texto.fst

# D) 10
fstunion transdutores/misto2texto.fst transdutores/numerico2texto.fst  > transdutores/data2texto.fst

rm -f transdutores/tmp.fst transdutores/tmp1.fst transdutores/tmp2.fst

#Criar PDFs
fstdraw --isymbols=syms.txt  --osymbols=syms.txt  --portrait transdutores/ano.fst | dot -Tpdf > transdutores/ano.pdf
fstdraw --isymbols=syms.txt  --osymbols=syms.txt  --portrait transdutores/ano_mil.fst | dot -Tpdf > transdutores/ano_mil.pdf
fstdraw --isymbols=syms.txt  --osymbols=syms.txt  --portrait transdutores/data2texto.fst | dot -Tpdf > transdutores/data2texto.pdf
fstdraw --isymbols=syms.txt  --osymbols=syms.txt  --portrait transdutores/dia.fst | dot -Tpdf > transdutores/dia.pdf
fstdraw --isymbols=syms.txt  --osymbols=syms.txt  --portrait transdutores/en2pt.fst | dot -Tpdf > transdutores/en2pt.pdf
fstdraw --isymbols=syms.txt  --osymbols=syms.txt  --portrait transdutores/ignore_bar.fst | dot -Tpdf > transdutores/ignore_bar.pdf
fstdraw --isymbols=syms.txt  --osymbols=syms.txt  --portrait transdutores/ignore_data.fst | dot -Tpdf > transdutores/ignore_data.pdf
fstdraw --isymbols=syms.txt  --osymbols=syms.txt  --portrait transdutores/ignore_data_end.fst | dot -Tpdf > transdutores/ignore_data_end.pdf
fstdraw --isymbols=syms.txt  --osymbols=syms.txt  --portrait transdutores/mes.fst | dot -Tpdf > transdutores/mes.pdf
fstdraw --isymbols=syms.txt  --osymbols=syms.txt  --portrait transdutores/misto2numerico.fst | dot -Tpdf > transdutores/misto2numerico.pdf
fstdraw --isymbols=syms.txt  --osymbols=syms.txt  --portrait transdutores/misto2texto.fst | dot -Tpdf > transdutores/misto2texto.pdf
fstdraw --isymbols=syms.txt  --osymbols=syms.txt  --portrait transdutores/mmm2Ing.fst | dot -Tpdf > transdutores/mmm2Ing.pdf
fstdraw --isymbols=syms.txt  --osymbols=syms.txt  --portrait transdutores/mmm2mm.fst | dot -Tpdf > transdutores/mmm2mm.pdf
fstdraw --isymbols=syms.txt  --osymbols=syms.txt  --portrait transdutores/numerico2texto.fst | dot -Tpdf > transdutores/numerico2texto.pdf
fstdraw --isymbols=syms.txt  --osymbols=syms.txt  --portrait transdutores/pt2en.fst | dot -Tpdf > transdutores/pt2en.pdf

#Testar
echo "---------------------------- Testes para 81959 ----------------------------"
fstcompose input/81959_misto.fst transdutores/misto2numerico.fst | fstarcsort > output/81959_misto2numerico.fst
echo -n "misto2numerico input: "
fstproject --project_output input/81959_misto.fst | fstrmepsilon | fsttopsort | fstprint --acceptor --isymbols=syms-out.txt | awk '{print $3}'
echo -n "output: "
fstproject --project_output output/81959_misto2numerico.fst | fstrmepsilon | fsttopsort | fstprint --acceptor --isymbols=syms-out.txt | awk '{print $3}'

fstcompose input/81959_pt.fst transdutores/pt2en.fst | fstarcsort > output/81959_pt2en.fst
echo -n "pt2en input: "
fstproject --project_output input/81959_pt.fst | fstrmepsilon | fsttopsort | fstprint --acceptor --isymbols=syms-out.txt | awk '{print $3}'
echo -n "output: "
fstproject --project_output output/81959_pt2en.fst | fstrmepsilon | fsttopsort | fstprint --acceptor --isymbols=syms-out.txt | awk '{print $3}'

fstcompose input/81959_numerico.fst transdutores/numerico2texto.fst | fstarcsort > output/81959_numerico2texto.fst
echo -n "numerico2texto input: "
fstproject --project_output input/81959_numerico.fst | fstrmepsilon | fsttopsort | fstprint --acceptor --isymbols=syms-out.txt | awk '{print $3}'
echo -n "output: "
fstproject --project_output output/81959_numerico2texto.fst | fstrmepsilon | fsttopsort | fstprint --acceptor --isymbols=syms-out.txt | awk '{print $3}'

fstcompose input/81959_misto.fst transdutores/misto2texto.fst | fstarcsort > output/81959_misto2texto.fst
echo -n "misto2texto input: "
fstproject --project_output input/81959_misto.fst | fstrmepsilon | fsttopsort | fstprint --acceptor --isymbols=syms-out.txt | awk '{print $3}'
echo -n "output: "
fstproject --project_output output/81959_misto2texto.fst | fstrmepsilon | fsttopsort | fstprint --acceptor --isymbols=syms-out.txt | awk '{print $3}'

fstcompose input/81959_pt.fst transdutores/data2texto.fst | fstarcsort > output/81959_data2texto.fst
echo -n "data2texto input: "
fstproject --project_output input/81959_pt.fst | fstrmepsilon | fsttopsort | fstprint --acceptor --isymbols=syms-out.txt | awk '{print $3}'
echo -n "output: "
fstproject --project_output output/81959_data2texto.fst | fstrmepsilon | fsttopsort | fstprint --acceptor --isymbols=syms-out.txt | awk '{print $3}'

#Criar PDFs
fstdraw --isymbols=syms.txt  --osymbols=syms.txt  --portrait output/81959_misto2numerico.fst | dot -Tpdf > output/81959_misto2numerico.pdf
fstdraw --isymbols=syms.txt  --osymbols=syms.txt  --portrait output/81959_pt2en.fst | dot -Tpdf > output/81959_pt2en.pdf
fstdraw --isymbols=syms.txt  --osymbols=syms.txt  --portrait output/81959_numerico2texto.fst | dot -Tpdf > output/81959_numerico2texto.pdf
fstdraw --isymbols=syms.txt  --osymbols=syms.txt  --portrait output/81959_misto2texto.fst | dot -Tpdf > output/81959_misto2texto.pdf
fstdraw --isymbols=syms.txt  --osymbols=syms.txt  --portrait output/81959_data2texto.fst | dot -Tpdf > output/81959_data2texto.pdf

echo "---------------------------- Testes para 81172 ---------------------------- "
fstcompose input/81172_misto.fst transdutores/misto2numerico.fst | fstarcsort > output/81172_misto2numerico.fst
echo -n "misto2numerico input: "
fstproject --project_output input/81172_misto.fst | fstrmepsilon | fsttopsort | fstprint --acceptor --isymbols=syms-out.txt | awk '{print $3}'
echo -n "output: "
fstproject --project_output output/81172_misto2numerico.fst | fstrmepsilon | fsttopsort | fstprint --acceptor --isymbols=syms-out.txt | awk '{print $3}'

fstcompose input/81172_pt.fst transdutores/pt2en.fst | fstarcsort > output/81172_pt2en.fst
echo -n "pt2en input: "
fstproject --project_output input/81172_pt.fst | fstrmepsilon | fsttopsort | fstprint --acceptor --isymbols=syms-out.txt | awk '{print $3}'
echo -n "output: "
fstproject --project_output output/81172_pt2en.fst | fstrmepsilon | fsttopsort | fstprint --acceptor --isymbols=syms-out.txt | awk '{print $3}'

fstcompose input/81172_numerico.fst transdutores/numerico2texto.fst | fstarcsort > output/81172_numerico2texto.fst
echo -n "numerico2texto input: "
fstproject --project_output input/81172_numerico.fst | fstrmepsilon | fsttopsort | fstprint --acceptor --isymbols=syms-out.txt | awk '{print $3}'
echo -n "output: "
fstproject --project_output output/81172_numerico2texto.fst | fstrmepsilon | fsttopsort | fstprint --acceptor --isymbols=syms-out.txt | awk '{print $3}'

fstcompose input/81172_misto.fst transdutores/misto2texto.fst | fstarcsort > output/81172_misto2texto.fst
echo -n "misto2texto input: "
fstproject --project_output input/81172_misto.fst | fstrmepsilon | fsttopsort | fstprint --acceptor --isymbols=syms-out.txt | awk '{print $3}'
echo -n "output: "
fstproject --project_output output/81172_misto2texto.fst | fstrmepsilon | fsttopsort | fstprint --acceptor --isymbols=syms-out.txt | awk '{print $3}'

fstcompose input/81172_numerico.fst transdutores/data2texto.fst | fstarcsort > output/81172_data2texto.fst
echo -n "data2texto input: "
fstproject --project_output input/81172_numerico.fst | fstrmepsilon | fsttopsort | fstprint --acceptor --isymbols=syms-out.txt | awk '{print $3}'
echo -n "output: "
fstproject --project_output output/81172_data2texto.fst | fstrmepsilon | fsttopsort | fstprint --acceptor --isymbols=syms-out.txt | awk '{print $3}'

#Criar PDFs
fstdraw --isymbols=syms.txt  --osymbols=syms.txt  --portrait output/81172_misto2numerico.fst | dot -Tpdf > output/81172_misto2numerico.pdf
fstdraw --isymbols=syms.txt  --osymbols=syms.txt  --portrait output/81172_pt2en.fst | dot -Tpdf > output/81172_pt2en.pdf
fstdraw --isymbols=syms.txt  --osymbols=syms.txt  --portrait output/81172_numerico2texto.fst | dot -Tpdf > output/81172_numerico2texto.pdf
fstdraw --isymbols=syms.txt  --osymbols=syms.txt  --portrait output/81172_misto2texto.fst | dot -Tpdf > output/81172_misto2texto.pdf
fstdraw --isymbols=syms.txt  --osymbols=syms.txt  --portrait output/81172_data2texto.fst | dot -Tpdf > output/81172_data2texto.pdf