import matplotlib.pyplot as plt
import numpy as np

# create data
x = np.arange(1)
y1 = [119]


y2 = [59]
y3 = [43]

width = 0.1

# plot data in grouped manner of bar type
plt.bar(x-0.1, y1, width, color='cyan')
plt.bar(x, y2, width, color='red')
plt.bar(x+0.1, y3, width, color='orange')

plt.xticks(x, ['C7'])
plt.xlabel("Cluster")
plt.ylabel("Collision Count")
plt.legend(["Driver Inattention/Distraction", "Failure to Yield Right-of-Way", "Other Vehicular"])
plt.show()
