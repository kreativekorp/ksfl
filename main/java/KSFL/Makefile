INSTJARDIR = /usr/lib
INSTBINDIR = /usr/bin
SRCFILES = src/*.java src/com/kreative/*/*.java src/com/kreative/*/*/*.java
PACKAGES = com.kreative.cff com.kreative.dff com.kreative.ksfl com.kreative.pe com.kreative.prc com.kreative.rsrc com.kreative.rsrc.misc com.kreative.rsrc.pict

all: clean bin doc osxclean KSFL.jar KSFL-src.tgz

eclipseall: eclipseclean osxclean KSFL.jar KSFL-src.tgz

clean:
	rm -rf bin
	rm -rf doc
	rm -rf KSFL*.jar
	rm -rf KSFL*.tgz

eclipseclean:
	rm -rf KSFL*.jar
	rm -rf KSFL*.tgz

bin:
	mkdir -p bin
	javac -sourcepath src $(SRCFILES) -d bin

doc:
	mkdir -p doc
	javadoc -sourcepath src $(PACKAGES) -d doc

osxclean:
	export COPYFILE_DISABLE=true
	rm -f src/.DS_Store
	rm -f src/*/.DS_Store
	rm -f src/*/*/.DS_Store
	rm -f src/*/*/*/.DS_Store
	rm -f src/*/*/*/*/.DS_Store
	rm -f src/*/*/*/*/*/.DS_Store
	rm -f src/*/*/*/*/*/*/.DS_Store
	rm -f src/*/*/*/*/*/*/*/.DS_Store
	rm -f src/*/*/*/*/*/*/*/*/.DS_Store
	rm -f bin/.DS_Store
	rm -f bin/*/.DS_Store
	rm -f bin/*/*/.DS_Store
	rm -f bin/*/*/*/.DS_Store
	rm -f bin/*/*/*/*/.DS_Store
	rm -f bin/*/*/*/*/*/.DS_Store
	rm -f bin/*/*/*/*/*/*/.DS_Store
	rm -f bin/*/*/*/*/*/*/*/.DS_Store
	rm -f bin/*/*/*/*/*/*/*/*/.DS_Store

KSFL.jar: osxclean
	jar cmf dep/MANIFEST.MF KSFL.jar -C bin com/kreative

KSFL-src.tgz: osxclean
	tar -czf KSFL-src.tgz src/com/kreative LICENSE

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

.PHONY: all eclipseall clean eclipseclean osxclean localuninstall localinstall uninstall install
