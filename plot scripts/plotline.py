import matplotlib.pyplot as plt
import numpy as np
  
  
# Define X and Y variable data
x = [0,1, 2, 3, 4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23]
y = [30145,16262,12483,10735,11813,12592,19499,28206,58061,58263,55638,57724,60860,63698,72635,64146,75592,72192,62416,50620,41807,34897,31578,25244]
plt.xticks(np.arange(0, 24, 1))
plt.plot(x, y,label="Cluster 1")
plt.legend(loc="upper left")
plt.xlabel("Hour")  # add X-axis label
plt.ylabel("Collision Count")  # add Y-axis label
plt.title("Cluster 1")  # add title
plt.show()
