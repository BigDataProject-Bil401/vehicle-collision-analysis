import matplotlib.pyplot as plt
import numpy as np

# create data
x = np.arange(1)
y1 = [190]


y2 = [171]
y3 = [126]

width = 0.1

# plot data in grouped manner of bar type
plt.bar(x-0.1, y1, width, color='cyan')
plt.bar(x, y2, width, color='red')
plt.bar(x+0.1, y3, width, color='orange')

plt.xticks(x, ['C8'])
plt.xlabel("Cluster")
plt.ylabel("Collision Count")
plt.legend(["Failure to Yield Right-of-Way", "Driver Inattention/Distraction", "Traffic Control Disregarded"])
plt.show()
