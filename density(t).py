import matplotlib.pyplot as plt
import pandas as pd
import numpy as np

target_bins = 30  # <-- ile binów chcemy docelowo
folders = ["RESULTS0,00/500000/randomNetwork/0,80/","RESULTS0,00/500000/randomNetwork/0,20/","RESULTS0,00/500000/randomNetwork/-0,40/","RESULTS0,00/500000/BA/0,80/",
           "RESULTS0,00/500000/BA/0,20/","RESULTS0,00/500000/BA/-0,40/"]
folders3=["RESULTS0,40/500000/randomNetwork/0,80/","RESULTS0,40/500000/randomNetwork/0,20/","RESULTS0,40/500000/randomNetwork/-0,40/","RESULTS0,40/500000/BA/0,80/",
           "RESULTS0,40/500000/BA/0,20/","RESULTS0,40/500000/BA/-0,40/"]
folders4=["RESULTS0,80/500000/randomNetwork/0,80/","RESULTS0,80/500000/randomNetwork/0,20/","RESULTS0,80/500000/randomNetwork/-0,40/","RESULTS0,80/500000/BA/0,80/",
           "RESULTS0,80/500000/BA/0,20/","RESULTS0,80/500000/BA/-0,40/"]
folders2=["RESULTS0,20/500000/randomNetwork/0,80/","RESULTS0,20/500000/randomNetwork/0,20/","RESULTS0,20/500000/randomNetwork/-0,40/","RESULTS0,20/500000/BA/0,80/",
           "RESULTS0,20/500000/BA/0,20/","RESULTS0,20/500000/BA/-0,40/"]

for f in folders4:
    def folder(i):
        return f"{f}{i}_steps.txt"
    
    topologia = "graf przypadkowy" if "randomNetwork" in f else "sieć BA"
    threshold = f.split('/')[3]  # Pobieramy próg z nazwy folderu
    similarity = f.split('/')[0]  # Pobieramy podobieństwo z nazwy folderu
    similarity = similarity.split('RESULTS')[1]

    df = pd.read_csv(folder(1), sep="\t")
    for i in range(2, 11):
        df1 = pd.read_csv(folder(i), sep="\t")
        df = pd.concat([df, df1], ignore_index=True)

    # Korekta
    df.loc[df['messages_interested_amount'] > 0, 'messages_interested_amount'] -= 1

    # Histogram z gęstością
    values = df['messages_interested_amount']
    #bins = np.arange(values.min(), values.max() + 2) - 0.5  # dla integerowych danych
    min_val = values.min()
    max_val = values.max()
    bin_width = max(1, (max_val - min_val) // target_bins)
    bins = np.arange(min_val, max_val + bin_width + 1, bin_width)
    counts, bin_edges = np.histogram(values, bins=bins, density=True)

    # Środek binów
    bin_centers = (bin_edges[:-1] + bin_edges[1:]) / 2

    # Scatter
    plt.figure(figsize=(8,6))
    plt.scatter(bin_centers[counts != 0], counts[counts != 0], alpha=0.8)

    plt.yscale('log')
    plt.xscale('log')
    plt.xlabel('Liczba ominiętych wiadomości',fontsize=16)
    plt.ylabel('Gęstość prawdopodobieństwa',fontsize=16)
    plt.tick_params(axis='both', labelsize=16)
    plt.title(topologia + ': τ=' +  threshold,fontsize=18)
    plt.grid(True)
    plt.tight_layout()
    plt.savefig(f + "plots/overload_density(t)_" + topologia + "_" + threshold + "_Ising=" + similarity + ".png")
    plt.show()
