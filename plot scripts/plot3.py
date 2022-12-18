import matplotlib.pyplot as plt
import numpy as np

# create data
x = np.arange(4)
y1 = [235,447,16,1]


y2 = [137,384,12,1]
y3 = [111,201,18,0]
y4= [51,244,9,1]
y5=[24,66,5,0]
width = 0.1

# plot data in grouped manner of bar type
plt.bar(x-0.2, y1, width, color='cyan')
plt.bar(x-0.1, y2, width, color='red')
plt.bar(x, y3, width, color='orange')
plt.bar(x+0.1, y4, width, color='green')
plt.bar(x+0.2, y5, width, color='yellow')
plt.xticks(x, ['C7', 'C8', 'C9', 'C10'])
plt.xlabel("Clusters")
plt.ylabel("Collision Count")
plt.legend(["Brooklyn", "Queens", "Manhattan","Bronx","Staten Island"])
plt.show()
