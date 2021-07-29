COMPILERDVI=latex
COMPILERBIB=bibtex

COMPILERPDF=pdflatex
VIEWPDF=acroread

Dvi2Ps=dvips


FUENTE = Elvira-Presentation

MOVERout=mv $(FUENTE).* out; mv out/$(FUENTE).tex . 
MOVERwarnings=mv *.aux warnings; mv *.log warnings; mv *.blg warnings
HAZ=make -f Makefile
Rm=rm -f warnings/* ; rm -Rf out/html





dvi : $(FUENTE).tex
	$(COMPILERDVI) $(FUENTE); $(COMPILERDVI) $(FUENTE);
	$(COMPILERBIB) $(FUENTE); $(COMPILERDVI) $(FUENTE); $(COMPILERDVI) $(FUENTE);	
	@ $(HAZ) mover
	
	
ps : $(FUENTE).tex
	$(HAZ) dvi;
	$(Dvi2Ps) -o out/$(FUENTE).ps out/$(FUENTE).dvi

pdf : $(FUENTE).tex
	$(COMPILERPDF) $(FUENTE); $(COMPILERPDF) $(FUENTE); 
	$(COMPILERBIB) $(FUENTE); $(COMPILERPDF) $(FUENTE); $(COMPILERPDF) $(FUENTE)
	$(VIEWPDF) $(FUENTE).pdf;
	@ $(HAZ) mover
	
mover : 
	@ $(MOVERwarnings); $(MOVERout)

clear : 
	@ $(Rm)
	
clean : 
	@ $(Rm)
