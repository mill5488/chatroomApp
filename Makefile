# Very simple Makefile for Multithreaded Chat Application 
# A Java Implementation
# Author: Maxwell Miller, mill5488@umn.edu 
#test2.txt is for testing/debugging code. 

JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Server.java \
	Client.java \
	ClientInfo.java\
	Sender.java\
	Listener.java\
	Handler.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class test2.txt




















































#CC = gcc
#CFLAGS = -g -std=c11 -fopenmp -lgomp -lomp

#TAR = km_openmp km_pthreads
#OBJ = km_openmp.o km_pthreads.o

#all: km_openmp km_pthreads

#km_openmp: km_openmp.o
#	$(CC) -o km_openmp km_openmp.c

#km_openmp.o: km_openmp.c
#	$(CC) -c km_openmp.c

#km_pthreads: km_pthreads.o
#	$(CC) -o km_ptheads km_pthreads.c

#km_pthreads.o: km_pthreads.c
#	$(CC) -c km_pthreads.c

#clean: 
#	rm -f $(OBJ) $(TAR)

#LDFLAGS = -g -L/usr/local/lib -fopenmp

#TAR = rs_openmp rs_mpi
#OBJ = rs_openmp.o rs_mpi.o
#FILE = output.txt

#all: rs_openmp rs_mpi

#rs_mpi: rs_mpi.o
#	$(CC) $(LDFLAGS) -o rs_mpi -std=c11 -lgomp -fopenmp rs_mpi.o

#rs_mpi.o: rs_mpi.c
#	$(CC) -c rs_mpi.c  

#rs_openmp: rs_openmp.o
#	$(CC) $(LDFLAGS) -o rs_openmp -std=c11 -lgomp -fopenmp rs_openmp.o

#rs_openmp.o: rs_openmp.c
#	$(CC) -c rs_openmp.c  

#clean:
#	rm -f $(OBJ) $(TAR) $(FILE)
