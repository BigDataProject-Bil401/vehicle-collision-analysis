import matplotlib.pyplot as plt
import numpy as np

# create data
x = np.arange(3)
y1 = [4437, 1620, 922]


y2 = [ 3451, 1217,620]
y3 = [2100, 800,467]
y4= [1244,436,220]
y5=[518,219,100]
width = 0.1

# plot data in grouped manner of bar type
plt.bar(x-0.2, y1, width, color='cyan')
plt.bar(x-0.1, y2, width, color='red')
plt.bar(x, y3, width, color='orange')
plt.bar(x+0.1, y4, width, color='green')
plt.bar(x+0.2, y5, width, color='yellow')
plt.xticks(x, [ 'C4', 'C5','C6'])
plt.xlabel("Clusters")
plt.ylabel("Collision Count")
plt.legend(["Brooklyn", "Queens", "Manhattan","Bronx","Staten Island"])
plt.show()
