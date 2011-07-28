all: dist/lib/stroke.jar

.PHONY : clean
clean:
	ant clean

.PHONY : distclean
distclean: clean
	ant distclean
	rm -rf third-party

.PHONY : dist/lib/stroke.jar
dist/lib/stroke.jar: third-party/xpp/xpp.jar third-party/jzlib/jzlib.jar third-party/icu4j.jar
	ant -Dxpp-dir=third-party/xpp -Djzlib-dir=third-party/jzlib

third-party/xpp/xpp.jar:
	mkdir -p third-party/xpp
	curl http://www.extreme.indiana.edu/dist/java-repository/xpp3/jars/xpp3-1.1.4c.jar -o third-party/xpp/xpp.jar

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

