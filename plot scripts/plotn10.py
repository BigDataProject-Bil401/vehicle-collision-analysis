import matplotlib.pyplot as plt
import numpy as np

# create data
x = np.arange(1)
y1 = [1]


y2 = [1]


width = 0.1

# plot data in grouped manner of bar type
plt.bar(x-0.1, y1, width, color='cyan')
plt.bar(x, y2, width, color='red')

plt.xticks(x, ['C10'])
plt.xlabel("Cluster")
plt.ylabel("Collision Count")
plt.legend(["Drugs(illegal)", "Physical Disability"])
plt.show()
