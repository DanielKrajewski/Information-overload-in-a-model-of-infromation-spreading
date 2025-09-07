import matplotlib.pyplot as plt
import pandas as pd
import numpy as np

folders = ["RESULTS0,00/500000/randomNetwork/0,80/","RESULTS0,00/500000/randomNetwork/0,20/","RESULTS0,00/500000/randomNetwork/-0,40/","RESULTS0,00/500000/BA/0,80/",
           "RESULTS0,00/500000/BA/0,20/","RESULTS0,00/500000/BA/-0,40/"]
folders2=["RESULTS0,40/500000/randomNetwork/0,80/","RESULTS0,40/500000/randomNetwork/0,20/","RESULTS0,40/500000/randomNetwork/-0,40/","RESULTS0,40/500000/BA/0,80/",
           "RESULTS0,40/500000/BA/0,20/","RESULTS0,40/500000/BA/-0,40/"]
folders3=["RESULTS0,80/500000/randomNetwork/0,80/","RESULTS0,80/500000/randomNetwork/0,20/","RESULTS0,80/500000/randomNetwork/-0,40/","RESULTS0,80/500000/BA/0,80/",
           "RESULTS0,80/500000/BA/0,20/","RESULTS0,80/500000/BA/-0,40/"]
folders4=["RESULTS0,20/500000/randomNetwork/0,80/","RESULTS0,20/500000/randomNetwork/0,20/","RESULTS0,20/500000/randomNetwork/-0,40/","RESULTS0,20/500000/BA/0,80/",
           "RESULTS0,20/500000/BA/0,20/","RESULTS0,20/500000/BA/-0,40/"]
folders60=["RESULTS600,00/500000/randomNetwork/0,80/","RESULTS600,00/500000/randomNetwork/0,20/","RESULTS600,00/500000/randomNetwork/-0,40/","RESULTS600,00/500000/BA/0,80/",
           "RESULTS600,00/500000/BA/0,20/","RESULTS600,00/500000/BA/-0,40/"]
folders6000=["RESULTS60000,00/5000000/BA/0,80/",
           "RESULTS60000,00/5000000/BA/0,20/","RESULTS60000,00/5000000/BA/-0,40/"]

for f in folders6000:
    def folder(i):
        return  f"{f}{i}_messages.txt"
    topologia = "graf przypadkowy" if "randomNetwork" in f else "sieć BA"
    threshold = f.split('/')[3]  # Pobieramy próg z nazwy folderu
    similarity = f.split('/')[0]  # Pobieramy podobieństwo z nazwy folderu
    similarity = similarity.split('RESULTS')[1]

    df = pd.read_csv(folder(1), sep="\t")
    for i in range (2,11):
        df1 = pd.read_csv(folder(i), sep="\t")
        df = pd.concat([df, df1], ignore_index=True)



    # Grupowanie po ilości repetycji i obliczanie średniej oraz odchylenia standardowego długości wiadomości
    #array2 = np.linspace(0, 9, num=10)
    array=np.logspace(0,3, num=30)
    array[0] = 0
    #array2[0] = 0

    #concatenate arrays
    #array = np.concatenate((array2, array), axis=None)

    df['repetitions'] = pd.cut(df['repetitions'], bins=array, labels=array[:-1])
    df['repetitions'] = df['repetitions'].astype(float)+1
    agg_df = df.groupby('repetitions')['length'].agg(['mean', 'std']).reset_index()

    counts,bin_edges = np.histogram(df['repetitions'], bins=array, density=True)


    fig, ax1 = plt.subplots(figsize=(8, 6))

    # Wykres 1: gęstość prawdopodobieństwa (lewa oś)
    ax1.set_xlabel('Liczba udostępnień wiadomości',fontsize=16)
    ax1.set_ylabel('Gęstość prawdopodobieństwa', color='tab:red',fontsize=16)
    ax1.set_xscale('log')
    ax1.set_yscale('log')
    ax1.set_xlim(0, 1000)
    ax1.set_ylim(1e-12, 1)
    ax1.grid(True)
    ax1.tick_params(axis='x',labelsize=16)

    # Gwiazdki dla gęstości
    ax1.plot(bin_edges[:-1][counts != 0] + 1, counts[counts != 0], '*', color='tab:red',markersize=10, label='Probability density')
    ax1.tick_params(axis='y', labelcolor='tab:red',labelsize=16)

    # Druga oś Y dla długości wiadomości
    ax2 = ax1.twinx()
    ax2.set_ylabel('Długość wiadomości', color='tab:blue',fontsize=16)
    ax2.errorbar(agg_df['repetitions'], agg_df['mean'], yerr=agg_df['std'],
                fmt='o', color='tab:blue', capsize=5, capthick=2, ecolor='tab:blue',markersize=5, alpha=0.7, label='Mean message length')
    ax2.tick_params(axis='y', labelcolor='tab:blue',labelsize=16)

    plt.title(topologia + ': τ=' +  threshold, fontsize=18)
    fig.tight_layout()
    plt.savefig(f + "plots/messages_" + topologia + "_" + threshold + "_Ising=" + similarity + ".png")
    plt.show()
