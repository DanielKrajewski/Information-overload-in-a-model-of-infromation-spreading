# convert_neighbours_with_opinion.py

input_file = "RESULTS0,40/500/BA/0,80/1_neighbours.txt"
edges_file = "wizualizacja/edges.tsv"
nodes_file = "wizualizacja/node_attributes.tsv"

edges = set()
opinions = {}

with open(input_file, 'r') as f:
    header = next(f)  # Pomijamy nagłówek
    for line in f:
        parts = line.strip().split('\t')
        if len(parts) != 3:
            continue
        node = int(parts[0])
        neighbours = map(int, parts[1].split())
        opinion = parts[2].strip()

        opinions[node] = opinion

        for neighbour in neighbours:
            edge = tuple(sorted((node, neighbour)))  # unikalne, nieskierowane
            edges.add(edge)

# Zapisz krawędzie do pliku TSV
with open(edges_file, 'w') as f:
    f.write("Source\tTarget\n")
    for source, target in sorted(edges):
        f.write(f"{source}\t{target}\n")

# Zapisz opinie węzłów do pliku TSV
with open(nodes_file, 'w') as f:
    f.write("Node\tOpinion\n")
    for node, opinion in sorted(opinions.items()):
        f.write(f"{node}\t{opinion}\n")

print(f"Zapisano {len(edges)} krawędzi do pliku '{edges_file}'.")
print(f"Zapisano {len(opinions)} węzłów z opiniami do pliku '{nodes_file}'.")
