INSTJARDIR = /usr/lib
INSTBINDIR = /usr/bin
SRCFILES := $(shell find src -name \*.java)
RESFILES := $(shell find src -type f \! -name .\* \! -name \*.java | sed s/^src\\///)
PACKAGES := $(shell find src -name \*.java | sed -E s/^src\\/\|\\/[^\\/]+$$//g | grep -v \\.java$$ | sort | uniq | tr / .)

all: clean bin doc KSFL.jar KSFL-src.tgz

eclipseall: eclipseclean KSFL.jar KSFL-src.tgz

clean:
	rm -rf bin
	rm -rf doc
	rm -rf KSFL*.jar
	rm -rf KSFL*.tgz

eclipseclean:
	rm -rf KSFL*.jar
	rm -rf KSFL*.tgz

bin: src
	mkdir -p bin
ifeq ($(origin JAVACFLAGS), undefined)
	$(eval JAVACFLAGS = $(shell dep/minJavaVerOpts))
endif
	javac $(JAVACFLAGS) -sourcepath src $(SRCFILES) -d bin
	$(foreach res,$(RESFILES),cp src/$(res) bin/$(res);)

doc: src
	mkdir -p doc
	javadoc $(JAVADOCFLAGS) -sourcepath src $(PACKAGES) -d doc

KSFL.jar: bin
	export COPYFILE_DISABLE=true
	find bin -name .DS_Store -delete
	jar cmf dep/MANIFEST.MF KSFL.jar -C bin com/kreative
	chmod +x KSFL.jar

KSFL-src.tgz: src
	export COPYFILE_DISABLE=true
	find src -name .DS_Store -delete
	tar -czf KSFL-src.tgz LICENSE src/com/kreative

localuninstall:
	rm ksfl

localinstall: KSFL.jar
	echo "#!/bin/sh" > ksfl
	echo 'java -Xmx1024M -jar KSFL.jar "$$@"' >> ksfl
	chmod +x ksfl

uninstall:
	rm -f $(INSTJARDIR)/KSFL.jar
	rm -f $(INSTBINDIR)/ksfl

install: KSFL.jar
	cp -f KSFL.jar $(INSTJARDIR)/KSFL.jar
	echo "#!/bin/sh" > $(INSTBINDIR)/ksfl
	echo 'java -Xmx1024M -jar "$(INSTJARDIR)/KSFL.jar" "$$@"' >> $(INSTBINDIR)/ksfl
	chmod +x $(INSTBINDIR)/ksfl

.PHONY: all eclipseall clean eclipseclean localuninstall localinstall uninstall install
