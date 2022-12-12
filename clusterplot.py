import matplotlib.pyplot as plt

vector_cluster_ids = open("data/vector-cluster-ids")
readlines = vector_cluster_ids.readlines()

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

x = [float(i) for i in x]
y = [float(i) for i in y]
cluster_ids = [int(i) for i in cluster_ids]

ax.scatter(x, y, c=cluster_ids, cmap='viridis', s=0.5)

plt.savefig('cluster_graph.png')
