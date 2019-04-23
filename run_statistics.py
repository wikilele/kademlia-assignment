#!/usr/bin/python3
import os

k_values = [2,8,10,16,20,30,40]
n_values = [500, 1000, 2000, 4000, 8000, 10000]
m_values = [80,160,256,512]

os.system("rm -rf ./statistics")
#os.system('java -classpath "./build/classes:./gephi-toolkit-0.9.2-all.jar:./jsoup-1.11.3.jar" midterm.Main  16 100 10')

javacmd = 'java -classpath "./build/classes:./gephi-toolkit-0.9.2-all.jar:./jsoup-1.11.3.jar" midterm.Main {} {} {}'



for k in k_values:
    for n in n_values:
        for m in m_values:
            command = javacmd.format(m,n,k)
            os.system(command)
           
            