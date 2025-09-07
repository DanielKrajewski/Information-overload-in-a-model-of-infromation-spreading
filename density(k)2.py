import matplotlib.pyplot as plt
import pandas as pd
import numpy as np

degrees_to_compare = [5, 10, 20, 50]
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

    
    df.loc[df['messages_interested_amount'] > 0, 'messages_interested_amount'] -= 1

    plt.figure(figsize=(8, 6))

    for degree in degrees_to_compare:
        subset = df[df['node_degree'] == degree]
        values = subset['messages_interested_amount']

        if values.nunique() < 2 or len(values) < 10:
            print(f"Za mało danych dla stopnia {degree} w folderze {f}")
            continue

        min_val = values.min()
        max_val = values.max()

        # Automatyczna szerokość binów
        bin_width = max(1, (max_val - min_val) // target_bins)
        #bins = np.arange(values.min(), values.max() + 2) - 0.5
        bins = np.arange(min_val, max_val + bin_width + 1, bin_width)

        counts, bin_edges = np.histogram(values, bins=bins, density=True)
        bin_centers = (bin_edges[:-1] + bin_edges[1:]) / 2

        plt.scatter(bin_centers[counts != 0], counts[counts != 0], label=f'Degree {degree}', alpha=0.75)



    plt.yscale('log')
    plt.xlabel('Liczba ominiętych wiadomości',fontsize=16)
    plt.ylabel('Gęstość prawdopodobieństwa',fontsize=16)
    plt.tick_params(axis='both', labelsize=16)
    #plt.title('Probability density of messages omitted by node degree',fontsize=16)
    plt.title(topologia + ': τ=' +  threshold,fontsize=18)
    plt.grid(True)
    plt.legend()
    plt.tight_layout()
    plt.savefig(f + "plots/overload_density(k)_" + topologia + "_" + threshold + "_Ising=" + similarity + ".png")
    #plt.show()
