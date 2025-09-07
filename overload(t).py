import matplotlib.pyplot as plt
import pandas as pd
import numpy as np


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
        return  f"{f}{i}_steps.txt"
    
    topologia = "graf przypadkowy" if "randomNetwork" in f else "sieć BA"
    threshold = f.split('/')[3]  # Pobieramy próg z nazwy folderu
    similarity = f.split('/')[0]  # Pobieramy podobieństwo z nazwy folderu
    similarity = similarity.split('RESULTS')[1]

    df = pd.read_csv(folder(1), sep="\t")
    for i in range (2,11):
        df1 = pd.read_csv(folder(i), sep="\t")
        df = pd.concat([df, df1], ignore_index=True)
        
    df.loc[df['messages_interested_amount'] > 0, 'messages_read'] = 1
    df.loc[df['messages_interested_amount'] == 0, 'messages_read'] = 0
    

    #minus one for values > 0 in column messages_interested_amount
    df.loc[df['messages_interested_amount'] > 0, 'messages_interested_amount'] -= 1

    array = np.linspace(0,500000, num=50)
    df['step'] = pd.cut(df['step'], bins=array, labels=array[:-1])
    df['step'] = df['step'].astype(float)


    agg_df2 = df.groupby('step')['messages_read'].agg(['mean', 'std']).reset_index()
    agg_df = df.groupby('step')['messages_interested_amount'].agg(['mean', 'std']).reset_index()

    # Tworzenie wykresu
    plt.figure(figsize=(8, 6))
    plt.errorbar(agg_df['step'], agg_df['mean'], yerr=agg_df['std'], fmt='o', color='b', ecolor='b', capsize=5,capthick=2,alpha=0.8, label='Wiadomości ominięte')
    plt.errorbar(agg_df2['step'], agg_df2['mean'], yerr=agg_df2['std'], fmt='o', color='g', ecolor='g',capsize=5, capthick=2,alpha=0.8, label='Wiadomości przeczytane')
    plt.legend(loc='upper right',fontsize=12)
    plt.grid(True)
    plt.xlabel('Krok czasowy',fontsize=16)
    plt.ylabel('Średnia liczba wiadomości',fontsize=16)
    plt.tick_params(axis='both', labelsize=16)
    plt.title(topologia + ': τ=' +  threshold,fontsize=18) 
    plt.savefig(f + "plots/overload(t)_" + topologia + "_" + threshold + "_Ising=" + similarity + ".png")
    plt.show()



