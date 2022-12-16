import matplotlib.pyplot as plt
vector_cluster_ids = open("data/vector-cluster-ids")
readlines = vector_cluster_ids.readlines()

cluster_centroids = open("data/cluster-centroids")
centroid_readlines = cluster_centroids.readlines()

fig, ax = plt.subplots()

x = []
y = []
cluster_ids = []

for line in readlines:
    line = line.replace("(", "").replace(")", "").replace("[", "").replace("]", "")
    splitline = line.split(",")
    x.append(splitline[0])
    y.append(splitline[1])
    cluster_ids.append(splitline[2])

x_centroid = []
y_centroid = []
for line in centroid_readlines:
    line = line.replace("[", "").replace("]", "")
    splitline = line.split(",")
    x_centroid.append(splitline[0])
    y_centroid.append(splitline[1])

x = [float(i) for i in x]
y = [float(i) for i in y]
cluster_ids = [int(i) for i in cluster_ids]

x_centroid = [float(i) for i in x_centroid]
y_centroid = [float(i) for i in y_centroid]

ax.scatter(x, y, c=cluster_ids, cmap='viridis', s=0.5)
for i in range(len(x_centroid)):
    ax.plot(x_centroid[i], y_centroid[i],c='black', marker="o", markersize=3)

plt.title("Cluster Graph")
plt.xlabel("Latitude")
plt.ylabel("Longitude")
plt.savefig('./data/cluster_graph.png')
