COMPILERDVI=latex
COMPILERBIB=bibtex

COMPILERPDF=pdflatex
VIEWPDF=acroread
VIEWPS=gv

Dvi2Ps=dvips
Ps2Pdf=ps2pdf


FUENTE = Elvira-Book
BIBLIO = Introduction/mainIntroduction Basic/Format/mainFormat Basic/Data/mainData Inference/Exact/mainExact Inference/Approx/mainApprox Inference/Abduction/mainAbduction ID/mainID Learning/Preprocessing/mainPreprocessing Learning/Classification/mainClassification Learning/Learning/mainLearning Explanation/mainExplanation Tools/Fusion/mainFusion Tools/Misc/mainMisc


TODO = $(FUENTE) $(BIBLIO)

HAZ=make -f Makefile
Rm=rm -f warnings/* ; rm -f out/*





dvi : $(FUENTE).tex
	$(COMPILERDVI) $(FUENTE);
	@ $(HAZ) Bibliografia;
	$(COMPILERDVI) $(FUENTE);
	$(COMPILERDVI) $(FUENTE);
	@ $(HAZ) mover

ps : $(FUENTE).tex
	$(HAZ) dvi;
	$(Dvi2Ps) -o out/$(FUENTE).ps out/$(FUENTE).dvi
	$(VIEWPS) out/$(FUENTE).ps

pdf : $(FUENTE).tex
	$(HAZ) ps
	$(Ps2Pdf) out/$(FUENTE).ps out/$(FUENTE).pdf
	$(VIEWPDF) out/$(FUENTE).pdf

clear :
	@ $(Rm)

clean :
	@ $(Rm)

Bibliografia :
	@for file in $(BIBLIO); do \
		echo ">>>>>>>>>>>>>>>>>>>>>>> Compilando $$file"; \
		$(COMPILERBIB) $$file; \
		echo "<<<<<<<<<<<<<<<<<<<<<<"; \
	done


mover :
	@for file in $(TODO); do \
		echo ">>>>>>>>>>>>>>>>>>>>>>> Moviendo auxiliares de $$file"; \
		if test -f $$file.aux; then \
		  mv $$file.aux warnings ; \
		fi;\
		if test -f $$file.log; then \
		  mv $$file.log warnings ; \
		fi;\
		if test -f $$file.bbl; then \
		  mv $$file.bbl warnings ; \
		fi;\
		if test -f $$file.blg; then \
		  mv $$file.blg warnings ; \
		fi;\
		if test -f $$file.toc; then \
		  mv $$file.toc warnings ; \
		fi;\
		echo "<<<<<<<<<<<<<<<<<<<<<<"; \
	done; \
	mv $(FUENTE).* out; mv out/$(FUENTE).tex .