import matplotlib

vector_cluster_ids = open("data/vector-cluster-ids")
readlines = vector_cluster_ids.readlines()

for line in readlines:
    line = line.replace("(", "").replace(")", "").replace("[", "").replace("]", "")
    splitline = line.split(",")
    x_coordinate = splitline[0]
    y_coordinate = splitline[1]
    cluster_id = splitline[2]