#!/usr/bin/python3
import os
from subprocess import Popen

#
# This script can be used to automate the tests
# Before running it you must build the project
# $> ant -f build.xml
#

k_values = [2,8,20]
n_values = [250, 500, 1000, 2000, 3000]
m_values = [16, 32, 64]

os.system("rm -rf ./statistics")

javacmd = 'java -classpath "./build/classes:./gephi-toolkit-0.9.2-all.jar:./jsoup-1.11.3.jar" midterm.Main {} {} {}'



for k in k_values:
    for n in n_values:
        for m in m_values:
            for i in range(0,3):
                command = javacmd.format(m,n,k)
                os.system(command)

for n in n_values:
    for i in range(0,3):
        command = javacmd.format(160,n,20)
        os.system(command)


command = javacmd.format(160,10000,20)
os.system(command)



           
            