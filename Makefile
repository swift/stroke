all: dist/lib/stroke.jar

DEFINES = -Dxpp-dir=third-party/xpp -Djzlib-dir=third-party/jzlib -Dicu4j-dir=third-party/ -Dstax2-dir=third-party/stax2/ -Daalto-dir=third-party/aalto/ 
JUNIT ?= /usr/share/junit/junit.jar

.PHONY : clean
clean:
	ant clean

.PHONY : distclean
distclean: clean
	ant distclean
	rm -rf third-party

.PHONY : dist/lib/stroke.jar
dist/lib/stroke.jar: third-party/jzlib/jzlib.jar third-party/icu4j.jar third-party/aalto/aalto-xml.jar third-party/stax2/stax2-api.jar
	ant ${DEFINES}

.PHONY : test
test: dist/lib/stroke.jar third-party/cobertura/cobertura.jar third-party/findbugs/lib/findbugs.jar
	ant ${DEFINES} -DJUNIT_JAR=${JUNIT} -Dcobertura-jar=third-party/cobertura/cobertura.jar -Djakarta-oro-jar=third-party/cobertura/lib/jakarta-oro-2.0.8.jar -Dlog4j-jar=third-party/cobertura/lib/log4j-1.2.9.jar -Dasm-jar=third-party/cobertura/lib/asm-3.0.jar -Dasm-tree-jar=third-party/cobertura/lib/asm-tree-3.0.jar -Dfindbugs.home=third-party/findbugs test

third-party/aalto/aalto-xml.jar:
	mkdir -p third-party/aalto
	curl http://repo2.maven.org/maven2/com/fasterxml/aalto-xml/0.9.8/aalto-xml-0.9.8.jar -o third-party/aalto/aalto-xml.jar

third-party/stax2/stax2-api.jar:
	mkdir -p third-party/stax2
	curl http://repository.codehaus.org/org/codehaus/woodstox/stax2-api/3.0.3/stax2-api-3.0.3.jar -o third-party/stax2/stax2-api.jar

third-party/jzlib/jzlib.jar:
	mkdir -p third-party
	curl http://www.jcraft.com/jzlib/jzlib-1.0.7.tar.gz -o third-party/jzlib-1.0.7.tar.gz
	tar -xvzf third-party/jzlib-1.0.7.tar.gz -C third-party/
	mv third-party/jzlib-1.0.7 third-party/jzlib
	cp build-jzlib.xml third-party/jzlib/build.xml
	ant -f third-party/jzlib/build.xml

third-party/icu4j.jar:
	mkdir -p third-party
	curl http://download.icu-project.org/files/icu4j/4.8.1/icu4j-4_8_1.jar -o third-party/icu4j.jar

third-party/cobertura/cobertura.jar:
	mkdir -p third-party
	curl -L 'http://sourceforge.net/projects/cobertura/files/cobertura/1.9.4.1/cobertura-1.9.4.1-bin.tar.bz2/download' -o third-party/cobertura-1.9.4.1-bin.tar.bz2
	tar -xvjf third-party/cobertura-1.9.4.1-bin.tar.bz2 -C third-party/
	mv third-party/cobertura-1.9.4.1 third-party/cobertura

third-party/findbugs/lib/findbugs.jar:
	mkdir -p third-party
	curl -L 'http://prdownloads.sourceforge.net/findbugs/findbugs-2.0.1.tar.gz?download' -o third-party/findbugs-2.0.1.tar.gz
	tar -xvzf third-party/findbugs-2.0.1.tar.gz -C third-party/
	mv third-party/findbugs-2.0.1 third-party/findbugs

.git/hooks/commit-msg:
	curl -k https://git.swift.im/review/tools/hooks/commit-msg -o .git/hooks/commit-msg
	chmod u+x .git/hooks/commit-msg
