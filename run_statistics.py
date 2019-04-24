#!/usr/bin/python3
import os
from subprocess import Popen

k_values = [2,8,20]
n_values = [250, 500, 1000, 5000]
m_values = [40,80,160]

os.system("rm -rf ./statistics")
#os.system('java -classpath "./build/classes:./gephi-toolkit-0.9.2-all.jar:./jsoup-1.11.3.jar" midterm.Main  16 100 10')

javacmd = 'java -classpath "./build/classes:./gephi-toolkit-0.9.2-all.jar:./jsoup-1.11.3.jar" midterm.Main {} {} {}'

# threads = []

for k in k_values:
    for n in n_values:
        for m in m_values:
            for i in range(0,3):
                command = javacmd.format(m,n,k)
                os.system(command)

# queste mancano tutte ma non posso farlo con n = 5000 ci mette troppo
for n in n_values:
    for i in range(0,7):
        command = javacmd.format(160,n,20)
        os.system(command)




           
            