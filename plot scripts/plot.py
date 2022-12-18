import matplotlib.pyplot as plt
import numpy as np

# create data
x = np.arange(3)
y1 = [315542,76145, 13603 ]
y2 = [275447,57686, 11053 ]
y3 = [245611,42224, 4303 ]
y4= [146719,33327,6232]
y5=[43776,8202,1726]
width = 0.1

# plot data in grouped manner of bar type
plt.bar(x-0.2, y1, width, color='cyan')
plt.bar(x-0.1, y2, width, color='red')
plt.bar(x, y3, width, color='orange')
plt.bar(x+0.1, y4, width, color='green')
plt.bar(x+0.2, y5, width, color='yellow')
plt.xticks(x, ['C1', 'C2', 'C3'])
plt.xlabel("Clusters")
plt.ylabel("Collision Count")
plt.legend(["Brooklyn", "Queens", "Manhattan","Bronx","Staten Island"])
plt.show()
