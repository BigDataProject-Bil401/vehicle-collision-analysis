import matplotlib.pyplot as plt
import numpy as np
  
  
# Define X and Y variable data
x = [0,1, 2, 3, 4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23]
y = [6712,3348,2687,2434,2711,3008,4772,6437,10983,9791,8841,9709,10816,11566,13499,14019,15387,16127,15633,13405,11587,9598,8038,6476]
x2 = [0,1, 2, 3, 4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23]
y2 = [1305,799,593,565,645,632,821,1110,1938,1523,1455,1593,1752,1868,2258,2308,2547,2483,2413,1962,1788,1628,1613,1318]
  
plt.plot(x, y,label="Cluster 2")
plt.plot(x2, y2,label="Cluster 3")
plt.xticks(np.arange(0, 24, 1))
plt.legend(loc="upper left")

plt.xlabel("Hour")  # add X-axis label
plt.ylabel("Collision Count")  # add Y-axis label
plt.title("Cluster 2 and 3")  # add title
plt.show()
