import matplotlib.pyplot as plt
import pandas as pd
import numpy as np

degrees_to_compare = [5, 10, 20]

folders = ["RESULTS/50000/randomNetwork/0,80/","RESULTS/50000/randomNetwork/0,20/","RESULTS/50000/randomNetwork/-0,40/","RESULTS/50000/BA/0,80/",
           "RESULTS/50000/BA/0,20/","RESULTS/50000/BA/-0,40/"]

for f in folders:
    def folder(i):
        return f"{f}{i}_steps.txt"
    
    df = pd.read_csv(folder(1), sep="\t")
    for i in range(2, 11):
        df1 = pd.read_csv(folder(i), sep="\t")
        df = pd.concat([df, df1], ignore_index=True)

    df.loc[df['messages_interested_amount'] > 0, 'messages_interested_amount'] -= 1

    plt.figure(figsize=(10, 7))

    for degree in degrees_to_compare:
        candidates = df[df['node_degree'] == degree]['node'].unique()
        if len(candidates) == 0:
            print(f"Brak węzłów o stopniu {degree} w folderze {f}")
            continue

        chosen_node = np.random.choice(candidates)  # losowy węzeł
        node_df = df[df['node'] == chosen_node]

        values = node_df['messages_interested_amount']
        if values.nunique() < 2:
            print(f"Za mało danych do rysowania dla node {chosen_node} o stopniu {degree}")
            continue

        bins = np.arange(values.min(), values.max() + 2) - 0.5
        counts, bin_edges = np.histogram(values, bins=bins, density=True)
        bin_centers = (bin_edges[:-1] + bin_edges[1:]) / 2

        plt.scatter(bin_centers[counts != 0], counts[counts != 0], label=f'Degree {degree} (node {chosen_node})', alpha=0.75)

    plt.yscale('log')
    plt.xlabel('Messages omitted')
    plt.ylabel('Probability density')
    plt.title('PDF of messages omitted by node degree')
    plt.grid(True)
    plt.legend()
    plt.tight_layout()
    plt.savefig(f + "plots/overload_density(k).png")
    plt.show()
