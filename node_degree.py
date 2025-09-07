import matplotlib.pyplot as plt
import pandas as pd
import numpy as np



folders = ["RESULTS/500000/randomNetwork/0,80/","RESULTS/500000/randomNetwork/0,20/","RESULTS/500000/randomNetwork/-0,40/",
           "RESULTS/500000/BA/0,80/","RESULTS/500000/BA/0,20/","RESULTS/500000/BA/-0,40/"]

for f in folders:
    def folder(i):
        return f"{f}{i}_steps.txt"
    
    
    topologia = "grafu przypadkowego" if "randomNetwork" in f else "sieci BA"
    df = pd.read_csv(folder(1), sep="\t")
    for i in range(2, 11):
        df1 = pd.read_csv(folder(i), sep="\t")
        df = pd.concat([df, df1], ignore_index=True)

    # Chcemy analizować tylko stopnie węzłów
    node_degrees = df['node_degree']

    # Jeśli są tylko 1-2 różne stopnie, to nie ma sensu rysować
    if node_degrees.nunique() < 2 or len(node_degrees) < 10:
        print(f"Za mało danych dla stopni węzłów w folderze {f}")
        continue

    plt.figure(figsize=(10, 7))

    min_val = node_degrees.min()
    max_val = node_degrees.max()

    # Automatyczna szerokość binów dla stopni węzłów
    bins = np.arange(min_val, max_val + 2) - 0.5  # dla integerowych danych

    counts, bin_edges = np.histogram(node_degrees, bins=bins, density=True)
    bin_centers = (bin_edges[:-1] + bin_edges[1:]) / 2

    # Rysowanie wykresu
    plt.scatter(bin_centers[counts != 0], counts[counts != 0], alpha=0.75 )
    plt.yscale('log')
    plt.xscale('log')
    plt.xlabel('Stopień węzła', fontsize=14)
    plt.ylabel('Gęstość prawdopodobieństwa', fontsize=14)
    plt.xticks(fontsize=12)
    plt.yticks(fontsize=12)

    plt.xlim(1, max_val + 1)
    plt.title('Dystrybucja stopni węzłów ' + topologia, fontsize=16)
    plt.grid(True)
    plt.tight_layout()
    plt.savefig(f + "plots/dystrybucja_" + topologia + ".png")
    plt.show()
